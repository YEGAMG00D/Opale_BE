package yegam.opale_be.domain.favorite.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 공연장 리뷰 관심 단건 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "FavoritePlaceReviewResponse DTO", description = "공연장 리뷰 관심 단건 응답 DTO")
public class FavoritePlaceReviewResponseDto {

  @Schema(description = "리뷰 ID", example = "32")
  private Long placeReviewId;

  // ✅ 추가됨
  @Schema(description = "공연장 ID", example = "PL12345")
  private String placeId;

  @Schema(description = "공연장명", example = "세종문화회관")
  private String placeName;

  @Schema(description = "리뷰 제목", example = "깔끔한 공연장")
  private String title;

  @Schema(description = "리뷰 내용", example = "좌석 간격이 넓어서 편했어요.")
  private String contents;

  @Schema(description = "평점", example = "4.5")
  private Float rating;

  @Schema(description = "작성자 닉네임", example = "theater_love")
  private String nickname;

  @Schema(description = "작성일")
  private LocalDateTime createdAt;

  @Schema(description = "관심 여부", example = "true")
  private Boolean isLiked;
}
