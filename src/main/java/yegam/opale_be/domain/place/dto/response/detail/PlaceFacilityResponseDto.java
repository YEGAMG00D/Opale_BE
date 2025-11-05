package yegam.opale_be.domain.place.dto.response.detail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PlaceFacilityResponse DTO", description = "공연장 내 편의시설 응답 DTO")
public class PlaceFacilityResponseDto {

  @Schema(description = "레스토랑 여부", example = "true")
  private Boolean restaurant;

  @Schema(description = "카페 여부", example = "true")
  private Boolean cafe;

  @Schema(description = "편의점 여부", example = "true")
  private Boolean store;

  @Schema(description = "놀이방 여부", example = "false")
  private Boolean nolibang;

  @Schema(description = "수유실 여부", example = "false")
  private Boolean suyu;

  @Schema(description = "장애시설-주차장", example = "true")
  private Boolean parkbarrier;

  @Schema(description = "장애시설-화장실", example = "true")
  private Boolean restbarrier;

  @Schema(description = "장애시설-경사로", example = "true")
  private Boolean runwbarrier;

  @Schema(description = "장애시설-엘리베이터", example = "true")
  private Boolean elevbarrier;

  @Schema(description = "주차시설", example = "true")
  private Boolean parkinglot;
}
