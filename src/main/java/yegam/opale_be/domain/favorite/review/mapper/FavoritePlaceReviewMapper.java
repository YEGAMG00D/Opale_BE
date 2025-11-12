package yegam.opale_be.domain.favorite.review.mapper;

import org.springframework.stereotype.Component;
import yegam.opale_be.domain.favorite.review.dto.response.FavoritePlaceReviewResponseDto;
import yegam.opale_be.domain.review.place.entity.PlaceReview;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 공연장 리뷰 관심 Mapper
 * - Entity ↔ DTO 변환 전담
 */
@Component
public class FavoritePlaceReviewMapper {

  /** 공연장 리뷰 Entity → 관심 Response DTO */
  public FavoritePlaceReviewResponseDto toResponseDto(PlaceReview entity, boolean isLiked) {
    if (entity == null) return null;

    // ✅ null 안전 처리
    String placeName = null;
    String placeId = null;
    if (entity.getPlace() != null) {
      placeName = entity.getPlace().getName();
      placeId = entity.getPlace().getPlaceId(); // ✅ 추가됨
    }

    return FavoritePlaceReviewResponseDto.builder()
        .placeReviewId(entity.getPlaceReviewId())
        .placeId(placeId) // ✅ 추가됨
        .placeName(placeName)
        .title(entity.getTitle())
        .contents(entity.getContents())
        .rating(entity.getRating())
        .nickname(entity.getUser() != null ? entity.getUser().getNickname() : null)
        .createdAt(entity.getCreatedAt())
        .isLiked(isLiked)
        .build();
  }

  /** 공연장 리뷰 Entity List → 관심 DTO List */
  public List<FavoritePlaceReviewResponseDto> toResponseDtoList(List<PlaceReview> entities) {
    if (entities == null) return Collections.emptyList();
    return entities.stream()
        .map(e -> toResponseDto(e, true)) // 관심 목록이므로 true
        .collect(Collectors.toList());
  }
}
