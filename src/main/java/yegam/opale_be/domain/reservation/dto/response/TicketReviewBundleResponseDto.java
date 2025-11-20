package yegam.opale_be.domain.reservation.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import yegam.opale_be.domain.review.performance.dto.response.PerformanceReviewResponseDto;
import yegam.opale_be.domain.review.place.dto.response.PlaceReviewResponseDto;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketReviewBundleResponseDto {

  private Long ticketId;

  private PerformanceReviewResponseDto performanceReview; // 공연 리뷰 (없으면 null)

  private PlaceReviewResponseDto placeReview; // 공연장 리뷰 (없으면 null)
}
