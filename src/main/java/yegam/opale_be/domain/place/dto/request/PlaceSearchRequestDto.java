package yegam.opale_be.domain.place.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PlaceSearchRequest DTO", description = "공연장 목록 및 지도 기반 검색 요청 DTO")
public class PlaceSearchRequestDto {

  @Schema(description = "지역 필터", example = "서울특별시")
  private String area;

  @Schema(description = "검색어 (공연장명 등)", example = "세종문화회관")
  private String keyword;

  @Schema(description = "정렬 방식 (이름순, 최신순 등)", example = "이름순")
  private String sortType;

  @Schema(description = "현재 위도", example = "37.5665")
  private BigDecimal latitude;

  @Schema(description = "현재 경도", example = "126.9780")
  private BigDecimal longitude;

  @Schema(description = "검색 반경 (m 단위)", example = "500")
  private Integer radius;

  @Schema(description = "페이지 번호 (1부터 시작)", example = "1")
  private Integer page;

  @Schema(description = "페이지당 항목 수", example = "20")
  private Integer size;
}
