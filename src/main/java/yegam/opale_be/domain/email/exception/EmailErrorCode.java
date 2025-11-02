package yegam.opale_be.domain.email.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import yegam.opale_be.global.exception.model.BaseErrorCode;

@Getter
@AllArgsConstructor
public enum EmailErrorCode implements BaseErrorCode {

  INVALID_EMAIL_FORMAT("EMAIL_4001", "올바르지 않은 이메일 형식입니다.", HttpStatus.BAD_REQUEST),
  EMAIL_NOT_FOUND("EMAIL_4002", "인증 요청된 이메일을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  CODE_MISMATCH("EMAIL_4003", "인증번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
  CODE_EXPIRED("EMAIL_4004", "인증번호가 만료되었습니다.", HttpStatus.BAD_REQUEST),
  SEND_FAILED("EMAIL_5001", "이메일 전송에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
