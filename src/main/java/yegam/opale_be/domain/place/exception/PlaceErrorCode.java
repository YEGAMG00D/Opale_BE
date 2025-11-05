package yegam.opale_be.domain.place.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import yegam.opale_be.global.exception.model.BaseErrorCode;

/**
 * PlaceErrorCode
 * - 공연장(Place) 도메인 예외 코드 정의
 */
@Getter
@AllArgsConstructor
public enum PlaceErrorCode implements BaseErrorCode {

  // 🔹 기본 조회 관련
  PLACE_NOT_FOUND("PLACE_4001", "존재하지 않는 공연장입니다.", HttpStatus.NOT_FOUND),
  PLACE_LIST_EMPTY("PLACE_4002", "공연장 목록이 존재하지 않습니다.", HttpStatus.NO_CONTENT),

  // 🔹 검색/요청 파라미터 관련
  INVALID_SEARCH_QUERY("PLACE_4003", "유효하지 않은 검색 조건입니다.", HttpStatus.BAD_REQUEST),

  // 🔹 좌표 기반 검색 관련
  INVALID_COORDINATE("PLACE_4004", "좌표 값이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
  RADIUS_TOO_LARGE("PLACE_4005", "검색 반경이 너무 큽니다. (최대 10km 이하)", HttpStatus.BAD_REQUEST),

  // 🔹 관련 데이터 조회 실패
  PLACE_STAGE_NOT_FOUND("PLACE_4006", "해당 공연장의 공연관 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  PLACE_PERFORMANCE_NOT_FOUND("PLACE_4007", "해당 공연장에서 진행되는 공연 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  PLACE_FACILITY_NOT_FOUND("PLACE_4008", "해당 공연장의 편의시설 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

  // 🔹 서버 내부 오류
  PLACE_DATA_ACCESS_ERROR("PLACE_5001", "공연장 데이터 접근 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
