package yegam.opale_be.domain.culture.performance.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import yegam.opale_be.global.exception.model.BaseErrorCode;

/**
 * PerformanceErrorCode
 * - 공연 관련 도메인 예외 코드 정의
 */
@Getter
@AllArgsConstructor
public enum PerformanceErrorCode implements BaseErrorCode {

  // 🔹 기본 조회 관련
  PERFORMANCE_NOT_FOUND("PERFORMANCE_4001", "존재하지 않는 공연입니다.", HttpStatus.NOT_FOUND),
  PERFORMANCE_LIST_EMPTY("PERFORMANCE_4002", "공연 목록이 존재하지 않습니다.", HttpStatus.NO_CONTENT),

  // 🔹 검색 관련
  INVALID_SEARCH_QUERY("PERFORMANCE_4003", "유효하지 않은 검색 조건입니다.", HttpStatus.BAD_REQUEST),

  // 🔹 좌표 기반 검색 관련
  INVALID_COORDINATE("PERFORMANCE_4004", "좌표 값이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
  RADIUS_TOO_LARGE("PERFORMANCE_4005", "검색 반경이 너무 큽니다. (최대 10km 이하)", HttpStatus.BAD_REQUEST),

  // 🔹 관련 데이터 조회 실패
  PERFORMANCE_RELATION_NOT_FOUND("PERFORMANCE_4006", "해당 공연의 예매처 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  PERFORMANCE_VIDEO_NOT_FOUND("PERFORMANCE_4007", "해당 공연의 영상 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  PERFORMANCE_IMAGE_NOT_FOUND("PERFORMANCE_4008", "해당 공연의 이미지 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

  // 🔹 서버 내부 오류
  PERFORMANCE_DATA_ACCESS_ERROR("PERFORMANCE_5001", "공연 데이터 접근 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
