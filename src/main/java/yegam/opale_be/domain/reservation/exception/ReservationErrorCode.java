package yegam.opale_be.domain.reservation.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import yegam.opale_be.global.exception.model.BaseErrorCode;

@Getter
@AllArgsConstructor
public enum ReservationErrorCode implements BaseErrorCode {

  TICKET_NOT_FOUND("RESERVATION_4001", "존재하지 않는 티켓 인증 내역입니다.", HttpStatus.NOT_FOUND),
  UNAUTHORIZED_ACCESS("RESERVATION_4002", "본인 소유의 티켓만 수정 또는 삭제할 수 있습니다.", HttpStatus.FORBIDDEN),
  INVALID_TICKET_DATA("RESERVATION_4003", "티켓 정보가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
  OCR_FAIL("RESERVATION_4004", "티켓 이미지 인식에 실패했습니다.", HttpStatus.BAD_REQUEST);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
