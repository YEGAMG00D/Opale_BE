package yegam.opale_be.domain.discount.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import yegam.opale_be.global.common.BaseTimeEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "discount")
public class Discount extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** 인터파크 / 타임티켓 */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private DiscountSiteType site;

  /** 공연 제목 */
  @Column(nullable = false)
  private String title;

  /** 공연장 */
  private String venue;

  /** 이미지 URL */
  @Column(columnDefinition = "TEXT")
  private String imageUrl;

  /** 할인 타입 (타임딜, 조기예매, 프리뷰 등) */
  private String saleType;

  /** 할인율 (문자열로 저장: 20%, 75% 등) */
  private String discountPercent;

  /** 할인된 가격 (문자열: 12,000원 등) */
  private String discountPrice;

  /** 공연 시작일 */
  private LocalDate startDate;

  /** 공연 종료일 */
  private LocalDate endDate;

  /** 상세 링크 */
  @Column(columnDefinition = "TEXT")
  private String link;

  /** 배치 ID (하루 1회 크롤링 구분) */
  @Column(nullable = false)
  private String batchId;

  /** 🔥 할인 종료 시각 (프론트에서 타이머 표시용) */
  private LocalDateTime discountEndDatetime;
}
