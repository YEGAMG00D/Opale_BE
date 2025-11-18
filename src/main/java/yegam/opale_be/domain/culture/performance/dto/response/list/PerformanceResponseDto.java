package yegam.opale_be.domain.culture.performance.dto.response.list;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(title = "PerformanceResponse DTO", description = "공연 목록 한 건 응답 DTO (명세서 기준)")
public class PerformanceResponseDto {

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
  private Double rating; // 현재는 null 또는 0.0로 대응

  @Schema(description = "리뷰 개수", example = "24")
  private Long reviewCount;

  @Schema(description = "키워드 목록", example = "[\"휴먼드라마\",\"대서사\"]")
  private List<String> keywords;

  @Schema(description = "AI 시놉시스 요약")
  private String aiSummary;
}
