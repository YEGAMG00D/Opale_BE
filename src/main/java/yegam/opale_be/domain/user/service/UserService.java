package yegam.opale_be.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.user.dto.request.PasswordChangeRequestDto;
import yegam.opale_be.domain.user.dto.request.UserDeleteRequestDto;
import yegam.opale_be.domain.user.dto.request.UserSignUpRequestDto;
import yegam.opale_be.domain.user.dto.request.UserUpdateRequestDto;
import yegam.opale_be.domain.user.dto.response.UserResponseDto;
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

    return userMapper.toUserResponseDto(user);
  }

  /** 내 정보 조회 */
  @Transactional(readOnly = true)
  public UserResponseDto getUser(Long userId) {
    return userRepository.findById(userId)
        .filter(u -> !Boolean.TRUE.equals(u.getIsDeleted()))
        .map(userMapper::toUserResponseDto)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
  }

  /** 내 정보 수정 */
  public UserResponseDto updateUser(Long userId, UserUpdateRequestDto dto) {
    return userRepository.findById(userId)
        .map(user -> {
          if (dto.getNickname() != null) user.setNickname(dto.getNickname());
          if (dto.getPhone() != null) user.setPhone(dto.getPhone());
          if (dto.getAddress1() != null) user.setAddress1(dto.getAddress1());
          if (dto.getAddress2() != null) user.setAddress2(dto.getAddress2());
          log.info("회원 정보 수정: userId={}, email={}", user.getUserId(), user.getEmail());
          return userMapper.toUserResponseDto(user);
        })
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
  }

  /** 비밀번호 변경 */
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

  /** 회원 탈퇴(Soft Delete) */
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
