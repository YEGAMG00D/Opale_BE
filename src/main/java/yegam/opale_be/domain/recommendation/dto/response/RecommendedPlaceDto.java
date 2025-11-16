package yegam.opale_be.domain.recommendation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "RecommendedPlace DTO", description = "추천된 공연장 데이터 DTO")
public class RecommendedPlaceDto {

  @Schema(description = "공연장 ID", example = "PL12345")
  private String placeId;

  @Schema(description = "공연장명", example = "세종문화회관")
  private String name;

  @Schema(description = "주소", example = "서울 종로구 세종대로 175")
  private String address;

  @Schema(description = "평점", example = "4.6")
  private Double rating;

  @Schema(description = "조회수", example = "1219")
  private Long viewCount;
}
