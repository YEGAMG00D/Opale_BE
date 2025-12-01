package yegam.opale_be.domain.banner.entity;

import jakarta.persistence.*;
import lombok.*;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.global.common.BaseTimeEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "main_content_banner")
public class MainContentBanner extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "content_banner_id")
  private Long contentBannerId;

  /** ✅ 배너 타이틀 */
  @Column(name = "title", length = 100, nullable = false)
  private String title;

  /** ✅ 배너 내용 (hover 시 노출) */
  @Lob
  @Column(name = "content", columnDefinition = "TEXT", nullable = false)
  private String content;

  /** ✅ 배너 이미지 URL */
  @Column(name = "image_url", length = 500, nullable = true)
  private String imageUrl;

  /** ✅ 클릭 시 이동할 주소 (선택) */
  @Column(name = "link_url", length = 500)
  private String linkUrl;

  /** ✅ 연결된 공연 (선택) */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "performance_id")
  private Performance performance;

  /** ✅ 메인 노출 여부 */
  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  /** ✅ 노출 순서 */
  @Column(name = "display_order", nullable = false)
  private Integer displayOrder;
}
