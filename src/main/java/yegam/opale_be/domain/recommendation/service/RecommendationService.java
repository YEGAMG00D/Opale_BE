package yegam.opale_be.domain.recommendation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.culture.performance.exception.PerformanceErrorCode;
import yegam.opale_be.domain.culture.performance.repository.PerformanceRepository;
import yegam.opale_be.domain.preference.entity.UserPreferenceVector;
import yegam.opale_be.domain.preference.repository.UserPreferenceVectorRepository;
import yegam.opale_be.domain.recommendation.dto.response.RecommendationListResponseDto;
import yegam.opale_be.domain.recommendation.dto.response.RecommendedPerformanceDto;
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

  private RecommendationListResponseDto buildVectorBasedRecommendation(
      List<Double> vector,
      int size,
      String sort
  ) {
    String normalizedSort = normalizeSort(sort);
    int topK = normalizeSize(size);

    List<PineconeMatch> matches = pineconeClientUtil.query(vector, topK);

    if (matches.isEmpty()) {
      return RecommendationListResponseDto.builder()
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
      dtoList.add(recommendationMapper.toRecommendedPerformanceDto(p, score));
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

    return RecommendationListResponseDto.builder()
        .totalCount(dtoList.size())
        .requestedSize(topK)
        .sort(normalizedSort)
        .recommendations(dtoList)
        .build();
  }

  // ----------------------------------------------------------------------
  // 1) 개인화 추천 (로그인 사용자)
  // ----------------------------------------------------------------------

  public RecommendationListResponseDto getUserRecommendations(Long userId, Integer size, String sort) {
    UserPreferenceVector vectorEntity = preferenceRepository.findById(userId)
        .orElseThrow(() -> new CustomException(RecommendationErrorCode.USER_VECTOR_NOT_FOUND));

    List<Double> vector = embeddingVectorUtil.parseToList(vectorEntity.getEmbeddingVector());
    return buildVectorBasedRecommendation(vector, size, sort);
  }

  // ----------------------------------------------------------------------
  // 2) 운영자용 개인화 추천 (userId 직접 입력)
  // ----------------------------------------------------------------------

  public RecommendationListResponseDto getUserRecommendationsByAdmin(Long userId, Integer size, String sort) {
    UserPreferenceVector vectorEntity = preferenceRepository.findById(userId)
        .orElseThrow(() -> new CustomException(RecommendationErrorCode.USER_VECTOR_NOT_FOUND));

    List<Double> vector = embeddingVectorUtil.parseToList(vectorEntity.getEmbeddingVector());
    return buildVectorBasedRecommendation(vector, size, sort);
  }

  // ----------------------------------------------------------------------
  // 3) 특정 공연과 비슷한 공연 추천
  // ----------------------------------------------------------------------

  public RecommendationListResponseDto getSimilarPerformances(String performanceId, Integer size, String sort) {
    Performance performance = performanceRepository.findById(performanceId)
        .orElseThrow(() -> new CustomException(PerformanceErrorCode.PERFORMANCE_NOT_FOUND));

    if (performance.getEmbeddingVector() == null || performance.getEmbeddingVector().isBlank()) {
      throw new CustomException(RecommendationErrorCode.PERFORMANCE_VECTOR_NOT_FOUND);
    }

    List<Double> vector = embeddingVectorUtil.parseToList(performance.getEmbeddingVector());
    RecommendationListResponseDto result = buildVectorBasedRecommendation(vector, size, sort);

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

  public RecommendationListResponseDto getGenreRecommendations(String genre, Integer size, String sort) {
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
        .map(p -> recommendationMapper.toRecommendedPerformanceDto(p, null))
        .collect(Collectors.toList());

    return RecommendationListResponseDto.builder()
        .totalCount(dtoList.size())
        .requestedSize(limit)
        .sort(normalizedSort)
        .recommendations(dtoList)
        .build();
  }

  // ----------------------------------------------------------------------
  // 5) 인기 기반 추천
  // ----------------------------------------------------------------------

  public RecommendationListResponseDto getPopularRecommendations(Integer size) {
    int limit = normalizeSize(size);
    PageRequest pageable = PageRequest.of(0, limit);

    List<Performance> performances = performanceRepository.findPopularPerformances(pageable);

    List<RecommendedPerformanceDto> dtoList = performances.stream()
        .map(p -> recommendationMapper.toRecommendedPerformanceDto(p, null))
        .collect(Collectors.toList());

    return RecommendationListResponseDto.builder()
        .totalCount(dtoList.size())
        .requestedSize(limit)
        .sort("popularity")
        .recommendations(dtoList)
        .build();
  }

  // ----------------------------------------------------------------------
  // 6) 최신 공연 추천
  // ----------------------------------------------------------------------

  public RecommendationListResponseDto getLatestRecommendations(Integer size) {
    int limit = normalizeSize(size);
    PageRequest pageable = PageRequest.of(0, limit);

    List<Performance> performances = performanceRepository.findLatestPerformances(pageable);

    List<RecommendedPerformanceDto> dtoList = performances.stream()
        .map(p -> recommendationMapper.toRecommendedPerformanceDto(p, null))
        .collect(Collectors.toList());

    return RecommendationListResponseDto.builder()
        .totalCount(dtoList.size())
        .requestedSize(limit)
        .sort("latest")
        .recommendations(dtoList)
        .build();
  }
}
