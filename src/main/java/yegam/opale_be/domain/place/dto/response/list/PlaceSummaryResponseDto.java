package yegam.opale_be.domain.place.dto.response.list;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PlaceSummaryResponse DTO", description = "공연장 요약 정보 응답 DTO")
public class PlaceSummaryResponseDto {

  @Schema(description = "공연장 ID", example = "PLC0001")
  private String placeId;

  @Schema(description = "공연장명", example = "세종문화회관")
  private String name;

  @Schema(description = "주소", example = "서울특별시 종로구 세종대로 175")
  private String address;

  @Schema(description = "대표 전화번호", example = "02-399-1114")
  private String telno;

  @Schema(description = "위도", example = "37.5721")
  private Double la;

  @Schema(description = "경도", example = "126.9769")
  private Double lo;

  @Schema(description = "공연관 개수", example = "3")
  private Integer stageCount;
}
