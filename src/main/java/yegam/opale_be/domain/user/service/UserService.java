package yegam.opale_be.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.preference.entity.UserPreferenceVector;
import yegam.opale_be.domain.preference.repository.UserPreferenceVectorRepository;
import yegam.opale_be.domain.preference.util.ZeroVectorUtil;
import yegam.opale_be.domain.user.dto.request.*;
import yegam.opale_be.domain.user.dto.response.*;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.domain.user.exception.UserErrorCode;
import yegam.opale_be.domain.user.mapper.UserMapper;
import yegam.opale_be.domain.user.repository.UserRepository;
import yegam.opale_be.global.exception.CustomException;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;

  // ⭐ 추가
  private final UserPreferenceVectorRepository vectorRepository;
  private final ZeroVectorUtil zeroVectorUtil;

  // ---------------------------------------------------------------------
  // 회원가입
  // ---------------------------------------------------------------------
  public UserResponseDto signUp(UserSignUpRequestDto dto) {

    if (userRepository.existsByEmail(dto.getEmail())) {
      throw new CustomException(UserErrorCode.DUPLICATE_EMAIL);
    }
    if (userRepository.existsByNickname(dto.getNickname())) {
      throw new CustomException(UserErrorCode.DUPLICATE_NICKNAME);
    }

    User user = User.builder()
        .email(dto.getEmail())
        .password(passwordEncoder.encode(dto.getPassword()))
        .name(dto.getName())
        .birth(dto.getBirth())
        .gender(dto.getGender())
        .phone(dto.getPhone())
        .address1(dto.getAddress1())
        .address2(dto.getAddress2())
        .nickname(dto.getNickname())
        .role(User.Role.USER)
        .isDeleted(false)
        .build();

    userRepository.save(user);
    log.info("회원가입 완료: userId={}, email={}", user.getUserId(), user.getEmail());

    // ⭐ A. 가입 시 0-vector 자동 생성
    UserPreferenceVector vector = UserPreferenceVector.builder()
        .userId(user.getUserId())
        .embeddingVector(zeroVectorUtil.generateZeroVectorJson())  // 1536차원 0 벡터
        .user(user)
        .build();

    vectorRepository.save(vector);
    log.info("⭐ 신규 유저 벡터 초기화 완료: userId={}", user.getUserId());

    return userMapper.toUserResponseDto(user);
  }


  // ---------------------------------------------------------------------
  // 이하 기존 코드 그대로
  // ---------------------------------------------------------------------

  @Transactional(readOnly = true)
  public CheckNicknameResponseDto checkDuplicateNickname(String nickname) {
    boolean exists = userRepository.existsByNickname(nickname);
    return userMapper.toCheckNicknameResponseDto(nickname, exists);
  }

  @Transactional(readOnly = true)
  public boolean checkDuplicateEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  @Transactional(readOnly = true)
  public UserResponseDto getUser(Long userId) {
    return userRepository.findById(userId)
        .filter(u -> !Boolean.TRUE.equals(u.getIsDeleted()))
        .map(userMapper::toUserResponseDto)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
  }

  public UserResponseDto updateUser(Long userId, UserUpdateRequestDto dto) {
    return userRepository.findById(userId)
        .map(user -> {
          if (dto.getNickname() != null) {
            if (userRepository.existsByNickname(dto.getNickname())
                && !dto.getNickname().equals(user.getNickname())) {
              throw new CustomException(UserErrorCode.DUPLICATE_NICKNAME);
            }
            user.setNickname(dto.getNickname());
          }

          if (dto.getPhone() != null) user.setPhone(dto.getPhone());
          if (dto.getAddress1() != null) user.setAddress1(dto.getAddress1());
          if (dto.getAddress2() != null) user.setAddress2(dto.getAddress2());

          log.info("회원 정보 수정: userId={}, email={}", user.getUserId(), user.getEmail());
          return userMapper.toUserResponseDto(user);
        })
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
  }

  public void changePassword(Long userId, PasswordChangeRequestDto dto) {
    userRepository.findById(userId)
        .map(user -> {
          if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(UserErrorCode.CURRENT_PASSWORD_NOT_MATCHED);
          }
          user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
          log.info("비밀번호 변경 완료: userId={}", user.getUserId());
          return user;
        })
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
  }

  public void deleteUser(Long userId, UserDeleteRequestDto dto) {
    userRepository.findById(userId)
        .map(user -> {
          user.setIsDeleted(true);
          user.setDeletedAt(LocalDateTime.now());
          log.info("회원 탈퇴 처리: userId={}, email={}", user.getUserId(), user.getEmail());
          return user;
        })
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
  }
}
