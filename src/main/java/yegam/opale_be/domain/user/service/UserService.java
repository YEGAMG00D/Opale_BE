package yegam.opale_be.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.email.service.EmailService;
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
  private final EmailService emailService;

  // ⭐ 추가
  private final UserPreferenceVectorRepository vectorRepository;
  private final ZeroVectorUtil zeroVectorUtil;

  // ---------------------------------------------------------------------
  // 회원가입
  // ---------------------------------------------------------------------
  @Transactional
  public UserResponseDto signUp(UserSignUpRequestDto dto) {

    // 중복 체크
    if (userRepository.existsByEmail(dto.getEmail())) {
      throw new CustomException(UserErrorCode.DUPLICATE_EMAIL);
    }
    if (userRepository.existsByNickname(dto.getNickname())) {
      throw new CustomException(UserErrorCode.DUPLICATE_NICKNAME);
    }

    // 1) 유저 생성
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

    userRepository.save(user);   // 여기서 userId가 생성됨

    log.info("회원가입 완료: userId={}, email={}", user.getUserId(), user.getEmail());

    // 2) 선호 벡터 자동 생성 (MapsId 구조)
    UserPreferenceVector vector = UserPreferenceVector.builder()
        .user(user)                     // ⭐ PK 자동 매핑됨
        .embeddingVector(zeroVectorUtil.generateZeroVectorJson())  // 초기 0벡터
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




  // ---------------------------------------------------------------------
  // 임시 비밀번호 발급 + 이메일 발송
  // ---------------------------------------------------------------------
  @Transactional
  public PasswordResetResponseDto resetPassword(PasswordResetRequestDto dto) {

    // 1) 이메일로 사용자 조회
    User user = userRepository.findByEmail(dto.getEmail())
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    // 2) 임시 비밀번호 생성
    String tempPassword = generateTempPassword();
    String encodedTempPw = passwordEncoder.encode(tempPassword);

    // 3) 사용자 비밀번호 업데이트
    user.setPassword(encodedTempPw);

    log.info("임시 비밀번호 발급 완료: email={}, tempPassword(raw)={}",
        user.getEmail(), tempPassword);

    // ⭐ 4) 이메일로 임시 비밀번호 발송 (여기가 중요함!)
    emailService.sendTempPassword(user.getEmail(), tempPassword);

    // 5) response DTO 반환
    return userMapper.toPasswordResetResponseDto(user.getEmail());
  }





  /** 랜덤 임시 비밀번호 생성 (패턴 준수: 영문+숫자+특수문자 포함, 10자리) */
  private String generateTempPassword() {

    String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String lower = "abcdefghijklmnopqrstuvwxyz";
    String digits = "0123456789";
    String special = "!@#$%^&*()_+-=";

    String all = upper + lower + digits + special;
    java.util.Random random = new java.util.Random();

    // ❶ 필수 문자 1개씩 강제 포함
    StringBuilder password = new StringBuilder();
    password.append(upper.charAt(random.nextInt(upper.length())));   // 영문 대문자 1
    password.append(digits.charAt(random.nextInt(digits.length()))); // 숫자 1
    password.append(special.charAt(random.nextInt(special.length()))); // 특수문자 1

    // ❷ 나머지 7문자는 전체에서 랜덤
    for (int i = 0; i < 7; i++) {
      password.append(all.charAt(random.nextInt(all.length())));
    }

    // ❸ 셔플해서 예측 불가능하게
    java.util.List<Character> chars = password.chars()
        .mapToObj(c -> (char) c)
        .collect(java.util.stream.Collectors.toList());

    java.util.Collections.shuffle(chars);

    // ❹ 최종 문자열로 변환
    StringBuilder finalPw = new StringBuilder();
    chars.forEach(finalPw::append);

    return finalPw.toString();
  }




}
