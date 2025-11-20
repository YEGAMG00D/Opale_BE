package yegam.opale_be.domain.review.place.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import yegam.opale_be.domain.review.common.ReviewType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PlaceReviewResponse DTO", description = "공연장 리뷰 단건 응답 DTO")
public class PlaceReviewResponseDto {

  @Schema(description = "리뷰 ID", example = "32")
  private Long placeReviewId;

  @Schema(description = "공연장 ID", example = "PL00123")
  private String placeId;

  @Schema(description = "공연장명", example = "세종문화회관")
  private String placeName;

  @Schema(description = "주소", example = "서울특별시 종로구 세종대로 175")
  private String placeAddress;

  @Schema(description = "작성자 ID", example = "7")
  private Long userId;

  @Schema(description = "작성자 닉네임", example = "theater_love")
  private String nickname;

  @Schema(description = "리뷰 제목", example = "깔끔한 공연장")
  private String title;

  @Schema(description = "리뷰 내용", example = "좌석 간격이 넓어서 편했어요.")
  private String contents;

  @Schema(description = "평점", example = "4.5")
  private Float rating;

  @Schema(description = "리뷰 타입", example = "PLACE")
  private ReviewType reviewType;

  @Schema(description = "작성일")
  private LocalDateTime createdAt;

  @Schema(description = "수정일")
  private LocalDateTime updatedAt;
}
