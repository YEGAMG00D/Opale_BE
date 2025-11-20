package yegam.opale_be.domain.preference.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.analytics.entity.UserEventLog;
import yegam.opale_be.domain.analytics.repository.UserEventLogRepository;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.culture.performance.repository.PerformanceRepository;
import yegam.opale_be.domain.preference.entity.UserPreferenceVector;
import yegam.opale_be.domain.preference.repository.UserPreferenceVectorRepository;
import yegam.opale_be.domain.preference.util.VectorEmbeddingAggregator;
import yegam.opale_be.domain.recommendation.util.EmbeddingVectorUtil;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.domain.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreferenceBatchService {

  private static final int RECENT_DAYS = 90;

  private final UserEventLogRepository eventLogRepository;
  private final UserPreferenceVectorRepository vectorRepository;
  private final PerformanceRepository performanceRepository;
  private final UserRepository userRepository;

  private final VectorEmbeddingAggregator vectorEmbeddingAggregator;
  private final EmbeddingVectorUtil embeddingVectorUtil;
  private final ObjectMapper objectMapper;

  /** 전체 유저 벡터 업데이트 */
  @Transactional
  public void updateAllUserVectors() {
    List<User> users = userRepository.findAll();
    log.info("🚀 전체 유저 벡터 업데이트 시작 — {}명", users.size());

    int success = 0;
    for (User user : users) {
      try {
        updateSingleUserVector(user.getUserId());
        success++;
      } catch (Exception e) {
        log.error("❌ 벡터 업데이트 실패: userId={}", user.getUserId(), e);
      }
    }

    log.info("🎉 전체 벡터 업데이트 완료 — 성공 {}/{}", success, users.size());
  }

  /** 특정 유저 벡터 업데이트 */
  @Transactional
  public void updateSingleUserVector(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

    // 최근 로그 N일
    LocalDateTime from = LocalDateTime.now().minusDays(RECENT_DAYS);
    List<UserEventLog> logs = eventLogRepository.findRecentLogs(userId, from);

    // PERFORMANCE 대상만 추출
    Set<String> performanceIds = logs.stream()
        .filter(log -> log.getTargetType() == UserEventLog.TargetType.PERFORMANCE)
        .map(UserEventLog::getTargetId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    // 공연 임베딩 로딩
    Map<String, List<Double>> embeddingMap = new HashMap<>();
    if (!performanceIds.isEmpty()) {
      List<Performance> performances = performanceRepository.findByPerformanceIdIn(
          new ArrayList<>(performanceIds));

      for (Performance p : performances) {
        try {
          List<Double> vec = embeddingVectorUtil.parseToList(p.getEmbeddingVector());
          if (vec.size() != VectorEmbeddingAggregator.VECTOR_DIM) continue;
          embeddingMap.put(p.getPerformanceId(), vec);
        } catch (Exception ignored) {}
      }
    }

    // 사용자 벡터 생성
    List<Double> userVector = vectorEmbeddingAggregator.buildUserEmbeddingVector(logs, embeddingMap);

    // JSON 직렬화
    String vectorJson;
    try {
      vectorJson = objectMapper.writeValueAsString(userVector);
    } catch (JsonProcessingException e) {
      return;
    }

    // 있음 → 업데이트, 없음 → 생성
    UserPreferenceVector entity = vectorRepository.findById(userId)
        .orElseGet(() -> {
          UserPreferenceVector v = new UserPreferenceVector();
          v.setUser(user);  // MapsId → PK 자동 설정
          return v;
        });

    entity.setEmbeddingVector(vectorJson);
    vectorRepository.save(entity);
  }
}
