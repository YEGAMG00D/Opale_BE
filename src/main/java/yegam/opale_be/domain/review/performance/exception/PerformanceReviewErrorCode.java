package yegam.opale_be.domain.review.performance.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import yegam.opale_be.global.exception.model.BaseErrorCode;

@Getter
@AllArgsConstructor
public enum PerformanceReviewErrorCode implements BaseErrorCode {

  REVIEW_NOT_FOUND("PERF_REVIEW_4001", "해당 공연 리뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  REVIEW_ACCESS_DENIED("PERF_REVIEW_4002", "이 리뷰에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),
  PERFORMANCE_NOT_FOUND("PERF_REVIEW_4003", "해당 공연을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  INVALID_REVIEW_TYPE("PERF_REVIEW_4004", "유효하지 않은 리뷰 타입입니다.", HttpStatus.BAD_REQUEST),
  TICKET_REQUIRED("PERF_REVIEW_4005", "해당 공연의 예매 내역이 있어야 리뷰 작성이 가능합니다.", HttpStatus.BAD_REQUEST);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
