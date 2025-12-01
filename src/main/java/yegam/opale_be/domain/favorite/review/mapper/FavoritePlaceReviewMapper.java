package yegam.opale_be.domain.favorite.review.mapper;

import org.springframework.stereotype.Component;
import yegam.opale_be.domain.favorite.review.dto.response.FavoritePlaceReviewResponseDto;
import yegam.opale_be.domain.favorite.review.entity.FavoritePlaceReview;
import yegam.opale_be.domain.review.place.entity.PlaceReview;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 공연장 리뷰 관심 Mapper
 * - FavoritePlaceReview → FavoritePlaceReviewResponseDto 변환
 */
@Component
public class FavoritePlaceReviewMapper {

  /** ✅ 관심 엔티티 → 관심 DTO */
  public FavoritePlaceReviewResponseDto toResponseDto(FavoritePlaceReview favorite, boolean isLiked) {
    if (favorite == null) return null;

    PlaceReview review = favorite.getPlaceReview();

    // null 안전 처리
    String placeName = null;
    String placeId = null;
    if (review.getPlace() != null) {
      placeName = review.getPlace().getName();
      placeId = review.getPlace().getPlaceId();
    }

    return FavoritePlaceReviewResponseDto.builder()
        .placeReviewId(review.getPlaceReviewId())
        .placeId(placeId)
        .placeName(placeName)
        .title(review.getTitle())
        .contents(review.getContents())
        .rating(review.getRating())
        .nickname(review.getUser() != null ? review.getUser().getNickname() : null)
        .createdAt(review.getCreatedAt())
        .isLiked(isLiked)
        .reviewType(review.getReviewType())

        .build();
  }

  /** ✅ 관심 목록 List → DTO List */
  public List<FavoritePlaceReviewResponseDto> toResponseDtoList(List<FavoritePlaceReview> favorites) {
    if (favorites == null || favorites.isEmpty()) return Collections.emptyList();

    return favorites.stream()
        .map(f -> toResponseDto(f, true)) // 관심 목록이므로 true
        .collect(Collectors.toList());
  }
}
