package yegam.opale_be.domain.review.performance.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import yegam.opale_be.domain.review.common.ReviewType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PerformanceReviewRequest DTO", description = "공연 리뷰 작성/수정 요청 DTO")
public class PerformanceReviewRequestDto {

  @Schema(description = "리뷰 제목", example = "정말 감동적인 공연이었어요!")
  private String title;

  @Schema(description = "리뷰 내용", example = "출연진들의 연기가 정말 훌륭했어요. 다음에도 또 볼래요!")
  private String contents;

  @Schema(description = "평점 (1~5)", example = "4.5")
  private Float rating;

  @Schema(description = "리뷰 타입 (AFTER, EXPECTATION)", example = "AFTER")
  private ReviewType reviewType;

  @Schema(description = "공연 ID", example = "PF12345")
  private String performanceId;

  @Schema(description = "티켓 인증 ID", example = "12")
  private Long ticketId;

}
