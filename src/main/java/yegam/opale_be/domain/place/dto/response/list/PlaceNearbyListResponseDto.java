package yegam.opale_be.domain.place.dto.response.list;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;
import yegam.opale_be.domain.place.dto.response.detail.PlaceNearbyResponseDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PlaceNearbyListResponse DTO", description = "좌표 기반 공연장 목록 조회 응답 DTO")
public class PlaceNearbyListResponseDto {

  @Schema(description = "총 공연장 개수", example = "15")
  private long totalCount;

  @Schema(description = "현재 페이지", example = "1")
  private int currentPage;

  @Schema(description = "페이지당 항목 수", example = "20")
  private int pageSize;

  @Schema(description = "총 페이지 수", example = "1")
  private int totalPages;

  @Schema(description = "정렬 방식 ('이름순', '거리순')", example = "이름순")
  private String sortType;



  @Schema(description = "기준 위도", example = "37.58217")
  private double searchLatitude;

  @Schema(description = "기준 경도", example = "126.9993234")
  private double searchLongitude;

  @Schema(description = "검색 반경", example = "500")
  private int searchRadius;




  @Schema(description = "공연장 목록 데이터 (거리 포함)")
  private List<PlaceNearbyResponseDto> places;
}
