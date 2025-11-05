package yegam.opale_be.domain.place.dto.response.detail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PlaceStageResponse DTO", description = "공연장 내 공연관(무대) 정보 응답 DTO")
public class PlaceStageResponseDto {

  @Schema(description = "공연관 ID", example = "STG001")
  private String stageId;

  @Schema(description = "공연관명", example = "대극장")
  private String name;

  @Schema(description = "좌석 수", example = "1000")
  private Integer seatscale;

  @Schema(description = "무대 면적", example = "15.8X13.8X8.7")
  private String stagearea;

  @Schema(description = "장애인석 수", example = "20")
  private Integer disabledseatscale;

  @Schema(description = "오케스트라 피트 여부", example = "true")
  private Boolean stageorchat;

  @Schema(description = "연습실 여부", example = "true")
  private Boolean stagepracat;

  @Schema(description = "분장실 여부", example = "true")
  private Boolean stagedresat;

  @Schema(description = "야외무대 여부", example = "false")
  private Boolean stageoutdrat;
}
