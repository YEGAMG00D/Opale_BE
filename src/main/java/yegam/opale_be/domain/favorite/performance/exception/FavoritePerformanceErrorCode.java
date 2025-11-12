package yegam.opale_be.domain.favorite.performance.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import yegam.opale_be.global.exception.model.BaseErrorCode;

/**
 * FavoritePerformanceErrorCode
 * - 공연 관심(Favorite) 도메인 예외 코드 정의
 */
@Getter
@AllArgsConstructor
public enum FavoritePerformanceErrorCode implements BaseErrorCode {

  FAVORITE_NOT_FOUND("FAVORITE_PERFORMANCE_4001", "관심 공연 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
