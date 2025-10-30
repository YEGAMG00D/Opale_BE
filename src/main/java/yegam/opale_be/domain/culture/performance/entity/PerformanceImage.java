package yegam.opale_be.domain.culture.performance.entity;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "performance_images")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "performance_image_id", nullable = false)
  private Long performanceImageId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "performance_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_perfimg_perf"))
  private Performance performance;

  @Column(name = "image_url", length = 255)
  private String imageUrl;

  @Enumerated(EnumType.STRING)
  @Column(name = "image_type", length = 20)
  private ImageType imageType;

  @Column(name = "created_at")
  private java.time.LocalDateTime createdAt;

  @Column(name = "source_url", length = 255)
  private String sourceUrl;

  public enum ImageType {
    DISCOUNT, SEAT, NOTICE, 기타
  }
}

