package yegam.opale_be.domain.report.entity;

import jakarta.persistence.*;
import lombok.*;
import yegam.opale_be.global.common.BaseTimeEntity;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "report_id")
  private Long reportId;

  /** 신고자 ID (User FK) */
  @Column(nullable = false)
  private Long reporterId;

  /** 신고 당한 대상 유저 ID */
  @Column(nullable = false)
  private Long targetUserId;

  /** 신고 대상 타입 (채팅, 리뷰, 유저 등) */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private ReportTargetType targetType;

  /** 신고 대상의 ID (예: 채팅 메시지 ID, 리뷰 ID 등) */
  @Column(nullable = false)
  private Long targetId;

  /** 간단 사유 (예: 욕설, 스팸, 광고 등) */
  @Column(nullable = false, length = 255)
  private String reason;

  /** 상세 설명 (선택) */
  @Lob
  private String detail;

  /** 처리 상태 */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ReportStatus status;

  /** 관리자 메모 (처리 결과 등) */
  @Column(length = 500)
  private String adminMemo;
}
