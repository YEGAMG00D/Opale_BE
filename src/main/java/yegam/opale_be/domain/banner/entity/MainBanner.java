package yegam.opale_be.domain.banner.entity;

import jakarta.persistence.*;
import lombok.*;
import yegam.opale_be.global.common.BaseTimeEntity;

@Entity
@Table(name = "main_banners")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainBanner extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "banner_id", nullable = false)
  private Long bannerId;

  /** 배너 이미지 URL (S3) */
  @Column(name = "image_url", nullable = false, length = 500)
  private String imageUrl;

  /** 배너 메인 문구 */
  @Column(name = "title_text", length = 255)
  private String titleText;

  /** 배너 부제 */
  @Column(name = "subtitle_text", length = 255)
  private String subtitleText;

  /** 배너 설명 문구 */
  @Column(name = "description_text", length = 255)
  private String descriptionText;

  /** 공연 날짜 텍스트 */
  @Column(name = "date_text", length = 255)
  private String dateText;

  /** 공연 장소 텍스트 */
  @Column(name = "place_text", length = 255)
  private String placeText;

  /** 연결된 공연 ID (선택) */
  @Column(name = "performance_id", length = 20)
  private String performanceId;

  /** 클릭 시 이동할 URL */
  @Column(name = "link_url", length = 500)
  private String linkUrl;

  /** 메인 노출 여부 */
  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  /** 배너 정렬 순서 */
  @Column(name = "display_order", nullable = false)
  private Integer displayOrder;
}
