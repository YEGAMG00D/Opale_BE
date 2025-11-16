package yegam.opale_be.domain.recommendation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "Place Recommendation List DTO",
    description = "추천된 공연장 목록 응답 DTO")
public class RecommendationPlaceListResponseDto {

  @Schema(description = "총 개수", example = "10")
  private int totalCount;

  @Schema(description = "요청한 사이즈", example = "10")
  private int requestedSize;

  @Schema(description = "정렬 기준", example = "popularity")
  private String sort;

  @Schema(description = "추천 공연장 목록")
  private List<RecommendedPlaceDto> recommendations;
}
