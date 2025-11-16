package yegam.opale_be.domain.analytics.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import yegam.opale_be.domain.analytics.dto.request.UserEventLogCreateRequestDto;
import yegam.opale_be.domain.analytics.dto.request.UserEventLogSearchRequestDto;
import yegam.opale_be.domain.analytics.dto.response.UserEventLogListResponseDto;
import yegam.opale_be.domain.analytics.dto.response.UserEventLogResponseDto;
import yegam.opale_be.domain.analytics.service.UserEventLogService;
import yegam.opale_be.global.exception.CustomException;
import yegam.opale_be.global.exception.GlobalErrorCode;
import yegam.opale_be.global.response.BaseResponse;

/**
 * UserEventLogController
 *
 * ◎ 사용자 행동 로그 API
 * - 요청 경로: /api/logs
 *
 * 1) 사용자 행동 로그 생성 (POST)
 * 2) 로그 목록 조회 + 필터링 (GET)
 */
@RestController
@RequestMapping("/api/logs")
@Tag(name = "Analytics - UserEventLog", description = "사용자 행동 로그(Analytics) API")
@RequiredArgsConstructor
public class UserEventLogController {

  private final UserEventLogService userEventLogService;

  // -----------------------------------------------------------------
  // 1) 사용자 행동 로그 생성
  // -----------------------------------------------------------------
  @Operation(summary = "사용자 행동 로그 생성",
      description = """
          사용자의 공연/공연장/리뷰에 대한 행동(조회, 관심, 예매, 리뷰 작성 등)을 로그로 저장합니다.
          - userId는 AccessToken으로부터 @AuthenticationPrincipal 로 주입됩니다.
          - eventType: VIEW / FAVORITE / BOOKED / REVIEW_WRITE 등
          - targetType: PERFORMANCE / PLACE / REVIEW
          """)
  @PostMapping
  public ResponseEntity<BaseResponse<UserEventLogResponseDto>> createUserEventLog(
      @AuthenticationPrincipal Long userId,
      @RequestBody UserEventLogCreateRequestDto dto
  ) {
    if (userId == null) {
      throw new CustomException(GlobalErrorCode.UNAUTHORIZED);
    }

    UserEventLogResponseDto response = userEventLogService.createUserEventLog(userId, dto);
    return ResponseEntity.ok(
        BaseResponse.success("사용자 행동 로그 저장 성공", response)
    );
  }

  // -----------------------------------------------------------------
  // 2) 사용자 행동 로그 목록 조회 (필터 + 페이징)
  // -----------------------------------------------------------------
  @Operation(summary = "사용자 행동 로그 목록 조회",
      description = """
          사용자 행동 로그를 검색 조건과 함께 조회합니다.
          - userId: 특정 사용자 로그만 조회
          - eventType: VIEW / FAVORITE / BOOKED / REVIEW_WRITE 등
          - targetType: PERFORMANCE / PLACE / REVIEW
          - targetId: 특정 공연/공연장/리뷰에 대한 로그만 조회
          - startDate, endDate: yyyy-MM-dd 형식의 날짜 범위
          - page, size: 페이징 처리 (1부터 시작)
          """)
  @GetMapping
  public ResponseEntity<BaseResponse<UserEventLogListResponseDto>> getUserEventLogs(
      @ModelAttribute UserEventLogSearchRequestDto searchDto
  ) {
    UserEventLogListResponseDto response = userEventLogService.searchUserEventLogs(searchDto);
    return ResponseEntity.ok(
        BaseResponse.success("사용자 행동 로그 목록 조회 성공", response)
    );
  }
}
