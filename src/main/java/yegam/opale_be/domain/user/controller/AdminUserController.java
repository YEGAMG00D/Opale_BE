package yegam.opale_be.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import yegam.opale_be.domain.user.dto.response.UserResponseDto;
import yegam.opale_be.domain.user.service.AdminUserService;
import yegam.opale_be.global.response.BaseResponse;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "AdminUser", description = "관리자용 회원 관리 API")
public class AdminUserController {

  private final AdminUserService adminUserService;

  @Operation(
      summary = "(운영자) 전체 회원 목록 확인",
      description = "탈퇴 회원 포함 전체 회원 목록을 가입일 기준 최신순으로 조회합니다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "조회 성공",
              content = @Content(schema = @Schema(implementation = BaseResponse.class)))
      }
  )
  @GetMapping("/users")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<List<UserResponseDto>>> getAllUsers() {
    List<UserResponseDto> users = adminUserService.getAllUsers();
    return ResponseEntity.ok(BaseResponse.success("전체 회원 조회 성공", users));
  }




  @Operation(
      summary = "(운영자) 특정 회원 상세 조회",
      description = "회원 ID로 특정 회원의 상세 정보를 조회합니다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "조회 성공",
              content = @Content(schema = @Schema(implementation = BaseResponse.class)))
      }
  )
  @GetMapping("/users/{userId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<UserResponseDto>> getUserById(
      @Parameter(description = "사용자 ID", example = "u_1a2b3c")
      @PathVariable Long userId
  ) {
    UserResponseDto user = adminUserService.getUserById(userId);
    return ResponseEntity.ok(BaseResponse.success("회원 상세 조회 성공", user));
  }
}
