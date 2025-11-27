package yegam.opale_be.domain.culture.performance.dto.response.admin;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AdminPerformanceVideoListResponseDto {

  private String performanceId;

  private String title;

  private int totalCount;

  private List<AdminPerformanceVideoResponseDto> videos;
}
