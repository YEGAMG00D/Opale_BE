package yegam.opale_be.domain.report.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import yegam.opale_be.global.exception.model.BaseErrorCode;

/**
 * ReportErrorCode
 * - 신고(Report) 도메인 전용 예외 코드 정의
 */
@Getter
@AllArgsConstructor
public enum ReportErrorCode implements BaseErrorCode {

  REPORT_NOT_FOUND(
      "REPORT_4001",
      "요청한 신고 정보를 찾을 수 없습니다.",
      HttpStatus.NOT_FOUND
  ),

  INVALID_REPORT_STATUS(
      "REPORT_4002",
      "유효하지 않은 신고 상태입니다.",
      HttpStatus.BAD_REQUEST
  ),

  DUPLICATE_REPORT(
      "REPORT_4003",
      "이미 신고한 대상입니다.",
      HttpStatus.CONFLICT
  ),

  SELF_REPORT_NOT_ALLOWED(
      "REPORT_4004",
      "자기 자신은 신고할 수 없습니다.",
      HttpStatus.BAD_REQUEST
  ),

  REPORT_ACCESS_DENIED(
      "REPORT_4005",
      "신고 처리 권한이 없습니다.",
      HttpStatus.FORBIDDEN
  ),

  REPORT_PROCESSING_ERROR(
      "REPORT_5001",
      "신고 처리 중 서버 오류가 발생했습니다.",
      HttpStatus.INTERNAL_SERVER_ERROR
  );

  private final String code;
  private final String message;
  private final HttpStatus status;
}
