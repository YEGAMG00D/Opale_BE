package yegam.opale_be.domain.user.exception;

import yegam.opale_be.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
  DUPLICATE_EMAIL("USER_4001", "이미 존재하는 이메일입니다.", HttpStatus.BAD_REQUEST),
  USER_NOT_FOUND("USER_4002", "존재하지 않는 사용자입니다.", HttpStatus.NOT_FOUND),
  PASSWORD_NOT_MATCHED("USER_4003", "현재 비밀번호가 일치하지 않습니다.",  HttpStatus.BAD_REQUEST),
  JWT_INVALID("USER_4004", "유효하지 않은 사용자 토큰입니다.", HttpStatus.UNAUTHORIZED);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
