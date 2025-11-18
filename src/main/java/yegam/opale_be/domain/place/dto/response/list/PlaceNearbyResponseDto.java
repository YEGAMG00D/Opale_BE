package yegam.opale_be.domain.place.dto.response.list;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PlaceNearbyResponse DTO", description = "좌표 기반 공연장 항목 DTO")
public class PlaceNearbyResponseDto {

  @Schema(description = "공연장 ID", example = "PLC0001")
  private String placeId;

  @Schema(description = "공연장명", example = "세종문화회관")
  private String name;

  @Schema(description = "주소", example = "서울특별시 종로구 세종대로 175")
  private String address;

  @Schema(description = "공연장 위도", example = "37.5725254")
  private BigDecimal latitude;

  @Schema(description = "공연장 경도", example = "126.9756429")
  private BigDecimal longitude;

  @Schema(description = "거리 (m 단위)", example = "215.23")
  private double distance;

  // ⭐ 추가됨
  @Schema(description = "공연장 평균 평점", example = "4.7")
  private Double rating;

  @Schema(description = "리뷰 개수", example = "15")
  private Long reviewCount;
}
