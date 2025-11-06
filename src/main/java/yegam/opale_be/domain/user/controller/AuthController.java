package yegam.opale_be.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import yegam.opale_be.domain.user.dto.request.LoginRequestDto;
import yegam.opale_be.domain.user.service.AuthService;
import yegam.opale_be.global.jwt.TokenResponse;
import yegam.opale_be.global.response.BaseResponse;



/**
 * AuthController
 *
 *  ◎ 인증 인가 관련 API 요청을 받는 Controller.
 *  - 요청 경로: /api/auth
 *
 *  1) 로그인
 *  2) AccessToken 재발급
 *  3) 로그아웃
 *
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증/인가 관련 API")
public class AuthController {

  private final AuthService authService;

  /** 로그인 */
  @Operation(
      summary = "사용자 로그인",
      description = "이메일과 비밀번호로 로그인하여 AccessToken과 RefreshToken을 발급받습니다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "로그인 성공",
              content = @Content(schema = @Schema(implementation = BaseResponse.class)))
      }
  )
  @PostMapping("/login")
  public ResponseEntity<BaseResponse<TokenResponse>> login(@RequestBody @Valid LoginRequestDto dto) {
    TokenResponse response = authService.login(dto);
    return ResponseEntity.ok(BaseResponse.success("로그인 성공", response));
  }

  /** AccessToken 재발급 */
  @Operation(
      summary = "Access Token 재발급",
      description = """
          Refresh Token을 이용해 새로운 Access Token과 Refresh Token을 재발급합니다.
          - AccessToken은 필요하지 않습니다.
          - 헤더에 "Refresh-Token" 을 추가하고, 로그인 시 받은 refreshToken을 입력하세요.
          """,
      responses = {
          @ApiResponse(responseCode = "200", description = "재발급 성공",
              content = @Content(schema = @Schema(implementation = BaseResponse.class)))
      }
  )
  @PostMapping("/refresh")
  public ResponseEntity<BaseResponse<TokenResponse>> refresh(
      @RequestHeader("Refresh-Token") String refreshToken) {
    TokenResponse response = authService.refreshAccessToken(refreshToken);
    return ResponseEntity.ok(BaseResponse.success("Access Token 재발급 성공", response));
  }

  /** 로그아웃 */
  @Operation(
      summary = "사용자 로그아웃",
      description = "현재 인증된 사용자의 AccessToken을 기반으로 RefreshToken을 삭제하고 세션을 만료합니다. 별도 입력 불필요합니다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "로그아웃 완료",
              content = @Content(schema = @Schema(implementation = BaseResponse.class)))
      }
  )
  @PostMapping("/logout")
  public ResponseEntity<BaseResponse<String>> logout(@AuthenticationPrincipal Long userId) {
    authService.logout(userId);
    return ResponseEntity.ok(BaseResponse.success("로그아웃이 완료되었습니다.", null));
  }
}
