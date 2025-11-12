package yegam.opale_be.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import yegam.opale_be.global.exception.model.BaseErrorCode;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements BaseErrorCode {

  // 공통 예외
  INVALID_INPUT_VALUE("GLOBAL_4001", "유효하지 않은 입력입니다.", HttpStatus.BAD_REQUEST),
  RESOURCE_NOT_FOUND("GLOBAL_4002", "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  INTERNAL_SERVER_ERROR("GLOBAL_5001", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  MISSING_REQUEST_PARAM("GLOBAL_4003", "요청에 필요한 파라미터가 없습니다.", HttpStatus.BAD_REQUEST),
  INVALID_JSON_FORMAT("GLOBAL_4004", "JSON 형식이 잘못되었습니다.", HttpStatus.BAD_REQUEST),
  URL_NOT_FOUND("GLOBAL_4005", "요청한 URL은 존재하지 않습니다.", HttpStatus.NOT_FOUND),

  // 인증/인가 관련
  UNAUTHORIZED("GLOBAL_4006", "로그인이 필요합니다.", HttpStatus.UNAUTHORIZED),
  FORBIDDEN("GLOBAL_4007", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),

  // JWT 관련
  JWT_EXPIRED("GLOBAL_4008", "JWT 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
  JWT_INVALID("GLOBAL_4009", "유효하지 않은 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
