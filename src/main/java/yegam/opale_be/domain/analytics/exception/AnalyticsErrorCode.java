package yegam.opale_be.domain.analytics.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import yegam.opale_be.global.exception.model.BaseErrorCode;

/**
 * AnalyticsErrorCode
 * - 사용자 행동 로그(Analytics) 도메인 예외 코드 정의
 */
@Getter
@AllArgsConstructor
public enum AnalyticsErrorCode implements BaseErrorCode {

  LOG_NOT_FOUND("ANALYTICS_4001", "요청한 로그 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  INVALID_EVENT_TYPE("ANALYTICS_4002", "유효하지 않은 이벤트 타입입니다.", HttpStatus.BAD_REQUEST),
  INVALID_TARGET_TYPE("ANALYTICS_4003", "유효하지 않은 타겟 타입입니다.", HttpStatus.BAD_REQUEST),
  INVALID_DATE_RANGE("ANALYTICS_4004", "검색 시작일이 종료일보다 이후일 수 없습니다.", HttpStatus.BAD_REQUEST),

  ANALYTICS_DATA_ACCESS_ERROR("ANALYTICS_5001", "로그 데이터 접근 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
