package yegam.opale_be.domain.report.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportTargetType {

  /** 채팅 메시지 신고 */
  CHAT_MESSAGE("채팅 메시지"),

  /** 공연 리뷰 신고 */
  PERFORMANCE_REVIEW("공연 리뷰"),

  /** 공연장 리뷰 신고 */
  PLACE_REVIEW("공연장 리뷰"),

  /** 특정 유저 자체에 대한 신고 */
  USER("사용자");

  private final String description;
}
