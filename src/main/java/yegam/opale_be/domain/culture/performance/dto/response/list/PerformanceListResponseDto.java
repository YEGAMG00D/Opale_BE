package yegam.opale_be.domain.culture.performance.dto.response.list;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;

/**
 * 공연 목록 응답 DTO
 * - 총 공연 개수, 페이지 정보, 공연 목록 데이터 포함
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PerformanceListResponse DTO", description = "공연 목록 및 페이지 정보 응답 DTO")
public class PerformanceListResponseDto {

  @Schema(description = "총 공연 수", example = "839")
  private long totalCount;

  @Schema(description = "현재 페이지 (1부터 시작)", example = "1")
  private int currentPage;

  @Schema(description = "페이지당 공연 개수", example = "20")
  private int pageSize;

  @Schema(description = "전체 페이지 수", example = "42")
  private int totalPages;

  @Schema(description = "다음 페이지 존재 여부", example = "true")
  private boolean hasNext;

  @Schema(description = "이전 페이지 존재 여부", example = "false")
  private boolean hasPrev;

  @Schema(description = "공연 목록 데이터")
  private List<PerformanceResponseDto> performances;
}
