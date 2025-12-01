package yegam.opale_be.domain.favorite.review.mapper;

import org.springframework.stereotype.Component;
import yegam.opale_be.domain.favorite.review.dto.response.FavoritePerformanceReviewResponseDto;
import yegam.opale_be.domain.favorite.review.entity.FavoritePerformanceReview;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FavoritePerformanceReviewMapper {

  /** 관심 엔티티 → 관심 DTO */
  public FavoritePerformanceReviewResponseDto toResponseDto(FavoritePerformanceReview favorite, boolean isLiked) {
    if (favorite == null) return null;

    var review = favorite.getPerformanceReview();

    String performanceId = null;
    String performanceTitle = null;

    if (review.getPerformance() != null) {
      performanceId = review.getPerformance().getPerformanceId();
      performanceTitle = review.getPerformance().getTitle();
    }

    return FavoritePerformanceReviewResponseDto.builder()
        .performanceReviewId(review.getPerformanceReviewId())
        .performanceId(performanceId)
        .performanceTitle(performanceTitle)

        .title(review.getTitle())
        .contents(review.getContents())
        .rating(review.getRating())

        .nickname(review.getUser() != null ? review.getUser().getNickname() : null)
        .createdAt(review.getCreatedAt())

        .isLiked(isLiked)
        .reviewType(review.getReviewType())

        .build();
  }

  /** 관심 목록 List → DTO List */
  public List<FavoritePerformanceReviewResponseDto> toResponseDtoList(List<FavoritePerformanceReview> favorites) {
    if (favorites == null || favorites.isEmpty()) return Collections.emptyList();

    return favorites.stream()
        .map(f -> toResponseDto(f, true))
        .collect(Collectors.toList());
  }
}
