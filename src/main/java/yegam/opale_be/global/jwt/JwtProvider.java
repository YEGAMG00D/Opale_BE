package yegam.opale_be.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import yegam.opale_be.global.exception.CustomException;
import yegam.opale_be.global.exception.GlobalErrorCode;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

  private final Key key;
  private final long accessTokenExpireTime;
  private final long refreshTokenExpireTime;

  public JwtProvider(
      @Value("${spring.jwt.secret}") String secretKey,
      @Value("${spring.jwt.access-token-expire-time}") long accessTokenExpireTime,
      @Value("${spring.jwt.refresh-token-expire-time}") long refreshTokenExpireTime
  ) {
    byte[] keyBytes = Base64.getDecoder().decode(secretKey);
    this.key = Keys.hmacShaKeyFor(keyBytes);
    this.accessTokenExpireTime = accessTokenExpireTime;
    this.refreshTokenExpireTime = refreshTokenExpireTime;
  }

  /** ✅ 토큰 유효성 검증 */
  public void validateTokenOrThrow(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token);
    } catch (ExpiredJwtException e) {
      log.warn("JWT 만료: {}", e.getMessage());
      throw new CustomException(GlobalErrorCode.JWT_EXPIRED);
    } catch (JwtException | IllegalArgumentException e) {
      log.warn("JWT 유효하지 않음: {}", e.getMessage());
      throw new CustomException(GlobalErrorCode.JWT_INVALID);
    }
  }

  /** ✅ 사용자 ID(Long) 추출 */
  public Long extractUserIdAsLong(String token) {
    try {
      return Long.parseLong(
          Jwts.parserBuilder()
              .setSigningKey(key)
              .build()
              .parseClaimsJws(token)
              .getBody()
              .getSubject()
      );
    } catch (Exception e) {
      throw new CustomException(GlobalErrorCode.JWT_INVALID);
    }
  }

  /** ✅ AccessToken 생성 */
  public String createAccessToken(Long userId, String email, String role) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + accessTokenExpireTime);
    return Jwts.builder()
        .setSubject(String.valueOf(userId))
        .claim("email", email)
        .claim("role", role) // ROLE_ 붙이지 않음
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  /** ✅ RefreshToken 생성 */
  public String createRefreshToken(Long userId) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + refreshTokenExpireTime);
    return Jwts.builder()
        .setSubject(String.valueOf(userId))
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  /** ✅ 역할(Role) 추출 */
  public String extractUserRole(String token) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token)
          .getBody()
          .get("role", String.class);
    } catch (Exception e) {
      throw new CustomException(GlobalErrorCode.JWT_INVALID);
    }
  }
}
