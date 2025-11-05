package yegam.opale_be.domain.place.dto.response.list;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PlaceListResponse DTO", description = "공연장 목록 조회 응답 DTO")
public class PlaceListResponseDto {

  @Schema(description = "총 공연장 개수", example = "123")
  private long totalCount;

  @Schema(description = "현재 페이지", example = "1")
  private int currentPage;

  @Schema(description = "페이지당 항목 수", example = "20")
  private int pageSize;

  @Schema(description = "총 페이지 수", example = "7")
  private int totalPages;

  @Schema(description = "다음 페이지 존재 여부", example = "true")
  private boolean hasNext;

  @Schema(description = "이전 페이지 존재 여부", example = "false")
  private boolean hasPrev;

  @Schema(description = "공연장 목록 데이터")
  private List<PlaceSummaryResponseDto> places;
}
