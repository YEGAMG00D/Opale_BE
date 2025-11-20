package yegam.opale_be.domain.recommendation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.analytics.entity.UserEventLog;
import yegam.opale_be.domain.analytics.repository.UserEventLogRepository;
import yegam.opale_be.domain.chat.room.entity.ChatRoom;
import yegam.opale_be.domain.chat.room.repository.ChatRoomRepository;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.culture.performance.exception.PerformanceErrorCode;
import yegam.opale_be.domain.culture.performance.repository.PerformanceRepository;
import yegam.opale_be.domain.place.entity.Place;
import yegam.opale_be.domain.place.repository.PlaceRepository;
import yegam.opale_be.domain.preference.entity.UserPreferenceVector;
import yegam.opale_be.domain.preference.repository.UserPreferenceVectorRepository;
import yegam.opale_be.domain.preference.util.ZeroVectorUtil;
import yegam.opale_be.domain.recommendation.dto.response.*;
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
  private final UserEventLogRepository userEventLogRepository;


  private final RecommendationMapper recommendationMapper;
  private final EmbeddingVectorUtil embeddingVectorUtil;
  private final PineconeClientUtil pineconeClientUtil;

  private final ZeroVectorUtil zeroVectorUtil; // ⭐ cold-start helper

  // ================================
  // common helpers
  // ================================
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
      List<Double> vector, int size, String sort
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

    List<String> ids = matches.stream().map(PineconeMatch::getId).toList();

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
      case "latest" ->
          dtoList.sort(Comparator.comparing(
              (RecommendedPerformanceDto d) -> d.getStartDate(),
              Comparator.nullsLast(Comparator.naturalOrder())
          ).reversed());

      case "popularity" ->
          dtoList.sort(Comparator.comparing(
              (RecommendedPerformanceDto d) -> d.getRating() != null ? d.getRating() : 0.0
          ).reversed());

      // similarity / auto → Pinecone score 유지
    }

    return RecommendationPerformanceListResponseDto.builder()
        .totalCount(dtoList.size())
        .requestedSize(topK)
        .sort(normalizedSort)
        .recommendations(dtoList)
        .build();
  }

  // ================================
  // 1) Personalized Recommendation
  // ================================
  @Transactional  // ⭐ save 안 하지만 read-only false (future safe)
  public RecommendationPerformanceListResponseDto getUserRecommendations(Long userId, Integer size, String sort) {

    UserPreferenceVector vec = preferenceRepository.findById(userId).orElse(null);

    List<Double> vector;
    if (vec == null) {
      // ⭐ 벡터 없으면 Zero Vector 사용
      vector = zeroVectorUtil.generateZeroVector();
      log.info("⭐ Cold-start user → zero vector 사용 (userId={})", userId);
    } else {
      vector = embeddingVectorUtil.parseToList(vec.getEmbeddingVector());
    }

    return buildVectorBasedRecommendation(vector, size, sort);
  }

  // ================================
  // 2) Personalized (Admin Tool)
  // ================================
  @Transactional
  public RecommendationPerformanceListResponseDto getUserRecommendationsByAdmin(Long userId, Integer size, String sort) {

    UserPreferenceVector vec = preferenceRepository.findById(userId).orElse(null);

    List<Double> vector;
    if (vec == null) {
      vector = zeroVectorUtil.generateZeroVector();
      log.info("⭐ Admin cold-start user → zero vector 사용 (userId={})", userId);
    } else {
      vector = embeddingVectorUtil.parseToList(vec.getEmbeddingVector());
    }

    return buildVectorBasedRecommendation(vector, size, sort);
  }

  // ================================
  // 3) Similarity
  // ================================
  public RecommendationPerformanceListResponseDto getSimilarPerformances(String performanceId, Integer size, String sort) {

    Performance p = performanceRepository.findById(performanceId)
        .orElseThrow(() -> new CustomException(PerformanceErrorCode.PERFORMANCE_NOT_FOUND));

    if (p.getEmbeddingVector() == null || p.getEmbeddingVector().isBlank()) {
      throw new CustomException(RecommendationErrorCode.PERFORMANCE_VECTOR_NOT_FOUND);
    }

    List<Double> vector = embeddingVectorUtil.parseToList(p.getEmbeddingVector());
    RecommendationPerformanceListResponseDto result = buildVectorBasedRecommendation(vector, size, sort);

    // 동일 공연 제외
    List<RecommendedPerformanceDto> filtered = result.getRecommendations().stream()
        .filter(dto -> !performanceId.equals(dto.getPerformanceId()))
        .toList();

    result.setRecommendations(filtered);
    result.setTotalCount(filtered.size());
    return result;
  }

  // ================================
  // 4) Genre
  // ================================
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
        .toList();

    return RecommendationPerformanceListResponseDto.builder()
        .totalCount(dtoList.size())
        .requestedSize(limit)
        .sort(normalizedSort)
        .recommendations(dtoList)
        .build();
  }

  // ================================
  // 5) Popular
  // ================================
  public RecommendationPerformanceListResponseDto getPopularRecommendations(Integer size) {

    int limit = normalizeSize(size);
    PageRequest pageable = PageRequest.of(0, limit);

    List<Performance> list = performanceRepository.findPopularPerformances(pageable);

    List<RecommendedPerformanceDto> dtoList =
        list.stream().map(p -> recommendationMapper.toPerformance(p, null)).toList();

    return RecommendationPerformanceListResponseDto.builder()
        .totalCount(dtoList.size())
        .requestedSize(limit)
        .sort("popularity")
        .recommendations(dtoList)
        .build();
  }

  // ================================
  // 6) Latest
  // ================================
  public RecommendationPerformanceListResponseDto getLatestRecommendations(Integer size) {

    int limit = normalizeSize(size);
    PageRequest pageable = PageRequest.of(0, limit);

    List<Performance> list = performanceRepository.findLatestPerformances(pageable);

    List<RecommendedPerformanceDto> dtoList =
        list.stream().map(p -> recommendationMapper.toPerformance(p, null)).toList();

    return RecommendationPerformanceListResponseDto.builder()
        .totalCount(dtoList.size())
        .requestedSize(limit)
        .sort("latest")
        .recommendations(dtoList)
        .build();
  }

  // ================================
  // 7) Popular Places
  // ================================
  public RecommendationPlaceListResponseDto getPopularPlaces(Integer size) {

    int limit = normalizeSize(size);
    PageRequest pageable = PageRequest.of(0, limit);

    List<Place> list = placeRepository.findPopularPlaces(pageable);

    List<RecommendedPlaceDto> dtoList = list.stream()
        .map(recommendationMapper::toPlace)
        .toList();

    return RecommendationPlaceListResponseDto.builder()
        .totalCount(dtoList.size())
        .requestedSize(limit)
        .sort("popularity")
        .recommendations(dtoList)
        .build();
  }

  // ================================
  // 8) Popular Chat Rooms
  // ================================
  public RecommendationChatRoomListResponseDto getPopularChatRooms(Integer size) {

    int limit = normalizeSize(size);
    PageRequest pageable = PageRequest.of(0, limit);

    List<ChatRoom> list = chatRoomRepository.findPopularChatRooms(pageable);

    List<RecommendedChatRoomDto> dtoList = list.stream()
        .map(recommendationMapper::toChatRoom)
        .toList();

    return RecommendationChatRoomListResponseDto.builder()
        .totalCount(dtoList.size())
        .requestedSize(limit)
        .sort("popularity")
        .recommendations(dtoList)
        .build();
  }


  /* ================================
   최근 본 공연 1개 조회
   ================================ */
  public String getRecentViewedPerformance(Long userId) {

    // 최근 VIEW 로그 1개 가져오기
    var log = userEventLogRepository.findTopByUser_UserIdAndEventTypeOrderByCreatedAtDesc(
        userId, UserEventLog.EventType.VIEW
    ).orElse(null);

    if (log == null || !"PERFORMANCE".equalsIgnoreCase(log.getTargetType().name())) {
      throw new CustomException(RecommendationErrorCode.RECENT_PERFORMANCE_NOT_FOUND);
    }

    return log.getTargetId(); // 여기가 performanceId
  }


  /* ================================
     최근 본 공연 기반 추천
     ================================ */
  public RecommendationPerformanceListResponseDto getRecentSimilarRecommendations(
      Long userId, Integer size, String sort
  ) {
    // 1) 최근 본 공연 ID 찾기
    String recentPerformanceId = getRecentViewedPerformance(userId);

    // 2) 기존 "비슷한 공연 추천" 로직 재사용
    return getSimilarPerformances(recentPerformanceId, size, sort);
  }




}
