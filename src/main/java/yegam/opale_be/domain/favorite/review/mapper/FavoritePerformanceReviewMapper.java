package yegam.opale_be.domain.favorite.review.mapper;

import org.springframework.stereotype.Component;
import yegam.opale_be.domain.favorite.review.dto.response.FavoritePerformanceReviewResponseDto;
import yegam.opale_be.domain.review.performance.entity.PerformanceReview;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 공연 리뷰 관심 Mapper
 * - Entity ↔ DTO 변환 전담
 */
@Component
public class FavoritePerformanceReviewMapper {

  /** 공연 리뷰 Entity → 관심 Response DTO */
  public FavoritePerformanceReviewResponseDto toResponseDto(PerformanceReview entity, boolean isLiked) {
    if (entity == null) return null;

    // ✅ 공연 정보가 null일 수도 있으므로 안전하게 처리
    String performanceId = null;
    String performanceTitle = null;
    if (entity.getPerformance() != null) {
      performanceId = entity.getPerformance().getPerformanceId();
      performanceTitle = entity.getPerformance().getTitle();
    }

    return FavoritePerformanceReviewResponseDto.builder()
        .performanceReviewId(entity.getPerformanceReviewId())
        .performanceId(performanceId)
        .performanceTitle(performanceTitle)
        .title(entity.getTitle())
        .contents(entity.getContents())
        .rating(entity.getRating())
        .nickname(entity.getUser() != null ? entity.getUser().getNickname() : null)
        .createdAt(entity.getCreatedAt())
        .isLiked(isLiked)
        .build();
  }

  /** 공연 리뷰 Entity List → 관심 DTO List */
  public List<FavoritePerformanceReviewResponseDto> toResponseDtoList(List<PerformanceReview> entities) {
    if (entities == null || entities.isEmpty()) return Collections.emptyList();
    return entities.stream()
        .map(e -> toResponseDto(e, true)) // 관심 목록이므로 isLiked=true
        .collect(Collectors.toList());
  }
}
