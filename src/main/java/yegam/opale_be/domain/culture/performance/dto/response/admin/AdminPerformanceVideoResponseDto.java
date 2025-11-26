package yegam.opale_be.domain.culture.performance.dto.response.admin;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminPerformanceVideoResponseDto {

  private Long performanceVideoId;
  private String youtubeVideoId;
  private String title;
  private String sourceUrl;
  private String thumbnailUrl;
  private String embedUrl;
}
