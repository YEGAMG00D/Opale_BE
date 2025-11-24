package yegam.opale_be.domain.discount.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import yegam.opale_be.global.exception.model.BaseErrorCode;

@Getter
@AllArgsConstructor
public enum DiscountErrorCode implements BaseErrorCode {

  DISCOUNT_NOT_FOUND("DISCOUNT_4001", "할인 공연 데이터가 존재하지 않습니다.", HttpStatus.NO_CONTENT),
  BATCH_NOT_FOUND("DISCOUNT_4002", "유효한 할인 데이터 batch가 없습니다.", HttpStatus.NOT_FOUND),
  DISCOUNT_DATA_ACCESS_ERROR("DISCOUNT_5001", "할인 공연 데이터 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
