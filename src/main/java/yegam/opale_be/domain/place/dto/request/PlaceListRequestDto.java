package yegam.opale_be.domain.place.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PlaceListRequest DTO", description = "공연장 목록 조회 요청 DTO (지역, 검색어, 정렬 기반)")
public class PlaceListRequestDto {

  @Schema(description = "지역 필터", example = "서울특별시")
  private String area;

  @Schema(description = "검색어 (공연장명 등)", example = "세종문화회관")
  private String keyword;

  @Schema(description = "정렬 방식 ('이름순')", example = "이름순")
  private String sortType; // 현재는 이름순만 의미 있음

  @Schema(description = "페이지 번호 (1부터 시작)", example = "1")
  private Integer page;

  @Schema(description = "페이지당 항목 수", example = "20")
  private Integer size;
}
