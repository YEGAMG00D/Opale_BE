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

    // ⭐ ticket null-safe + Lazy loading safe
    Long ticketId = null;
    String seatInfo = null;
    java.time.LocalDateTime performanceDate = null;

    if (entity.getTicket() != null) {
      try {
        ticketId = entity.getTicket().getTicketId();
        seatInfo = entity.getTicket().getSeatInfo();
        performanceDate = entity.getTicket().getPerformanceDate();
      } catch (Exception e) {
        // 잘못된 Proxy / ID=0 / Lazy 로딩 오류 방지
        ticketId = null;
        seatInfo = null;
        performanceDate = null;
      }
    }

    return PerformanceReviewResponseDto.builder()
        .performanceReviewId(entity.getPerformanceReviewId())
        .performanceId(entity.getPerformance().getPerformanceId())
        .performanceTitle(entity.getPerformance().getTitle())
        .poster(entity.getPerformance().getPoster())

        .userId(entity.getUser().getUserId())
        .nickname(entity.getUser().getNickname())

        .ticketId(ticketId)

        .performanceDate(performanceDate)
        .seatInfo(seatInfo)

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
