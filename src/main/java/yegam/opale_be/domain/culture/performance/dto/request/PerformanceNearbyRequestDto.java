package yegam.opale_be.domain.culture.performance.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PerformanceNearbyRequest DTO", description = "좌표 기반 근처 공연 조회 요청 DTO")
public class PerformanceNearbyRequestDto {

  @Schema(description = "현재 위도", example = "37.5665")
  private BigDecimal latitude;

  @Schema(description = "현재 경도", example = "126.9780")
  private BigDecimal longitude;

  @Schema(description = "검색 반경 (단위: m)", example = "2000")
  private Integer radius;

  @Schema(description = "페이지 번호", example = "1")
  private Integer page;

  @Schema(description = "페이지당 항목 수", example = "20")
  private Integer size;
}
