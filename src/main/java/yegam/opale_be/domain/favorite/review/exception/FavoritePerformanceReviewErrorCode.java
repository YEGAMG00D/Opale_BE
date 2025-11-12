package yegam.opale_be.domain.favorite.review.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import yegam.opale_be.global.exception.model.BaseErrorCode;

/**
 * FavoritePerformanceReviewErrorCode
 * - 공연 리뷰 관심(Favorite) 도메인 예외 코드 정의
 */
@Getter
@AllArgsConstructor
public enum FavoritePerformanceReviewErrorCode implements BaseErrorCode {

  FAVORITE_NOT_FOUND("FAVORITE_PERFORMANCE_REVIEW_4001", "해당 공연 리뷰 관심 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
