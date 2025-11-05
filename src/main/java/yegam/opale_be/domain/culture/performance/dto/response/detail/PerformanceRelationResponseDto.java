package yegam.opale_be.domain.culture.performance.dto.response.detail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 공연 예매처 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PerformanceRelationResponse DTO", description = "공연 예매처 정보 응답 DTO")
public class PerformanceRelationResponseDto {

  @Schema(description = "예매처 고유의 ID", example = "1")
  private Long relationId;

  @Schema(description = "예매처 이름", example = "인터파크")
  private String siteName;

  @Schema(description = "예매처 URL", example = "https://ticket.interpark.com/...")
  private String siteUrl;
}
