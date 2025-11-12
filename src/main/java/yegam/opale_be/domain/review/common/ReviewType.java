package yegam.opale_be.domain.review.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewType {

  /** 공연 리뷰 */
  AFTER("후기"),         // 공연 후 작성하는 후기
  EXPECTATION("기대평"), // 공연 전 기대평

  /** 공연장 리뷰 */
  PLACE("공연장 리뷰");

  private final String description;
}
