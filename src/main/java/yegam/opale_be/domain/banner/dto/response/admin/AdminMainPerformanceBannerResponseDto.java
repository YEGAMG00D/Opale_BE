package yegam.opale_be.domain.banner.dto.response.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "AdminMainPerformanceBannerResponseDto", description = "관리자 메인 공연 배너 응답 DTO")
public class AdminMainPerformanceBannerResponseDto {

  @Schema(description = "배너 ID")
  private Long bannerId;

  @Schema(description = "공연 ID")
  private String performanceId;

  @Schema(description = "공연명")
  private String performanceTitle;

  @Schema(description = "노출 순서")
  private Integer displayOrder;

  @Schema(description = "활성화 여부")
  private Boolean isActive;
}
