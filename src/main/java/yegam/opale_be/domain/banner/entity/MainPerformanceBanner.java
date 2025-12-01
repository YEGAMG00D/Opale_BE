package yegam.opale_be.domain.banner.entity;


import jakarta.persistence.*;
import lombok.*;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.global.common.BaseTimeEntity;



@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "main_performance_banner")
@Builder
@AllArgsConstructor
public class MainPerformanceBanner extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long bannerId;

  /** 연결된 공연 */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "performance_id", nullable = false)
  private Performance performance;

  /** 메인 노출 여부 */
  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  /** 메인 배너 정렬 순서 */
  @Column(name = "display_order", nullable = false)
  private Integer displayOrder;

}

