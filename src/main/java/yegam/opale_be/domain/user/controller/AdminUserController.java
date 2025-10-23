package yegam.userservice.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import yegam.userservice.domain.user.dto.response.UserResponseDto;
import yegam.userservice.domain.user.service.AdminUserService;
import yegam.userservice.global.response.BaseResponse;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "AdminUser", description = "AdminUser 관리 API")
public class AdminUserController {

  private final AdminUserService adminUserService;

  /** 전체 회원 목록 조회 */
  @Operation(summary = "(운영자) 전체 회원 목록 확인", description = "전체 회원 목록을 조회하는 API")
  @GetMapping("/users")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<List<UserResponseDto>>> getAllUsers() {
    List<UserResponseDto> users = adminUserService.getAllUsers();
    return ResponseEntity.ok(BaseResponse.success("전체 회원 조회 성공", users));
  }

  /** 특정 회원 상세 조회 */
  @Operation(summary = "(운영자) 특정 회원 확인", description = "특정 회원 ID로 정보를 조회하는 API")
  @GetMapping("/users/{userId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<UserResponseDto>> getUserById(@PathVariable Long userId) {
    UserResponseDto user = adminUserService.getUserById(userId);
    return ResponseEntity.ok(BaseResponse.success("회원 상세 조회 성공", user));
  }
}
