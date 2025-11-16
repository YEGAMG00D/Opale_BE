package yegam.opale_be.domain.preference.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.preference.dto.request.UserPreferenceVectorRequestDto;
import yegam.opale_be.domain.preference.dto.response.UserPreferenceVectorResponseDto;
import yegam.opale_be.domain.preference.entity.UserPreferenceVector;
import yegam.opale_be.domain.preference.exception.PreferenceErrorCode;
import yegam.opale_be.domain.preference.mapper.UserPreferenceVectorMapper;
import yegam.opale_be.domain.preference.repository.UserPreferenceVectorRepository;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.domain.user.exception.UserErrorCode;
import yegam.opale_be.domain.user.repository.UserRepository;
import yegam.opale_be.global.exception.CustomException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPreferenceVectorService {

  private final UserPreferenceVectorRepository preferenceRepository;
  private final UserPreferenceVectorMapper preferenceMapper;
  private final UserRepository userRepository;

  /**
   * 사용자 선호 벡터 조회
   */
  public UserPreferenceVectorResponseDto getUserVector(Long userId) {
    UserPreferenceVector vector = preferenceRepository.findById(userId)
        .orElseThrow(() -> new CustomException(PreferenceErrorCode.VECTOR_NOT_FOUND));

    return preferenceMapper.toResponseDto(vector);
  }

  /**
   * 사용자 선호 벡터 생성
   */
  @Transactional
  public UserPreferenceVectorResponseDto createUserVector(Long userId, UserPreferenceVectorRequestDto dto) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    UserPreferenceVector vector = preferenceMapper.toEntity(user, dto);
    UserPreferenceVector saved = preferenceRepository.save(vector);

    log.info("🎯 사용자 선호 벡터 생성: userId={}", userId);
    return preferenceMapper.toResponseDto(saved);
  }

  /**
   * 사용자 선호 벡터 업데이트
   */
  @Transactional
  public UserPreferenceVectorResponseDto updateUserVector(Long userId, UserPreferenceVectorRequestDto dto) {
    UserPreferenceVector vector = preferenceRepository.findById(userId)
        .orElseThrow(() -> new CustomException(PreferenceErrorCode.VECTOR_NOT_FOUND));

    preferenceMapper.updateEntity(vector, dto);

    log.info("🔄 사용자 선호 벡터 업데이트: userId={}", userId);
    return preferenceMapper.toResponseDto(vector);
  }
}
