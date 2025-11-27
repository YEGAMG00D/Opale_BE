package yegam.opale_be.domain.culture.performance.dto.response.admin;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminPerformanceVideoResponseDto {

  private Long performanceVideoId;

  private String youtubeVideoId;

  private String title;

  private String thumbnailUrl;

  private String sourceUrl;

  private String embedUrl;
}
