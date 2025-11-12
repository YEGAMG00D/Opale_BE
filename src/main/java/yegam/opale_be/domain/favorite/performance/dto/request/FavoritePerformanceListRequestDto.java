package yegam.opale_be.domain.favorite.performance.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 공연 관심 목록 조회 요청 DTO
 * - 마이페이지 또는 페이지네이션 요청용
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "FavoritePerformanceListRequest DTO", description = "공연 관심 목록 조회 요청 DTO")
public class FavoritePerformanceListRequestDto {

  @Schema(description = "현재 페이지 (1부터 시작)", example = "1")
  private Integer page;

  @Schema(description = "페이지당 공연 개수", example = "20")
  private Integer size;
}
