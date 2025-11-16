package yegam.opale_be.domain.recommendation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "RecommendedPlace DTO", description = "추천된 공연장 데이터 DTO (기본 공연장 목록 DTO와 구조 통일)")
public class RecommendedPlaceDto {

  @Schema(description = "공연장 ID", example = "FC000020")
  private String placeId;

  @Schema(description = "공연장명", example = "세종문화회관")
  private String name;

  @Schema(description = "주소", example = "서울 종로구 세종대로 175")
  private String address;

  @Schema(description = "전화번호")
  private String telno;

  @Schema(description = "평점 (null → 0)")
  private Double rating;

  @Schema(description = "공연관 수")
  private Integer stageCount;

  @Schema(description = "조회수 (null → 0)")
  private Long viewCount;
}
