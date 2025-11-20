package yegam.opale_be.domain.review.place.mapper;

import org.springframework.stereotype.Component;
import yegam.opale_be.domain.review.place.dto.response.PlaceReviewResponseDto;
import yegam.opale_be.domain.review.place.entity.PlaceReview;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 공연장 리뷰 Entity ↔ DTO 변환 전담 Mapper
 */
@Component
public class PlaceReviewMapper {

  /** Entity → 단일 Response DTO */
  public PlaceReviewResponseDto toResponseDto(PlaceReview entity) {
    if (entity == null) return null;

    return PlaceReviewResponseDto.builder()
        .placeReviewId(entity.getPlaceReviewId())
        .placeId(entity.getPlace().getPlaceId())
        .placeName(entity.getPlace().getName())
        .placeAddress(entity.getPlace().getAddress())
        .userId(entity.getUser().getUserId())
        .nickname(entity.getUser().getNickname())
        .title(entity.getTitle())
        .contents(entity.getContents())
        .rating(entity.getRating())
        .reviewType(entity.getReviewType())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .build();
  }

  /** Entity List → DTO List */
  public List<PlaceReviewResponseDto> toResponseDtoList(List<PlaceReview> reviews) {
    return reviews.stream()
        .map(this::toResponseDto)
        .collect(Collectors.toList());
  }
}
