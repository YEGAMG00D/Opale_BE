package yegam.opale_be.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import yegam.opale_be.domain.user.dto.request.*;
import yegam.opale_be.domain.user.dto.response.CheckNicknameResponseDto;
import yegam.opale_be.domain.user.dto.response.UserResponseDto;
import yegam.opale_be.domain.user.service.UserService;
import yegam.opale_be.global.exception.CustomException;
import yegam.opale_be.global.exception.GlobalErrorCode;
import yegam.opale_be.global.response.BaseResponse;


/**
 * UserController
 *
 *  ◎ 회원 정보 관련 API 요청을 받는 Controller.
 *  - 요청 경로: /api/users
 *
 *  1) 이메일 중복 확인
 *  2) 닉네임 중복 확인
 *  3) 회원가입
 *  4) 내 정보 조회
 *  5) 내 정보 수정
 *  6) 비밀번호 변경, 회원 탈퇴
 *
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "회원 관리 API")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  /** 이메일 중복 확인 */
  @Operation(summary = "이메일 중복 확인", description = "사용자 이메일이 중복되는지 확인하는 API")
  @PostMapping("/check-duplicate")
  public ResponseEntity<BaseResponse<String>> checkDuplicate(@RequestBody @Valid CheckEmailRequestDto dto) {
    boolean isDuplicated = userService.checkDuplicateEmail(dto.getEmail());
    String message = isDuplicated ? "이미 존재하는 이메일입니다." : "사용 가능한 이메일입니다.";
    return ResponseEntity.ok(BaseResponse.success(message, null));
  }

  /** 닉네임 중복 확인 */
  @Operation(summary = "닉네임 중복 확인", description = "사용자 닉네임이 중복되는지 확인하는 API")
  @PostMapping("/check-nickname")
  public ResponseEntity<BaseResponse<CheckNicknameResponseDto>> checkDuplicateNickname(
      @RequestBody @Valid CheckNicknameRequestDto dto
  ) {
    CheckNicknameResponseDto response = userService.checkDuplicateNickname(dto.getNickname());
    String message = response.isAvailable()
        ? "사용 가능한 닉네임입니다."
        : "이미 존재하는 닉네임입니다.";
    return ResponseEntity.ok(BaseResponse.success(message, response));
  }


  /** 회원가입 */
  @Operation(summary = "회원가입", description = "사용자 회원가입을 위한 API")
  @PostMapping
  public ResponseEntity<BaseResponse<UserResponseDto>> signUp(@RequestBody @Valid UserSignUpRequestDto dto) {
    UserResponseDto response = userService.signUp(dto);
    return ResponseEntity.ok(BaseResponse.success("회원가입이 완료되었습니다.", response));
  }

  /** 내 정보 조회 */
  @Operation(summary = "사용자 본인 정보 조회", description = "사용자 본인의 정보 조회를 위한 API")
  @GetMapping("/me")
  public ResponseEntity<BaseResponse<UserResponseDto>> getMyInfo(@AuthenticationPrincipal Long userId) {
    if (userId == null) throw new CustomException(GlobalErrorCode.UNAUTHORIZED);
    UserResponseDto response = userService.getUser(userId);
    return ResponseEntity.ok(BaseResponse.success("내 정보 조회 성공", response));
  }

  /** 내 정보 수정 */
  @Operation(summary = "내 정보 수정", description = "사용자 본인의 정보를 수정합니다. (닉네임 중복 검사 포함)")
  @PutMapping("/me")
  public ResponseEntity<BaseResponse<UserResponseDto>> updateMyInfo(
      @AuthenticationPrincipal Long userId,
      @RequestBody @Valid UserUpdateRequestDto dto
  ) {
    UserResponseDto response = userService.updateUser(userId, dto);
    return ResponseEntity.ok(BaseResponse.success("내 정보 수정 완료", response));
  }

  /** 비밀번호 변경 */
  @Operation(summary = "비밀번호 변경", description = "사용자의 비밀번호를 변경합니다.")
  @PatchMapping("/me/password")
  public ResponseEntity<BaseResponse<String>> changePassword(
      @AuthenticationPrincipal Long userId,
      @RequestBody @Valid PasswordChangeRequestDto dto
  ) {
    userService.changePassword(userId, dto);
    return ResponseEntity.ok(BaseResponse.success("비밀번호가 변경되었습니다.", null));
  }

  /** 회원 탈퇴 */
  @Operation(summary = "회원 탈퇴", description = "사용자 본인의 계정을 비활성화(soft delete)합니다.")
  @PatchMapping("/me")
  public ResponseEntity<BaseResponse<String>> deleteUser(
      @AuthenticationPrincipal Long userId,
      @RequestBody(required = false) UserDeleteRequestDto dto
  ) {
    userService.deleteUser(userId, dto);
    return ResponseEntity.ok(BaseResponse.success("회원 탈퇴가 완료되었습니다.", null));
  }
}
