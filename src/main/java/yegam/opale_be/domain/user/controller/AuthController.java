package yegam.opale_be.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yegam.opale_be.domain.user.dto.request.LoginRequestDto;
import yegam.opale_be.domain.user.service.AuthService;
import yegam.opale_be.global.jwt.TokenResponse;
import yegam.opale_be.global.response.BaseResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {

  private final AuthService authService;

  /** 로그인 */
  @Operation(summary = "사용자 로그인", description = "로그인 후 Access Token과 Refresh Token을 발급받습니다.")
  @PostMapping("/login")
  public ResponseEntity<BaseResponse<TokenResponse>> login(@RequestBody @Valid LoginRequestDto dto) {
    TokenResponse response = authService.login(dto);
    return ResponseEntity.ok(BaseResponse.success("로그인 성공", response));
  }

  /** Access Token 재발급 */
  @Operation(summary = "Access Token 재발급", description = "Refresh Token으로 Access Token을 재발급받습니다.")
  @PostMapping("/refresh")
  public ResponseEntity<BaseResponse<TokenResponse>> refresh(@RequestHeader("Authorization") String refreshToken) {
    TokenResponse response = authService.refreshAccessToken(refreshToken);
    return ResponseEntity.ok(BaseResponse.success("Access Token 재발급 성공", response));
  }

  /** 로그아웃 */
  @Operation(summary = "사용자 로그아웃", description = "Access Token을 블랙리스트 처리하여 로그아웃합니다.")
  @PostMapping("/logout")
  public ResponseEntity<BaseResponse<String>> logout(@RequestHeader("Authorization") String accessToken) {
    authService.logout(accessToken);
    return ResponseEntity.ok(BaseResponse.success("로그아웃이 완료되었습니다.", null));
  }
}
