package yegam.opale_be.domain.culture.performance.dto.response.detail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 공연 영상 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PerformanceVideoResponse DTO", description = "공연 관련 영상 응답 DTO")
public class PerformanceVideoResponseDto {

  @Schema(description = "공연 관련 영상 고유의 ID", example = "1")
  private Long performanceVideoId;

  @Schema(description = "유튜브 영상 ID", example = "AbCdEfGh123")
  private String youtubeVideoId;

  @Schema(description = "영상 제목", example = "레미제라블 하이라이트")
  private String title;

  @Schema(description = "영상 출처 유튜브 URL", example = "https://youtube.com/...")
  private String sourceUrl;

  @Schema(description = "썸네일 URL", example = "https://img.youtube.com/...")
  private String thumbnailUrl;

  @Schema(description = "임베드 URL", example = "https://youtube.com/embed/...")
  private String embedUrl;
}
