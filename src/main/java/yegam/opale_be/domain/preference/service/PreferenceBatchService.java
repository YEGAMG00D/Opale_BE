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

  private static final int RECENT_DAYS = 90; // ✅ 최근 90일 로그만 사용

  private final UserEventLogRepository eventLogRepository;
  private final UserPreferenceVectorRepository vectorRepository;
  private final PerformanceRepository performanceRepository;
  private final UserRepository userRepository;

  private final VectorEmbeddingAggregator vectorEmbeddingAggregator;
  private final EmbeddingVectorUtil embeddingVectorUtil;
  private final ObjectMapper objectMapper;

  /** 🔥 전체 유저 벡터 일괄 업데이트 */
  @Transactional
  public void updateAllUserVectors() {
    List<User> users = userRepository.findAll();
    log.info("🚀 사용자 선호 벡터 전체 업데이트 시작 — 총 {}명", users.size());

    int success = 0;
    for (User user : users) {
      try {
        updateSingleUserVector(user.getUserId());
        success++;
      } catch (Exception e) {
        log.error("❌ 사용자 벡터 업데이트 실패: userId={}", user.getUserId(), e);
      }
    }

    log.info("🎉 사용자 선호 벡터 전체 업데이트 완료 — 성공: {}/{}", success, users.size());
  }

  /** 🔥 특정 유저 벡터 업데이트 (개별 호출용) */
  @Transactional
  public void updateSingleUserVector(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

    // 1) 최근 N일 로그 가져오기
    LocalDateTime from = LocalDateTime.now().minusDays(RECENT_DAYS);
    List<UserEventLog> logs = eventLogRepository.findRecentLogs(userId, from);

    log.debug("📊 유저 로그 수집: userId={}, logs={}", userId, logs.size());

    // 2) 로그에서 공연 ID만 뽑기 (PERFORMANCE 대상)
    Set<String> performanceIds = logs.stream()
        .filter(log -> log.getTargetType() == UserEventLog.TargetType.PERFORMANCE)
        .map(UserEventLog::getTargetId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    // 3) 해당 공연들의 embedding 불러오기
    Map<String, List<Double>> embeddingMap = new HashMap<>();
    if (!performanceIds.isEmpty()) {
      List<Performance> performances = performanceRepository.findByPerformanceIdIn(
          new ArrayList<>(performanceIds)
      );

      for (Performance p : performances) {
        String raw = p.getEmbeddingVector();
        if (raw == null || raw.isBlank()) continue;

        try {
          List<Double> vec = embeddingVectorUtil.parseToList(raw);
          // 차원 안 맞으면 스킵
          if (vec.size() != VectorEmbeddingAggregator.VECTOR_DIM) {
            log.warn("⚠ 공연 임베딩 차원 불일치: performanceId={}, size={}",
                p.getPerformanceId(), vec.size());
            continue;
          }
          embeddingMap.put(p.getPerformanceId(), vec);
        } catch (Exception e) {
          log.warn("⚠ 공연 임베딩 파싱 실패: performanceId={}", p.getPerformanceId(), e);
        }
      }
    }

    // 4) 유저 최종 임베딩 계산 (로그 없으면 0-vector)
    List<Double> userVector = vectorEmbeddingAggregator.buildUserEmbeddingVector(logs, embeddingMap);

    // 5) JSON 문자열로 직렬화
    String vectorJson;
    try {
      vectorJson = objectMapper.writeValueAsString(userVector);
    } catch (JsonProcessingException e) {
      log.error("❌ 유저 벡터 직렬화 실패: userId={}", userId, e);
      return;
    }

    // 6) upsert (있으면 업데이트, 없으면 생성)
    UserPreferenceVector vectorEntity = vectorRepository.findById(userId)
        .orElseGet(() -> {
          UserPreferenceVector v = new UserPreferenceVector();
          v.setUserId(userId);
          v.setUser(user);
          return v;
        });

    vectorEntity.setEmbeddingVector(vectorJson);
    vectorRepository.save(vectorEntity);

    log.info("✅ 유저 벡터 업데이트 완료: userId={}, dim={}, logs={}",
        userId, userVector.size(), logs.size());
  }
}
