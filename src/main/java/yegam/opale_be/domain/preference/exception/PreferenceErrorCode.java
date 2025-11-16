package yegam.opale_be.domain.preference.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import yegam.opale_be.global.exception.model.BaseErrorCode;

/**
 * PreferenceErrorCode
 * - 사용자 선호 임베딩 벡터 관련 예외 코드
 */
@Getter
@AllArgsConstructor
public enum PreferenceErrorCode implements BaseErrorCode {

  VECTOR_NOT_FOUND("PREF_4001", "사용자 선호 벡터가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  INVALID_VECTOR_FORMAT("PREF_4002", "임베딩 벡터 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),

  VECTOR_CREATE_FAILED("PREF_5001", "선호 벡터 생성 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  VECTOR_UPDATE_FAILED("PREF_5002", "선호 벡터 업데이트 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
