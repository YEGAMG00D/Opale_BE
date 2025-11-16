package yegam.opale_be.domain.preference.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.analytics.entity.UserEventLog;
import yegam.opale_be.domain.analytics.repository.UserEventLogRepository;
import yegam.opale_be.domain.preference.entity.UserPreferenceVector;
import yegam.opale_be.domain.preference.repository.UserPreferenceVectorRepository;
import yegam.opale_be.domain.preference.util.VectorCalculatorUtil;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.domain.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreferenceBatchService {

  private final UserRepository userRepository;
  private final UserEventLogRepository eventLogRepository;
  private final UserPreferenceVectorRepository vectorRepository;
  private final VectorCalculatorUtil vectorCalculatorUtil;
  private final ObjectMapper objectMapper;

  /** 🔥 전체 유저 선호 벡터 업데이트 */
  @Transactional
  public void updateAllUserVectors() {

    List<User> users = userRepository.findAll();
    log.info("🚀 전체 사용자 벡터 업데이트 시작 — 총 {}명", users.size());

    for (User user : users) {
      updateSingleUserVector(user.getUserId());
    }

    log.info("🎉 전체 사용자 벡터 업데이트 완료");
  }

  /** 🔥 단일 유저 선호 벡터 업데이트 */
  @Transactional
  public void updateSingleUserVector(Long userId) {

    List<UserEventLog> logs = eventLogRepository.findByUser_UserId(userId);

    List<Double> embedding = vectorCalculatorUtil.calculateUserEmbedding(logs);

    String vectorJson;
    try {
      vectorJson = objectMapper.writeValueAsString(embedding);
    } catch (JsonProcessingException e) {
      log.error("❌ JSON 직렬화 오류: userId={}", userId);
      return;
    }

    UserPreferenceVector vector = vectorRepository.findById(userId)
        .orElse(UserPreferenceVector.builder()
            .userId(userId)
            .build());

    vector.setEmbeddingVector(vectorJson);
    vectorRepository.save(vector);

    log.info("✅ 유저 벡터 업데이트: userId={}, dim={}", userId, embedding.size());
  }
}
