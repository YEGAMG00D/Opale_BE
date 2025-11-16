package yegam.opale_be.domain.recommendation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "RecommendedPerformance DTO", description = "추천된 공연 데이터 DTO")
public class RecommendedPerformanceDto {

  @Schema(description = "공연 ID", example = "PF273937")
  private String performanceId;

  @Schema(description = "공연명")
  private String title;

  @Schema(description = "장르명")
  private String genre;

  @Schema(description = "포스터 이미지 URL")
  private String poster;

  @Schema(description = "시작일")
  private LocalDate startDate;

  @Schema(description = "종료일")
  private LocalDate endDate;

  @Schema(description = "평점")
  private Double rating;

  @Schema(description = "Pinecone 유사도 점수")
  private Double score;

  @Schema(description = "조회수", example = "137")
  private Long viewCount;
}
