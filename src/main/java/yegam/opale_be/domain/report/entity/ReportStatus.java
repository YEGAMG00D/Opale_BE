package yegam.opale_be.domain.report.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportStatus {

  /** 접수된 상태 (기본값) */
  PENDING("처리 대기"),

  /** 신고 내용이 유효하여 조치한 상태 */
  APPROVED("신고 승인"),

  /** 신고 기각 */
  REJECTED("신고 반려");

  private final String description;
}
