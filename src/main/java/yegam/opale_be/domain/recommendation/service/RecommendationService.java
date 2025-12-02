package yegam.opale_be.domain.recommendation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
import java.util.concurrent.TimeUnit;
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
  private final ZeroVectorUtil zeroVectorUtil;

  /** ✅ Redis */
  private final StringRedisTemplate redisTemplate;

  /** ✅ LocalDate 직렬화 대응 */
  private final ObjectMapper objectMapper = new ObjectMapper()
      .registerModule(new JavaTimeModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

  private static final long POPULAR_CACHE_TTL_MINUTES = 30;
  private static final String POPULAR_CACHE_KEY = "recommendation:popular";
  private static final String GENRE_CACHE_KEY_PREFIX = "recommendation:genre:";

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

    if ("latest".equals(normalizedSort)) {
      dtoList.sort(Comparator.comparing(
          (RecommendedPerformanceDto d) -> d.getStartDate(),
          Comparator.nullsLast(Comparator.naturalOrder())
      ).reversed());
    }

    return RecommendationPerformanceListResponseDto.builder()
        .totalCount(dtoList.size())
        .requestedSize(topK)
        .sort(normalizedSort)
        .recommendations(dtoList)
        .build();
  }

  // ================================
  // 1) 개인화 추천 (✅ Controller와 일치)
  // ================================
  @Transactional
  public RecommendationPerformanceListResponseDto getUserRecommendations(
      Long userId, Integer size, String sort
  ) {
    UserPreferenceVector vec = preferenceRepository.findById(userId).orElse(null);

    List<Double> vector;
    if (vec == null) {
      vector = zeroVectorUtil.generateZeroVector();
    } else {
      vector = embeddingVectorUtil.parseToList(vec.getEmbeddingVector());
    }

    return buildVectorBasedRecommendation(vector, size, sort);
  }

  // ================================
  // 2) 운영자용 개인화 추천
  // ================================
  public RecommendationPerformanceListResponseDto getUserRecommendationsByAdmin(
      Long userId, Integer size, String sort
  ) {
    return getUserRecommendations(userId, size, sort);
  }

  // ================================
  // 3) 유사 공연
  // ================================
  public RecommendationPerformanceListResponseDto getSimilarPerformances(
      String performanceId, Integer size, String sort
  ) {
    Performance p = performanceRepository.findById(performanceId)
        .orElseThrow(() -> new CustomException(PerformanceErrorCode.PERFORMANCE_NOT_FOUND));

    if (p.getEmbeddingVector() == null || p.getEmbeddingVector().isBlank()) {
      throw new CustomException(RecommendationErrorCode.PERFORMANCE_VECTOR_NOT_FOUND);
    }

    List<Double> vector = embeddingVectorUtil.parseToList(p.getEmbeddingVector());
    RecommendationPerformanceListResponseDto result =
        buildVectorBasedRecommendation(vector, size, sort);

    List<RecommendedPerformanceDto> filtered = result.getRecommendations().stream()
        .filter(dto -> !performanceId.equals(dto.getPerformanceId()))
        .toList();

    result.setRecommendations(filtered);
    result.setTotalCount(filtered.size());
    return result;
  }

  // ================================
  // 4) 장르 기반 추천 (✅ Redis)
  // ================================
  public RecommendationPerformanceListResponseDto getGenreRecommendations(
      String genre, Integer size, String sort
  ) {
    String cacheKey = GENRE_CACHE_KEY_PREFIX + genre;

    try {
      String cached = redisTemplate.opsForValue().get(cacheKey);
      if (cached != null) {
        return objectMapper.readValue(cached, RecommendationPerformanceListResponseDto.class);
      }
    } catch (Exception e) {
      log.warn("⚠️ Genre Redis 캐시 파싱 실패");
    }

    String normalizedSort = normalizeSort(sort);
    int limit = normalizeSize(size);
    PageRequest pageable = PageRequest.of(0, limit);

    List<Performance> performances =
        "latest".equals(normalizedSort)
            ? performanceRepository.findLatestByGenre(genre, pageable)
            : performanceRepository.findPopularByGenre(genre, pageable);

    List<RecommendedPerformanceDto> dtoList =
        performances.stream().map(p -> recommendationMapper.toPerformance(p, null)).toList();

    RecommendationPerformanceListResponseDto response =
        RecommendationPerformanceListResponseDto.builder()
            .totalCount(dtoList.size())
            .requestedSize(limit)
            .sort(normalizedSort)
            .recommendations(dtoList)
            .build();

    try {
      redisTemplate.opsForValue().set(
          cacheKey,
          objectMapper.writeValueAsString(response),
          POPULAR_CACHE_TTL_MINUTES,
          TimeUnit.MINUTES
      );
    } catch (Exception e) {
      log.warn("⚠️ Genre Redis 저장 실패");
    }

    return response;
  }

  // ================================
  // 5) 인기 공연 (✅ Redis)
  // ================================
  public RecommendationPerformanceListResponseDto getPopularRecommendations(Integer size) {

    try {
      String cached = redisTemplate.opsForValue().get(POPULAR_CACHE_KEY);
      if (cached != null) {
        return objectMapper.readValue(cached, RecommendationPerformanceListResponseDto.class);
      }
    } catch (Exception e) {
      log.warn("⚠️ Popular Redis 캐시 파싱 실패");
    }

    int limit = normalizeSize(size);
    PageRequest pageable = PageRequest.of(0, limit);

    List<Performance> list = performanceRepository.findPopularPerformances(pageable);

    List<RecommendedPerformanceDto> dtoList =
        list.stream().map(p -> recommendationMapper.toPerformance(p, null)).toList();

    RecommendationPerformanceListResponseDto response =
        RecommendationPerformanceListResponseDto.builder()
            .totalCount(dtoList.size())
            .requestedSize(limit)
            .sort("popularity")
            .recommendations(dtoList)
            .build();

    try {
      redisTemplate.opsForValue().set(
          POPULAR_CACHE_KEY,
          objectMapper.writeValueAsString(response),
          POPULAR_CACHE_TTL_MINUTES,
          TimeUnit.MINUTES
      );
    } catch (Exception e) {
      log.warn("⚠️ Popular Redis 저장 실패");
    }

    return response;
  }

  // ================================
  // 6) 최신 공연
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
  // 7) 인기 공연장
  // ================================
  public RecommendationPlaceListResponseDto getPopularPlaces(Integer size) {

    int limit = normalizeSize(size);
    PageRequest pageable = PageRequest.of(0, limit);

    List<Place> list = placeRepository.findPopularPlaces(pageable);

    List<RecommendedPlaceDto> dtoList =
        list.stream().map(recommendationMapper::toPlace).toList();

    return RecommendationPlaceListResponseDto.builder()
        .totalCount(dtoList.size())
        .requestedSize(limit)
        .sort("popularity")
        .recommendations(dtoList)
        .build();
  }

  // ================================
  // 8) 인기 채팅방
  // ================================
  public RecommendationChatRoomListResponseDto getPopularChatRooms(Integer size) {

    int limit = normalizeSize(size);
    PageRequest pageable = PageRequest.of(0, limit);

    List<ChatRoom> list = chatRoomRepository.findPopularChatRooms(pageable);

    List<RecommendedChatRoomDto> dtoList =
        list.stream().map(recommendationMapper::toChatRoom).toList();

    return RecommendationChatRoomListResponseDto.builder()
        .totalCount(dtoList.size())
        .requestedSize(limit)
        .sort("popularity")
        .recommendations(dtoList)
        .build();
  }

  // ================================
  // 9) 최근 본 공연
  // ================================
  public String getRecentViewedPerformance(Long userId) {

    var log = userEventLogRepository
        .findTopByUser_UserIdAndEventTypeOrderByCreatedAtDesc(
            userId, UserEventLog.EventType.VIEW
        ).orElse(null);

    if (log == null || !"PERFORMANCE".equalsIgnoreCase(log.getTargetType().name())) {
      throw new CustomException(RecommendationErrorCode.RECENT_PERFORMANCE_NOT_FOUND);
    }

    return log.getTargetId();
  }

  // ================================
  // 10) 최근 기반 추천
  // ================================
  public RecommendationPerformanceListResponseDto getRecentSimilarRecommendations(
      Long userId, Integer size, String sort
  ) {
    String recentPerformanceId = getRecentViewedPerformance(userId);
    return getSimilarPerformances(recentPerformanceId, size, sort);
  }

}
