package yegam.opale_be.domain.culture.performance.dto.request.admin;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPerformanceVideoRequestDto {

  private String youtubeVideoId;

  private String title;

  private String thumbnailUrl;

  private String sourceUrl;

  private String embedUrl;
}
