package yegam.opale_be.domain.culture.entity;


import jakarta.persistence.*;
import lombok.*;
import yegam.opale_be.global.common.BaseTimeEntity;

@Entity
@Table(name = "performance_info_images")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceInfoImage extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "info_img_id", nullable = false)
  private Long infoImgId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "performance_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_info_image_performance"))
  private Performance performance;

  @Column(name = "image_url", length = 255)
  private String imageUrl;

  @Column(name = "order_index")
  private Integer orderIndex;
}
