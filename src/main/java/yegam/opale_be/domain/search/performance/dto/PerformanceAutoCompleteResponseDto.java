package yegam.opale_be.domain.search.performance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PerformanceAutoCompleteResponseDto {

  private String performanceId;
  private String title;
}
