package yegam.opale_be.domain.recommendation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "RecommendedPerformance DTO", description = "추천된 공연 데이터 DTO (기본 공연 목록 DTO와 구조 통일)")
public class RecommendedPerformanceDto {

  @Schema(description = "공연 ID", example = "PF12345")
  private String performanceId;

  @Schema(description = "공연명")
  private String title;

  @Schema(description = "장르명")
  private String genrenm;

  @Schema(description = "포스터 이미지 URL")
  private String poster;

  @Schema(description = "공연장명")
  private String placeName;

  @Schema(description = "시작일")
  private LocalDate startDate;

  @Schema(description = "종료일")
  private LocalDate endDate;

  @Schema(description = "평점 (null → 0)")
  private Double rating;

  @Schema(description = "키워드 목록")
  private List<String> keywords;

  @Schema(description = "AI 요약")
  private String aiSummary;

  @Schema(description = "Pinecone 유사도 점수 (null → 0)")
  private Double score;

  @Schema(description = "조회수 (null → 0)")
  private Long viewCount;
}
