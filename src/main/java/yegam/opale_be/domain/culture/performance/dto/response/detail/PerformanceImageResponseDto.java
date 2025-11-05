package yegam.opale_be.domain.culture.performance.dto.response.detail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 공연 추가 수집 이미지 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PerformanceImageResponse DTO", description = "공연 관련 추가 수집 이미지 응답 DTO")
public class PerformanceImageResponseDto {

  @Schema(description = "수집 이미지 고유의 ID", example = "1")
  private Long performanceImageId;

  @Schema(description = "이미지 URL", example = "https://example.com/seatmap.jpg")
  private String imageUrl;

  @Schema(description = "이미지 유형", example = "DISCOUNT, SEAT, NOTICE 등")
  private String imageType;
}
