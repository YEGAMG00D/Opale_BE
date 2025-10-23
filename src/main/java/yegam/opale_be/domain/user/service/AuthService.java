package yegam.opale_be.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.user.dto.request.LoginRequestDto;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.domain.user.exception.UserErrorCode;
import yegam.opale_be.domain.user.repository.UserRepository;
import yegam.opale_be.global.exception.CustomException;
import yegam.opale_be.global.jwt.JwtProvider;
import yegam.opale_be.global.jwt.TokenResponse;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {

  private final JwtProvider jwtProvider;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  private final Set<String> blacklistedTokens = new HashSet<>();

  public TokenResponse login(LoginRequestDto dto) {
    User user = userRepository.findByEmail(dto.getEmail())
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
      throw new CustomException(UserErrorCode.PASSWORD_NOT_MATCHED);
    }

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
    );

    String accessToken = jwtProvider.createAccessToken(
        String.valueOf(user.getId()), user.getEmail(), user.getRole().name());
    String refreshToken = jwtProvider.createRefreshToken(String.valueOf(user.getId()));

    return TokenResponse.builder()
        .accessToken("Bearer " + accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  public TokenResponse refreshAccessToken(String refreshTokenHeader) {
    String refreshToken = extractToken(refreshTokenHeader);
    jwtProvider.validateTokenOrThrow(refreshToken); // 예외 던지기
    String userId = jwtProvider.extractUserId(refreshToken);

    User user = userRepository.findById(Long.valueOf(userId))
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    String newAccessToken = jwtProvider.createAccessToken(
        userId, user.getEmail(), user.getRole().name());

    return TokenResponse.builder()
        .accessToken("Bearer " + newAccessToken)
        .refreshToken(refreshToken)
        .build();
  }

  public void logout(String accessTokenHeader) {
    String accessToken = extractToken(accessTokenHeader);
    jwtProvider.validateTokenOrThrow(accessToken); // 예외 던지기
    blacklistedTokens.add(accessToken);
    log.info("로그아웃 완료 - accessToken 블랙리스트 등록");
  }

  private String extractToken(String headerValue) {
    if (headerValue == null || !headerValue.startsWith("Bearer ")) {
      throw new CustomException(UserErrorCode.JWT_INVALID);
    }
    return headerValue.substring(7).trim();
  }
}
