package yegam.opale_be.domain.search.performance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PerformanceAutoCompleteResponseDto {

  @Schema(description = "공연 ID", example = "PF12345")
  private String performanceId;

  @Schema(description = "공연명", example = "레미제라블")
  private String title;

  @Schema(description = "공연장명", example = "블루스퀘어 신한카드홀")
  private String placeName;

  @Schema(description = "시작일", example = "2025-10-01")
  private LocalDate startDate;

  @Schema(description = "종료일", example = "2025-12-31")
  private LocalDate endDate;

}
