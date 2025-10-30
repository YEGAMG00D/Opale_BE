package yegam.opale_be.domain.culture.performance.entity;

import jakarta.persistence.*;
import lombok.*;
import yegam.opale_be.global.common.BaseTimeEntity;

@Entity
@Table(name = "performance_videos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceVideo extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "performance_video_id", nullable = false)
  private Long performanceVideoId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "performance_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_perf_video_performance"))
  private yegam.opale_be.domain.culture.performance.entity.Performance performance;

  @Column(name = "youtube_video_id", length = 50)
  private String youtubeVideoId;

  @Column(name = "title", length = 255)
  private String title;

  @Column(name = "thumbnail_url", length = 255)
  private String thumbnailUrl;

  @Column(name = "source_url", length = 255)
  private String sourceUrl;

  @Column(name = "embed_url", length = 255)
  private String embedUrl;
}
