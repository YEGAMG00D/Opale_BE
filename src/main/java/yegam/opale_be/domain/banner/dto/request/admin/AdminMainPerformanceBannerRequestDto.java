package yegam.opale_be.domain.banner.dto.request.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "AdminMainPerformanceBannerRequestDto", description = "관리자 메인 공연 배너 등록 요청 DTO")
public class AdminMainPerformanceBannerRequestDto {

  @Schema(description = "연결할 공연 ID", example = "PF264321")
  private String performanceId;

  @Schema(description = "노출 순서", example = "1")
  private Integer displayOrder;

  @Schema(description = "활성화 여부", example = "true")
  private Boolean isActive;
}
