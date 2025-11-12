package yegam.opale_be.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import yegam.opale_be.domain.user.dto.response.AdminUserListResponseDto;
import yegam.opale_be.domain.user.dto.response.AdminUserResponseDto;
import yegam.opale_be.domain.user.service.AdminUserService;
import yegam.opale_be.global.response.BaseResponse;



/**
 * AdminUserController
 *
 *  ◎ 운영자용 회원 정보 관련 API 요청을 받는 Controller.
 *  - 요청 경로: /api/admin
 *
 *  1) 전체 회원 목록 확인
 *  2) 특정 회원 상세 정보 확인
 *
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "AdminUser", description = "관리자용 회원 관리 API")
public class AdminUserController {

  private final AdminUserService adminUserService;

  /** 전체 회원 목록 확인 */
  /**
   *
   * @param includeAdmin
   * @return {totalCount, users}
   */
  @Operation(
      summary = "(운영자) 전체 회원 목록 확인",
      description = "탈퇴 회원 포함 전체 회원 목록을 조회합니다. " +
          "기본적으로 일반 회원만 표시하며, `includeAdmin=true` 를 지정하면 운영자 계정도 함께 표시됩니다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "조회 성공",
              content = @Content(schema = @Schema(implementation = AdminUserListResponseDto.class)))
      }
  )
  @GetMapping("/users")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<AdminUserListResponseDto>> getAllUsers(
      @Parameter(description = "운영자 포함 여부", example = "false")
      @RequestParam(defaultValue = "false") boolean includeAdmin
  ) {
    AdminUserListResponseDto response = adminUserService.getAllUsers(includeAdmin);
    return ResponseEntity.ok(BaseResponse.success("전체 회원 조회 성공", response));
  }


  /** 특정 회원 상세 조회 */
  /**
   *
   * @param userId
   * @return {userId, email, name, nickname, birth, role, isDeleted, createdAt}
   */
  @Operation(
      summary = "(운영자) 특정 회원 상세 조회",
      description = "회원 ID로 특정 회원의 상세 정보를 조회합니다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "조회 성공",
              content = @Content(schema = @Schema(implementation = AdminUserResponseDto.class)))
      }
  )
  @GetMapping("/users/{userId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<AdminUserResponseDto>> getUserById(
      @Parameter(description = "사용자 userId", example = "1")
      @PathVariable Long userId
  ) {
    AdminUserResponseDto user = adminUserService.getUserById(userId);
    return ResponseEntity.ok(BaseResponse.success("회원 상세 조회 성공", user));
  }
}
