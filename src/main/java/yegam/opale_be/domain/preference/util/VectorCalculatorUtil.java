package yegam.opale_be.domain.preference.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import yegam.opale_be.domain.analytics.entity.UserEventLog;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.culture.performance.repository.PerformanceRepository;
import yegam.opale_be.domain.recommendation.util.EmbeddingVectorUtil;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class VectorCalculatorUtil {

  private final PerformanceRepository performanceRepository;
  private final EmbeddingVectorUtil embeddingVectorUtil;

  /** 이벤트별 기본 가중치 */
  private static final Map<UserEventLog.EventType, Double> WEIGHTS = Map.of(
      UserEventLog.EventType.VIEW, 1.0,
      UserEventLog.EventType.LIKE, 2.0,
      UserEventLog.EventType.FAVORITE, 3.0,
      UserEventLog.EventType.REVIEW_WRITE, 5.0,
      UserEventLog.EventType.BOOKED, 8.0
  );

  /**
   * 🔥 유저 선호 embedding 벡터 계산
   * - performanceId 기반 weight 누적
   * - 각 공연 embedding vector(1536차원)를 가져와 가중 평균
   */
  public List<Double> calculateUserEmbedding(List<UserEventLog> logs) {

    if (logs.isEmpty()) return Collections.emptyList();

    // 1) 공연별 weight 누적
    Map<String, Double> weightMap = new HashMap<>();
    for (UserEventLog log : logs) {
      if (log.getTargetId() == null) continue;

      double weight = WEIGHTS.getOrDefault(log.getEventType(), 1.0);
      weightMap.put(log.getTargetId(),
          weightMap.getOrDefault(log.getTargetId(), 0.0) + weight);
    }

    // 2) 공연 embedding 가져오기
    double[] sumVector = null;
    double totalWeight = 0.0;

    for (Map.Entry<String, Double> entry : weightMap.entrySet()) {
      String performanceId = entry.getKey();
      double weight = entry.getValue();

      Performance p = performanceRepository.findById(performanceId).orElse(null);
      if (p == null || p.getEmbeddingVector() == null) continue;

      List<Double> embedding = embeddingVectorUtil.parseToList(p.getEmbeddingVector());
      if (embedding.isEmpty()) continue;

      // 초기화
      if (sumVector == null) {
        sumVector = new double[embedding.size()];
      }

      // 가중치 적용하여 합산
      for (int i = 0; i < embedding.size(); i++) {
        sumVector[i] += embedding.get(i) * weight;
      }

      totalWeight += weight;
    }

    if (sumVector == null) return Collections.emptyList();

    // 3) 정규화(가중 평균)
    for (int i = 0; i < sumVector.length; i++) {
      sumVector[i] /= totalWeight;
    }

    return Arrays.stream(sumVector)
        .boxed()
        .toList();
  }
}
