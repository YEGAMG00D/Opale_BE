package yegam.opale_be.domain.preference.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import yegam.opale_be.domain.analytics.entity.UserEventLog;
import yegam.opale_be.domain.recommendation.util.EmbeddingVectorUtil;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class VectorEmbeddingAggregator {

  private final EmbeddingVectorUtil embeddingVectorUtil;

  /** ✅ 임베딩 차원 (현재 OpenAI 1536 사용 중이라고 가정) */
  public static final int VECTOR_DIM = 1536;

  /** ✅ 이벤트별 기본 가중치 */
  private static final Map<UserEventLog.EventType, Double> BASE_WEIGHTS = Map.of(
      UserEventLog.EventType.VIEW, 1.0,
      UserEventLog.EventType.FAVORITE, 3.0,
      UserEventLog.EventType.BOOKED, 5.0,
      UserEventLog.EventType.REVIEW_WRITE, 10.0
  );

  /** ✅ time-decay용 반감기 (대략 30일 기준) */
  private static final double HALF_LIFE_DAYS = 30.0;

  /**
   * 🎯 유저 선호 벡터 계산
   *
   * @param logs          해당 유저의 이벤트 로그들
   * @param embeddingMap  key: performanceId, value: 공연 임베딩 벡터
   * @return 유저 최종 임베딩 (길이 1536), 로그/임베딩 없으면 0-vector
   */
  public List<Double> buildUserEmbeddingVector(
      List<UserEventLog> logs,
      Map<String, List<Double>> embeddingMap
  ) {
    if (logs == null || logs.isEmpty() || embeddingMap == null || embeddingMap.isEmpty()) {
      log.debug("⚪ 유저 로그 또는 임베딩 없음 → 0-vector 반환");
      return zeroVector();
    }

    double[] acc = new double[VECTOR_DIM];
    double totalWeight = 0.0;
    LocalDateTime now = LocalDateTime.now();

    for (UserEventLog logEvent : logs) {
      // PERFORMANCE 대상만 사용 (필요하면 나중에 PLACE/REVIEW도 확장)
      if (logEvent.getTargetType() != UserEventLog.TargetType.PERFORMANCE) {
        continue;
      }
      String performanceId = logEvent.getTargetId();
      if (performanceId == null) continue;

      List<Double> perfVector = embeddingMap.get(performanceId);
      if (perfVector == null || perfVector.size() != VECTOR_DIM) {
        log.debug("⚠ 공연 임베딩 없음 또는 차원 불일치: performanceId={}", performanceId);
        continue;
      }

      // 기본 이벤트 weight
      double baseWeight = BASE_WEIGHTS.getOrDefault(logEvent.getEventType(), 1.0);

      // 시간 디케이 (최근일수록 weight 큼)
      long daysAgo = ChronoUnit.DAYS.between(
          logEvent.getCreatedAt().toLocalDate(),
          now.toLocalDate()
      );
      if (daysAgo < 0) daysAgo = 0;
      double decay = Math.exp(-daysAgo / HALF_LIFE_DAYS);

      double finalWeight = baseWeight * decay;
      if (finalWeight <= 0) continue;

      totalWeight += finalWeight;

      for (int i = 0; i < VECTOR_DIM; i++) {
        acc[i] += perfVector.get(i) * finalWeight;
      }
    }

    if (totalWeight <= 0) {
      log.debug("⚪ totalWeight=0 → 0-vector 반환");
      return zeroVector();
    }

    // 평균 내서 정규화
    List<Double> result = new ArrayList<>(VECTOR_DIM);
    for (int i = 0; i < VECTOR_DIM; i++) {
      result.add(acc[i] / totalWeight);
    }

    return result;
  }

  /** 🔹 1536차원 0-vector 생성 (cold start용) */
  public List<Double> zeroVector() {
    List<Double> list = new ArrayList<>(VECTOR_DIM);
    for (int i = 0; i < VECTOR_DIM; i++) {
      list.add(0.0);
    }
    return list;
  }
}
