package yegam.opale_be.domain.banner.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import yegam.opale_be.global.exception.model.BaseErrorCode;

@Getter
@AllArgsConstructor
public enum BannerErrorCode implements BaseErrorCode {

  BANNER_NOT_FOUND("BANNER_4001", "존재하지 않는 배너입니다.", HttpStatus.NOT_FOUND),
  BANNER_DATA_ERROR("BANNER_5001", "배너 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
