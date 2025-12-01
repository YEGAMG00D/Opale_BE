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
  PASSWORD_NOT_MATCHED("USER_4003", "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
  CURRENT_PASSWORD_NOT_MATCHED("USER_4004", "현재 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),

  JWT_INVALID("USER_4005", "유효하지 않은 사용자 토큰입니다.", HttpStatus.UNAUTHORIZED),
  USER_DELETED("USER_4006", "탈퇴한 사용자입니다.", HttpStatus.FORBIDDEN),

  REFRESH_TOKEN_NOT_FOUND("USER_4007", "저장된 Refresh Token이 없습니다.", HttpStatus.UNAUTHORIZED),
  REFRESH_TOKEN_MISMATCH("USER_4008", "유효하지 않은 Refresh Token입니다.", HttpStatus.UNAUTHORIZED),


  DUPLICATE_NICKNAME("USER_4009", "이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT),

  EMAIL_NOT_VERIFIED("USER_4010", "이메일 인증이 완료되지 않았습니다.", HttpStatus.BAD_REQUEST);


  private final String code;
  private final String message;
  private final HttpStatus status;
}
