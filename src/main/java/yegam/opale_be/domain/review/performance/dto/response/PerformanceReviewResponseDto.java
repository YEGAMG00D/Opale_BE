package yegam.opale_be.domain.review.performance.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import yegam.opale_be.domain.review.common.ReviewType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PerformanceReviewResponse DTO", description = "공연 리뷰 단건 응답 DTO")
public class PerformanceReviewResponseDto {

  @Schema(description = "리뷰 ID", example = "101")
  private Long performanceReviewId;

  @Schema(description = "공연 ID", example = "PF12345")
  private String performanceId;

  @Schema(description = "공연명", example = "레미제라블")
  private String performanceTitle;

  @Schema(description = "공연 포스터 URL")
  private String poster;

  @Schema(description = "작성자 ID", example = "7")
  private Long userId;

  @Schema(description = "작성자 닉네임", example = "musical_fan_01")
  private String nickname;

  @Schema(description = "리뷰 제목", example = "감동적인 무대였어요!")
  private String title;

  @Schema(description = "리뷰 내용", example = "출연진의 연기가 대단했습니다.")
  private String contents;

  @Schema(description = "평점", example = "5.0")
  private Float rating;

  @Schema(description = "리뷰 타입", example = "AFTER")
  private ReviewType reviewType;

  @Schema(description = "작성일")
  private LocalDateTime createdAt;

  @Schema(description = "수정일")
  private LocalDateTime updatedAt;
}
