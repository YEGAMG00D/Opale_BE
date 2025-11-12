package yegam.opale_be.domain.favorite.performance.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import yegam.opale_be.domain.culture.performance.entity.Performance;

import java.time.LocalDate;
import java.util.List;

/**
 * 공연 관심 목록 단건 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "FavoritePerformanceResponse DTO", description = "공연 관심 단건 응답 DTO")
public class FavoritePerformanceResponseDto {

  @Schema(description = "공연 ID", example = "PF12345")
  private String performanceId;

  @Schema(description = "공연명", example = "레미제라블")
  private String title;

  @Schema(description = "장르명", example = "뮤지컬")
  private String genrenm;

  @Schema(description = "공연 포스터 URL")
  private String poster;

  @Schema(description = "공연장명", example = "블루스퀘어 신한카드홀")
  private String placeName;

  @Schema(description = "시작일", example = "2025-10-01")
  private LocalDate startDate;

  @Schema(description = "종료일", example = "2025-12-31")
  private LocalDate endDate;

  @Schema(description = "평점(리뷰 합산)", example = "4.6")
  private Double rating;

  @Schema(description = "키워드 목록", example = "[\"휴먼드라마\",\"대서사\"]")
  private List<String> keywords;

  @Schema(description = "AI 시놉시스 요약")
  private String aiSummary;

  @Schema(description = "관심 여부", example = "true")
  private Boolean isLiked;


}
