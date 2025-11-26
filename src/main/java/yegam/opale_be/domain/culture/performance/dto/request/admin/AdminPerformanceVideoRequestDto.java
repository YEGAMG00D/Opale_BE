package yegam.opale_be.domain.culture.performance.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminPerformanceVideoRequestDto {

  @NotBlank
  private String youtubeVideoId;

  @NotBlank
  private String title;

  private String sourceUrl;
  private String thumbnailUrl;
  private String embedUrl;
}
