package yegam.opale_be.domain.favorite.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 공연 리뷰 관심 목록 조회 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "FavoritePerformanceReviewListRequest DTO", description = "공연 리뷰 관심 목록 조회 요청 DTO")
public class FavoritePerformanceReviewListRequestDto {

  @Schema(description = "현재 페이지 (1부터 시작)", example = "1")
  private Integer page;

  @Schema(description = "페이지당 리뷰 개수", example = "20")
  private Integer size;
}
