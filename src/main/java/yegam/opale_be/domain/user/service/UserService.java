package yegam.opale_be.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.user.dto.request.*;
import yegam.opale_be.domain.user.dto.response.UserResponseDto;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.domain.user.exception.UserErrorCode;
import yegam.opale_be.domain.user.repository.UserRepository;
import yegam.opale_be.domain.user.mapper.UserMapper;
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

  /** 이메일 중복 확인 */
  @Transactional(readOnly = true)
  public boolean checkDuplicateEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  /** 회원가입 */
  public UserResponseDto signUp(UserSignUpRequestDto dto) {
    if (userRepository.existsByEmail(dto.getEmail())) {
      throw new CustomException(UserErrorCode.DUPLICATE_EMAIL);
    }

    String encodedPassword = passwordEncoder.encode(dto.getPassword());

    User user = User.builder()
        .email(dto.getEmail())
        .password(encodedPassword)
        .name(dto.getName())
        .birth(dto.getBirth())
        .gender(dto.getGender())
        .phone(dto.getPhone())
        .address(dto.getAddress())
        .nickname(dto.getNickname())
        .role(User.Role.USER)
        .isDeleted(false)
        .build();

    userRepository.save(user);
    log.info("회원가입 완료: {}", user.getEmail());
    return userMapper.toUserResponseDto(user);
  }

  /** 내 정보 조회 */
  @Transactional(readOnly = true)
  public UserResponseDto getUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
    if (user.getIsDeleted()) {
      throw new CustomException(UserErrorCode.USER_NOT_FOUND);
    }

    log.info("내 정보 조회: {}", user.getEmail());
    return userMapper.toUserResponseDto(user);
  }

  /** 내 정보 수정 */
  public UserResponseDto updateUser(Long userId, UserUpdateRequestDto dto) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    if (dto.getNickname() != null) user.setNickname(dto.getNickname());
    if (dto.getPhone() != null) user.setPhone(dto.getPhone());
    if (dto.getAddress() != null) user.setAddress(dto.getAddress());

    userRepository.save(user);
    log.info("회원 정보 수정 완료: {}", user.getEmail());
    return userMapper.toUserResponseDto(user);
  }

  /** 비밀번호 변경 */
  public void changePassword(Long userId, PasswordChangeRequestDto dto) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    // 현재 비번이랑 같은지 체크
    if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
      throw new CustomException(UserErrorCode.PASSWORD_NOT_MATCHED);
    }

    user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    userRepository.save(user);
    log.info("비밀번호 변경 완료: {}", user.getEmail());
  }

  /** 회원 탈퇴 (Soft Delete) */
  public void deleteUser(Long userId, UserDeleteRequestDto dto) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    user.setIsDeleted(true);
    user.setDeletedAt(LocalDateTime.now());
    userRepository.save(user);
    log.info("회원 탈퇴 처리 완료: {}", user.getEmail());
  }
}
