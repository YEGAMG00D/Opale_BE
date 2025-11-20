package yegam.opale_be.domain.review.performance.mapper;

import org.springframework.stereotype.Component;
import yegam.opale_be.domain.review.performance.dto.response.PerformanceReviewResponseDto;
import yegam.opale_be.domain.review.performance.entity.PerformanceReview;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PerformanceReviewMapper {

  /** Entity → 단일 Response DTO */
  public PerformanceReviewResponseDto toResponseDto(PerformanceReview entity) {
    if (entity == null) return null;

    return PerformanceReviewResponseDto.builder()
        .performanceReviewId(entity.getPerformanceReviewId())
        .performanceId(entity.getPerformance().getPerformanceId())
        .performanceTitle(entity.getPerformance().getTitle())
        .poster(entity.getPerformance().getPoster())
        .userId(entity.getUser().getUserId())
        .nickname(entity.getUser().getNickname())
        .performanceDate(entity.getTicket().getPerformanceDate())
        .seatInfo(entity.getTicket().getSeatInfo())
        .title(entity.getTitle())
        .contents(entity.getContents())
        .rating(entity.getRating())
        .reviewType(entity.getReviewType())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .build();
  }

  /** Entity List → DTO List */
  public List<PerformanceReviewResponseDto> toResponseDtoList(List<PerformanceReview> reviews) {
    return reviews.stream()
        .map(this::toResponseDto)
        .collect(Collectors.toList());
  }
}
