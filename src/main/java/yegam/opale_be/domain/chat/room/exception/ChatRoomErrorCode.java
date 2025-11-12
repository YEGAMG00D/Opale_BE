package yegam.opale_be.domain.chat.room.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import yegam.opale_be.global.exception.model.BaseErrorCode;

/**
 * ChatRoomErrorCode
 * - 채팅방 도메인 예외 코드 정의
 */
@Getter
@AllArgsConstructor
public enum ChatRoomErrorCode implements BaseErrorCode {

  ROOM_NOT_FOUND("CHATROOM_4001", "존재하지 않는 채팅방입니다.", HttpStatus.NOT_FOUND),
  CREATOR_NOT_FOUND("CHATROOM_4002", "방 개설자 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  PERFORMANCE_NOT_FOUND("CHATROOM_4003", "연관된 공연 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  INVALID_ROOM_TYPE("CHATROOM_4004", "유효하지 않은 채팅방 타입입니다.", HttpStatus.BAD_REQUEST),
  INVALID_ROOM_PASSWORD("CHATROOM_4005", "비공개방 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
  ROOM_CREATE_FAILED("CHATROOM_5001", "채팅방 생성 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
