package yegam.opale_be.domain.culture.performance.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 공연 목록 및 검색 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PerformanceRequest DTO", description = "공연 목록 및 검색 조건 요청 DTO")
public class PerformanceSearchRequestDto {

  @Schema(description = "장르 (예: 뮤지컬, 연극, 콘서트)", example = "뮤지컬")
  private String genre;

  @Schema(description = "검색어 (공연명 또는 출연자명)", example = "레미제라블")
  private String keyword;

  @Schema(description = "정렬 방식 (인기순, 최신순)", example = "인기")
  private String sortType;

  @Schema(description = "지역 필터", example = "서울특별시")
  private String area;

  @Schema(description = "페이지 번호 (무한스크롤 또는 페이지네이션)", example = "0")
  private Integer page;

  @Schema(description = "페이지당 항목 수", example = "20")
  private Integer size;
}
