package yegam.opale_be.domain.place.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PlaceNearbyRequest DTO", description = "좌표 기반 근처 공연장 조회 요청 DTO (지도 검색용)")
public class PlaceNearbyRequestDto {

  @Schema(description = "현재 위도", example = "37.58217", required = true)
  private BigDecimal latitude;

  @Schema(description = "현재 경도", example = "126.9993234", required = true)
  private BigDecimal longitude;

  @Schema(description = "검색 반경 (m 단위, 기본 3000)", example = "500")
  private Integer radius;

  @Schema(description = "정렬 방식 ('거리순' 또는 '이름순')", example = "거리순")
  private String sortType;

  @Schema(description = "페이지 번호 (1부터 시작)", example = "1")
  private Integer page;

  @Schema(description = "페이지당 항목 수", example = "20")
  private Integer size;
}
