package yegam.opale_be.domain.place.dto.response.detail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PlacePerformanceResponse DTO", description = "공연장에 속한 공연 목록 항목 DTO")
public class PlacePerformanceResponseDto {

  @Schema(description = "공연 ID", example = "PF12345")
  private String performanceId;

  @Schema(description = "공연명", example = "레미제라블")
  private String title;

  @Schema(description = "장르", example = "뮤지컬")
  private String genrenm;

  @Schema(description = "공연 포스터", example = "http://www.kopis.or.kr/upload/pfmPoster/abc.jpg")
  private String poster;

  @Schema(description = "공연 시작일", example = "2025-12-01")
  private LocalDate startDate;

  @Schema(description = "공연 종료일", example = "2025-12-31")
  private LocalDate endDate;

  @Schema(description = "공연 상태", example = "공연중 / 공연예정 / 공연종료")
  private String prfstate;

  @Schema(description = "AI 요약", example = "혁명과 사랑을 그린 대서사극.")
  private String aiSummary;

  @Schema(description = "키워드 목록", example = "[\"혁명\", \"사랑\", \"뮤지컬\"]")
  private List<String> keywords;
}
