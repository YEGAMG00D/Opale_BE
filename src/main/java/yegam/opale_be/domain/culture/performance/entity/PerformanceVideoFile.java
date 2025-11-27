package yegam.opale_be.domain.culture.performance.entity;


import jakarta.persistence.*;
import lombok.*;
import yegam.opale_be.global.common.BaseTimeEntity;



@Entity
@Table(name = "performance_video_files")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceVideoFile extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long performanceVideoFileId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "performance_id", nullable = false)
  private Performance performance;

  @Column(nullable = false, length = 500)
  private String videoUrl;   // S3 URL

  @Column(nullable = false, length = 50)
  private String videoType;  // MP4, WEBM 등

  @Column
  private Long fileSize;

  @Column(length = 255)
  private String sourceUrl;  // 출처 있으면

  @Column(nullable = false)
  private Boolean isMain;    // 메인 영상 여부
}

