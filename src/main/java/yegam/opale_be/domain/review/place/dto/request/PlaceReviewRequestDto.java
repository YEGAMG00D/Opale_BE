package yegam.opale_be.domain.review.place.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import yegam.opale_be.domain.review.common.ReviewType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PlaceReviewRequest DTO", description = "공연장 리뷰 작성/수정 요청 DTO")
public class PlaceReviewRequestDto {

  @Schema(description = "리뷰 제목", example = "시설이 깔끔하고 접근성이 좋아요!")
  private String title;

  @Schema(description = "리뷰 내용", example = "좌석이 편하고 화장실도 가까워서 좋았어요.")
  private String contents;

  @Schema(description = "평점 (1~5)", example = "4.8")
  private Float rating;

  @Schema(description = "리뷰 타입 (PLACE)", example = "PLACE")
  private ReviewType reviewType;

  @Schema(description = "공연장 ID", example = "PL00123")
  private String placeId;
}
