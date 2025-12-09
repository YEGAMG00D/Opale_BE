package yegam.opale_be.domain.search.performance.mapper;

import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.search.performance.dto.PerformanceAutoCompleteResponseDto;

public class PerformanceAutoCompleteMapper {

  public static PerformanceAutoCompleteResponseDto toDto(Performance p) {
    return new PerformanceAutoCompleteResponseDto(
        p.getPerformanceId(),
        p.getTitle(),
        p.getPlaceName(),
        p.getStartDate() != null ? p.getStartDate().toLocalDate() : null,
        p.getEndDate() != null ? p.getEndDate().toLocalDate() : null
    );
  }

}
