package yegam.opale_be.domain.place.dto.response.list;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PlaceSummaryResponse DTO", description = "공연장 목록 응답 요약 DTO")
public class PlaceSummaryResponseDto {

  @Schema(description = "공연장 ID", example = "FC000020")
  private String placeId;

  @Schema(description = "공연장명", example = "세종문화회관")
  private String name;

  @Schema(description = "주소", example = "서울특별시 종로구 세종대로 175")
  private String address;

  @Schema(description = "전화번호", example = "02-399-1000")
  private String telno;

  @Schema(description = "위도", example = "37.5725254")
  private BigDecimal latitude;

  @Schema(description = "경도", example = "126.9756429")
  private BigDecimal longitude;

  @Schema(description = "공연관 수", example = "8")
  private Integer stageCount;

  // ✅ 추가됨
  @Schema(description = "공연장 평균 평점", example = "4.7")
  private Double rating;
}
