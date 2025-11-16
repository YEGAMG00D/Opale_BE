package yegam.opale_be.domain.preference.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import yegam.opale_be.domain.preference.dto.request.UserPreferenceVectorRequestDto;
import yegam.opale_be.domain.preference.dto.response.UserPreferenceVectorResponseDto;
import yegam.opale_be.domain.preference.service.UserPreferenceVectorService;
import yegam.opale_be.global.exception.CustomException;
import yegam.opale_be.global.exception.GlobalErrorCode;
import yegam.opale_be.global.response.BaseResponse;

@RestController
@RequestMapping("/api/preference")
@Tag(name = "Preference", description = "사용자 선호 벡터 API")
@RequiredArgsConstructor
public class UserPreferenceVectorController {

  private final UserPreferenceVectorService preferenceService;

  // -------------------------------------------------------------------------
  // 1) 선호 벡터 조회
  // -------------------------------------------------------------------------
  @Operation(summary = "사용자 선호 벡터 조회",
      description = "로그인된 사용자의 개인 선호 임베딩 벡터를 조회합니다.")
  @GetMapping
  public ResponseEntity<BaseResponse<UserPreferenceVectorResponseDto>> getUserVector(
      @AuthenticationPrincipal Long userId
  ) {
    if (userId == null) throw new CustomException(GlobalErrorCode.UNAUTHORIZED);

    UserPreferenceVectorResponseDto response = preferenceService.getUserVector(userId);
    return ResponseEntity.ok(BaseResponse.success("선호 벡터 조회 성공", response));
  }

  // -------------------------------------------------------------------------
  // 2) 선호 벡터 생성
  // -------------------------------------------------------------------------
  @Operation(summary = "사용자 선호 벡터 생성",
      description = "사용자의 개인 선호 임베딩 벡터를 새로 생성합니다.")
  @PostMapping
  public ResponseEntity<BaseResponse<UserPreferenceVectorResponseDto>> createUserVector(
      @AuthenticationPrincipal Long userId,
      @RequestBody UserPreferenceVectorRequestDto dto
  ) {
    if (userId == null) throw new CustomException(GlobalErrorCode.UNAUTHORIZED);

    UserPreferenceVectorResponseDto response =
        preferenceService.createUserVector(userId, dto);

    return ResponseEntity.ok(BaseResponse.success("선호 벡터 생성 성공", response));
  }

  // -------------------------------------------------------------------------
  // 3) 선호 벡터 업데이트
  // -------------------------------------------------------------------------
  @Operation(summary = "사용자 선호 벡터 업데이트",
      description = "기존에 저장된 선호 벡터를 새로운 벡터로 업데이트합니다.")
  @PutMapping
  public ResponseEntity<BaseResponse<UserPreferenceVectorResponseDto>> updateUserVector(
      @AuthenticationPrincipal Long userId,
      @RequestBody UserPreferenceVectorRequestDto dto
  ) {
    if (userId == null) throw new CustomException(GlobalErrorCode.UNAUTHORIZED);

    UserPreferenceVectorResponseDto response =
        preferenceService.updateUserVector(userId, dto);

    return ResponseEntity.ok(BaseResponse.success("선호 벡터 업데이트 성공", response));
  }
}
