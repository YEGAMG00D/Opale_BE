package yegam.opale_be.domain.review.place.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PlaceReviewListResponse DTO", description = "공연장 리뷰 목록 응답 DTO (페이지 정보 포함)")
public class PlaceReviewListResponseDto {

  @Schema(description = "총 리뷰 수", example = "54")
  private long totalCount;

  @Schema(description = "현재 페이지 (1부터 시작)", example = "1")
  private int currentPage;

  @Schema(description = "페이지당 리뷰 수", example = "10")
  private int pageSize;

  @Schema(description = "전체 페이지 수", example = "6")
  private int totalPages;

  @Schema(description = "다음 페이지 존재 여부", example = "true")
  private boolean hasNext;

  @Schema(description = "이전 페이지 존재 여부", example = "false")
  private boolean hasPrev;

  @Schema(description = "리뷰 목록 데이터")
  private List<PlaceReviewResponseDto> reviews;
}
