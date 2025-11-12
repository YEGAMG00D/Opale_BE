package yegam.opale_be.domain.chat.message.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import yegam.opale_be.global.exception.model.BaseErrorCode;

@Getter
@AllArgsConstructor
public enum ChatMessageErrorCode implements BaseErrorCode {

  CHAT_ROOM_NOT_FOUND("CHAT_4001", "채팅방을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  USER_NOT_FOUND("CHAT_4002", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  UNAUTHORIZED("CHAT_4003", "로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
