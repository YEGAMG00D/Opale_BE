package yegam.opale_be.domain.report.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import yegam.opale_be.domain.report.dto.request.ReportCreateRequestDto;
import yegam.opale_be.domain.report.dto.request.ReportStatusUpdateRequestDto;
import yegam.opale_be.domain.report.dto.response.ReportDetailResponseDto;
import yegam.opale_be.domain.report.dto.response.ReportListResponseDto;
import yegam.opale_be.domain.report.entity.ReportStatus;
import yegam.opale_be.domain.report.service.ReportService;
import yegam.opale_be.global.response.BaseResponse;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Report API", description = "신고 관련 API")
public class ReportController {

  private final ReportService reportService;

  /**
   * 1) 신고 생성 (사용자용)
   */
  @Operation(summary = "신고 등록", description = "사용자가 신고를 등록합니다.")
  @PostMapping
  public ResponseEntity<BaseResponse<ReportDetailResponseDto>> createReport(
      @AuthenticationPrincipal Long userId,
      @Valid @RequestBody ReportCreateRequestDto requestDto
  ) {
    ReportDetailResponseDto response = reportService.createReport(userId, requestDto);
    return ResponseEntity.ok(BaseResponse.success("신고 등록 성공", response));
  }

  /**
   * 2) 신고 목록 조회 (운영자용)
   *    추후 SecurityConfig에서 ROLE_ADMIN 전용으로 제한하면 됨
   */
  @Operation(summary = "신고 목록 조회 (운영자용)", description = "신고 목록을 페이지네이션하여 조회합니다.")
  @GetMapping
  public ResponseEntity<BaseResponse<ReportListResponseDto>> getReports(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) ReportStatus status
  ) {
    ReportListResponseDto response = reportService.getReports(page, size, status);
    return ResponseEntity.ok(BaseResponse.success("신고 목록 조회 성공", response));
  }

  /**
   * 3) 신고 상세 조회 (운영자용)
   */
  @Operation(summary = "신고 상세 조회 (운영자용)", description = "특정 신고의 상세 정보를 조회합니다.")
  @GetMapping("/{reportId}")
  public ResponseEntity<BaseResponse<ReportDetailResponseDto>> getReportDetail(
      @PathVariable Long reportId
  ) {
    ReportDetailResponseDto response = reportService.getReportDetail(reportId);
    return ResponseEntity.ok(BaseResponse.success("신고 상세 조회 성공", response));
  }

  /**
   * 4) 신고 처리 (승인 / 반려 + 관리자 메모)
   *    예: PATCH /api/reports/1/status
   */
  @Operation(summary = "신고 처리 (운영자용)", description = "신고 상태를 승인/반려로 변경하고 관리자 메모를 저장합니다.")
  @PatchMapping("/{reportId}/status")
  public ResponseEntity<BaseResponse<ReportDetailResponseDto>> updateReportStatus(
      @AuthenticationPrincipal Long adminId,
      @PathVariable Long reportId,
      @Valid @RequestBody ReportStatusUpdateRequestDto requestDto
  ) {
    ReportDetailResponseDto response =
        reportService.updateReportStatus(adminId, reportId, requestDto);
    return ResponseEntity.ok(BaseResponse.success("신고 처리 성공", response));
  }
}
