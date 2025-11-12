package yegam.opale_be.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import yegam.opale_be.global.jwt.TokenResponse;

/**
 * 로그인 응답 DTO
 * - AccessToken / RefreshToken + 사용자 정보 함께 반환
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "LoginResponse DTO", description = "로그인 응답 (토큰 + 사용자 정보)")
public class LoginResponseDto {

  @Schema(description = "JWT 토큰 정보")
  private TokenResponse token;

  @Schema(description = "로그인한 사용자 정보")
  private UserResponseDto user;
}
