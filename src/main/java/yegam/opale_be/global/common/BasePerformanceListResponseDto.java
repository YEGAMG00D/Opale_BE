package yegam.opale_be.global.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;

/**
 * 공통 리스트 응답 DTO
 * (공연 ID + 공연명 + 총 개수 + 데이터 목록)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "BaseListResponse DTO", description = "공연 ID, 공연명, 총 개수, 데이터 목록을 포함하는 기본 리스트 응답 구조")
public class BasePerformanceListResponseDto<T> {

  @Schema(description = "공연 ID", example = "PF276569")
  private String performanceId;

  @Schema(description = "공연명", example = "레미제라블")
  private String title;


  @Schema(description = "총 항목 개수", example = "3")
  private int totalCount;

  @Schema(description = "데이터 목록")
  private List<T> items;
}
