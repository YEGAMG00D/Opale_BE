package yegam.opale_be.domain.recommendation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.chat.room.entity.ChatRoom;
import yegam.opale_be.domain.chat.room.repository.ChatRoomRepository;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.culture.performance.exception.PerformanceErrorCode;
import yegam.opale_be.domain.culture.performance.repository.PerformanceRepository;
import yegam.opale_be.domain.place.entity.Place;
import yegam.opale_be.domain.place.repository.PlaceRepository;
import yegam.opale_be.domain.preference.entity.UserPreferenceVector;
import yegam.opale_be.domain.preference.repository.UserPreferenceVectorRepository;
import yegam.opale_be.domain.recommendation.dto.response.RecommendationChatRoomListResponseDto;
import yegam.opale_be.domain.recommendation.dto.response.RecommendationPerformanceListResponseDto;
import yegam.opale_be.domain.recommendation.dto.response.RecommendationPlaceListResponseDto;
import yegam.opale_be.domain.recommendation.dto.response.RecommendedChatRoomDto;
import yegam.opale_be.domain.recommendation.dto.response.RecommendedPerformanceDto;
import yegam.opale_be.domain.recommendation.dto.response.RecommendedPlaceDto;
import yegam.opale_be.domain.recommendation.exception.RecommendationErrorCode;
import yegam.opale_be.domain.recommendation.mapper.RecommendationMapper;
import yegam.opale_be.domain.recommendation.util.EmbeddingVectorUtil;
import yegam.opale_be.domain.recommendation.util.PineconeClientUtil;
import yegam.opale_be.domain.recommendation.util.PineconeMatch;
import yegam.opale_be.global.exception.CustomException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

  private final UserPreferenceVectorRepository preferenceRepository;
  private final PerformanceRepository performanceRepository;
  private final PlaceRepository placeRepository;
  private final ChatRoomRepository chatRoomRepository;

  private final RecommendationMapper recommendationMapper;
  private final EmbeddingVectorUtil embeddingVectorUtil;
  private final PineconeClientUtil pineconeClientUtil;

  // ----------------------------------------------------------------------
  // 공통 헬퍼
  // ----------------------------------------------------------------------

  private String normalizeSort(String sort) {
    if (sort == null || sort.isBlank()) return "auto";
    return sort.toLowerCase();
  }

  private int normalizeSize(Integer size) {
    if (size == null || size <= 0) return 10;
    if (size > 50) return 50;
    return size;
  }

  private RecommendationPerformanceListResponseDto buildVectorBasedRecommendation(
      List<Double> vector,
      int size,
      String sort
  ) {
    String normalizedSort = normalizeSort(sort);
    int topK = normalizeSize(size);

    List<PineconeMatch> matches = pineconeClientUtil.query(vector, topK);

    if (matches.isEmpty()) {
      return RecommendationPerformanceListResponseDto.builder()
          .totalCount(0)
          .requestedSize(topK)
          .sort(normalizedSort)
          .recommendations(List.of())
          .build();
    }

    List<String> ids = matches.stream()
        .map(PineconeMatch::getId)
        .toList();

    Map<String, Double> scoreMap = matches.stream()
        .collect(Collectors.toMap(PineconeMatch::getId, PineconeMatch::getScore));

    List<Performance> performances = performanceRepository.findByPerformanceIdIn(ids);

    Map<String, Performance> performanceMap = performances.stream()
        .collect(Collectors.toMap(Performance::getPerformanceId, p -> p));

    List<RecommendedPerformanceDto> dtoList = new ArrayList<>();

    for (String id : ids) {
      Performance p = performanceMap.get(id);
      if (p == null) continue;
      Double score = scoreMap.getOrDefault(id, 0.0);
      dtoList.add(recommendationMapper.toPerformance(p, score));
    }


    switch (normalizedSort) {

      case "latest" -> dtoList.sort(
          Comparator.comparing(
              (RecommendedPerformanceDto d) -> d.getStartDate(),
              Comparator.nullsLast(Comparator.naturalOrder())
          ).reversed()
      );

      case "popularity" -> dtoList.sort(
          Comparator.comparing(
              (RecommendedPerformanceDto d) -> d.getRating() != null ? d.getRating() : 0.0
          ).reversed()
      );

      case "similarity" -> {
        // do nothing — Pinecone score 유지
      }

      case "auto" -> {
        // do nothing — Pinecone score 유지
      }

      default -> {
        // do nothing
      }
    }

    return RecommendationPerformanceListResponseDto.builder()
        .totalCount(dtoList.size())
        .requestedSize(topK)
        .sort(normalizedSort)
        .recommendations(dtoList)
        .build();
  }

  // ----------------------------------------------------------------------
  // 1) 개인화 추천 (로그인 사용자)
  // ----------------------------------------------------------------------

  public RecommendationPerformanceListResponseDto getUserRecommendations(Long userId, Integer size, String sort) {
    UserPreferenceVector vectorEntity = preferenceRepository.findById(userId)
        .orElseThrow(() -> new CustomException(RecommendationErrorCode.USER_VECTOR_NOT_FOUND));

    List<Double> vector = embeddingVectorUtil.parseToList(vectorEntity.getEmbeddingVector());
    return buildVectorBasedRecommendation(vector, size, sort);
  }

  // ----------------------------------------------------------------------
  // 2) 운영자용 개인화 추천 (userId 직접 입력)
  // ----------------------------------------------------------------------

  public RecommendationPerformanceListResponseDto getUserRecommendationsByAdmin(Long userId, Integer size, String sort) {
    UserPreferenceVector vectorEntity = preferenceRepository.findById(userId)
        .orElseThrow(() -> new CustomException(RecommendationErrorCode.USER_VECTOR_NOT_FOUND));

    List<Double> vector = embeddingVectorUtil.parseToList(vectorEntity.getEmbeddingVector());
    return buildVectorBasedRecommendation(vector, size, sort);
  }

  // ----------------------------------------------------------------------
  // 3) 특정 공연과 비슷한 공연 추천
  // ----------------------------------------------------------------------

  public RecommendationPerformanceListResponseDto getSimilarPerformances(String performanceId, Integer size, String sort) {
    Performance performance = performanceRepository.findById(performanceId)
        .orElseThrow(() -> new CustomException(PerformanceErrorCode.PERFORMANCE_NOT_FOUND));

    if (performance.getEmbeddingVector() == null || performance.getEmbeddingVector().isBlank()) {
      throw new CustomException(RecommendationErrorCode.PERFORMANCE_VECTOR_NOT_FOUND);
    }

    List<Double> vector = embeddingVectorUtil.parseToList(performance.getEmbeddingVector());
    RecommendationPerformanceListResponseDto result = buildVectorBasedRecommendation(vector, size, sort);

    List<RecommendedPerformanceDto> filtered = result.getRecommendations().stream()
        .filter(dto -> !performanceId.equals(dto.getPerformanceId()))
        .collect(Collectors.toList());

    result.setRecommendations(filtered);
    result.setTotalCount(filtered.size());
    return result;
  }

  // ----------------------------------------------------------------------
  // 4) 장르 기반 추천 (DB 기반)
  // ----------------------------------------------------------------------

  public RecommendationPerformanceListResponseDto getGenreRecommendations(String genre, Integer size, String sort) {
    String normalizedSort = normalizeSort(sort);
    int limit = normalizeSize(size);
    PageRequest pageable = PageRequest.of(0, limit);

    List<Performance> performances;

    if ("latest".equals(normalizedSort)) {
      performances = performanceRepository.findLatestByGenre(genre, pageable);
    } else {
      performances = performanceRepository.findPopularByGenre(genre, pageable);
      normalizedSort = "popularity";
    }

    List<RecommendedPerformanceDto> dtoList = performances.stream()
        .map(p -> recommendationMapper.toPerformance(p, null))
        .collect(Collectors.toList());

    return RecommendationPerformanceListResponseDto.builder()
        .totalCount(dtoList.size())
        .requestedSize(limit)
        .sort(normalizedSort)
        .recommendations(dtoList)
        .build();
  }

  // ----------------------------------------------------------------------
  // 5) 인기 기반 추천
  // ----------------------------------------------------------------------

  public RecommendationPerformanceListResponseDto getPopularRecommendations(Integer size) {
    int limit = normalizeSize(size);
    PageRequest pageable = PageRequest.of(0, limit);

    List<Performance> performances = performanceRepository.findPopularPerformances(pageable);

    List<RecommendedPerformanceDto> dtoList = performances.stream()
        .map(p -> recommendationMapper.toPerformance(p, null))
        .collect(Collectors.toList());

    return RecommendationPerformanceListResponseDto.builder()
        .totalCount(dtoList.size())
        .requestedSize(limit)
        .sort("popularity")
        .recommendations(dtoList)
        .build();
  }

  // ----------------------------------------------------------------------
  // 6) 최신 공연 추천
  // ----------------------------------------------------------------------

  public RecommendationPerformanceListResponseDto getLatestRecommendations(Integer size) {
    int limit = normalizeSize(size);
    PageRequest pageable = PageRequest.of(0, limit);

    List<Performance> performances = performanceRepository.findLatestPerformances(pageable);

    List<RecommendedPerformanceDto> dtoList = performances.stream()
        .map(p -> recommendationMapper.toPerformance(p, null))
        .collect(Collectors.toList());

    return RecommendationPerformanceListResponseDto.builder()
        .totalCount(dtoList.size())
        .requestedSize(limit)
        .sort("latest")
        .recommendations(dtoList)
        .build();
  }

  // ------------------------------------------------------
// 7) 인기 공연장 추천
// ------------------------------------------------------
  public RecommendationPlaceListResponseDto getPopularPlaces(Integer size) {
    int limit = normalizeSize(size);
    PageRequest pageable = PageRequest.of(0, limit);

    List<Place> places = placeRepository.findPopularPlaces(pageable);

    List<RecommendedPlaceDto> dtoList = places.stream()
        .map(recommendationMapper::toPlace)
        .toList();

    return RecommendationPlaceListResponseDto.builder()
        .totalCount(dtoList.size())
        .requestedSize(limit)
        .sort("popularity")
        .recommendations(dtoList)
        .build();
  }


  // ------------------------------------------------------
// 8) 인기 채팅방 추천
// ------------------------------------------------------
  public RecommendationChatRoomListResponseDto getPopularChatRooms(Integer size) {
    int limit = normalizeSize(size);
    PageRequest pageable = PageRequest.of(0, limit);

    List<ChatRoom> rooms = chatRoomRepository.findPopularChatRooms(pageable);

    List<RecommendedChatRoomDto> dtoList = rooms.stream()
        .map(recommendationMapper::toChatRoom)
        .toList();

    return RecommendationChatRoomListResponseDto.builder()
        .totalCount(dtoList.size())
        .requestedSize(limit)
        .sort("popularity")
        .recommendations(dtoList)
        .build();
  }






}
