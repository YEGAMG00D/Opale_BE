package yegam.opale_be.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.user.dto.request.LoginRequestDto;
import yegam.opale_be.domain.user.dto.response.LoginResponseDto;
import yegam.opale_be.domain.user.dto.response.UserResponseDto;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.domain.user.entity.UserToken;
import yegam.opale_be.domain.user.exception.UserErrorCode;
import yegam.opale_be.domain.user.mapper.UserMapper;
import yegam.opale_be.domain.user.repository.UserRepository;
import yegam.opale_be.domain.user.repository.UserTokenRepository;
import yegam.opale_be.global.exception.CustomException;
import yegam.opale_be.global.jwt.JwtProvider;
import yegam.opale_be.global.jwt.TokenResponse;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {

  private final JwtProvider jwtProvider;
  private final UserRepository userRepository;
  private final UserTokenRepository userTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;

  private final Set<String> blacklistedTokens = new HashSet<>();


  /** ✅ 로그인 */
  public LoginResponseDto login(LoginRequestDto dto) {
    User user = userRepository.findByEmail(dto.getEmail())
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
      throw new CustomException(UserErrorCode.PASSWORD_NOT_MATCHED);
    }

    String accessToken = jwtProvider.createAccessToken(user.getUserId(), user.getEmail(), user.getRole().name());
    String refreshToken = jwtProvider.createRefreshToken(user.getUserId());

    userTokenRepository.save(UserToken.builder()
        .userId(user.getUserId())
        .refreshToken(refreshToken)
        .issuedAt(LocalDateTime.now())
        .expiresAt(LocalDateTime.now().plusDays(7))
        .build());

    log.info("✅ 로그인 성공: userId={}, email={}", user.getUserId(), user.getEmail());

    // ✅ TokenResponse
    TokenResponse tokenResponse = TokenResponse.builder()
        .accessToken("Bearer " + accessToken)
        .refreshToken(refreshToken)
        .build();

    // ✅ UserResponseDto
    UserResponseDto userResponse = userMapper.toUserResponseDto(user);

    // ✅ LoginResponseDto로 통합 반환
    return LoginResponseDto.builder()
        .token(tokenResponse)
        .user(userResponse)
        .build();
  }



  /** ✅ RefreshToken 기반 AccessToken 재발급 */
  public TokenResponse refreshAccessToken(String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
      throw new CustomException(UserErrorCode.JWT_INVALID);
    }

    jwtProvider.validateTokenOrThrow(refreshToken);

    Long userId = jwtProvider.extractUserIdAsLong(refreshToken);
    if (userId == null) {
      throw new CustomException(UserErrorCode.JWT_INVALID);
    }

    UserToken savedToken = userTokenRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.REFRESH_TOKEN_NOT_FOUND));

    if (!refreshToken.equals(savedToken.getRefreshToken())) {
      throw new CustomException(UserErrorCode.REFRESH_TOKEN_MISMATCH);
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    String newAccessToken = jwtProvider.createAccessToken(userId, user.getEmail(), user.getRole().name());
    String newRefreshToken = jwtProvider.createRefreshToken(userId);

    savedToken.setRefreshToken(newRefreshToken);
    savedToken.setIssuedAt(LocalDateTime.now());
    savedToken.setExpiresAt(LocalDateTime.now().plusDays(7));
    userTokenRepository.save(savedToken);

    log.info("♻️ AccessToken & RefreshToken 재발급 완료: userId={}", userId);

    return TokenResponse.builder()
        .accessToken("Bearer " + newAccessToken)
        .refreshToken(newRefreshToken)
        .build();
  }



  /** ✅ 로그아웃 (AccessToken 자동 인식) */
  public void logout(Long userId) {
    if (userId == null) {
      throw new CustomException(UserErrorCode.JWT_INVALID);
    }

    userTokenRepository.findById(userId).ifPresent(userTokenRepository::delete);
    log.info("🚪 로그아웃 완료: userId={} (RefreshToken 삭제)", userId);
  }

  public boolean isBlacklisted(String token) {
    return blacklistedTokens.contains(token);
  }
}
