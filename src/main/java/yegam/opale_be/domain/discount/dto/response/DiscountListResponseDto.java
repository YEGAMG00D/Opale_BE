package yegam.opale_be.domain.discount.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Schema(description = "할인 공연 목록 응답 DTO")
public class DiscountListResponseDto {

  @Schema(description = "총 할인 공연 수")
  private long totalCount;

  @Schema(description = "할인 공연 리스트")
  private List<DiscountItemResponseDto> items;
}
