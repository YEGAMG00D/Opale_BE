package yegam.opale_be.domain.review.place.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import yegam.opale_be.global.exception.model.BaseErrorCode;

@Getter
@AllArgsConstructor
public enum PlaceReviewErrorCode implements BaseErrorCode {

  REVIEW_NOT_FOUND("PLACE_REVIEW_4001", "해당 공연장 리뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  REVIEW_ACCESS_DENIED("PLACE_REVIEW_4002", "이 리뷰에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),
  PLACE_NOT_FOUND("PLACE_REVIEW_4003", "해당 공연장을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
