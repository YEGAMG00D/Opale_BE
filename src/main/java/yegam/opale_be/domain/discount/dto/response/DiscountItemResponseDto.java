package yegam.opale_be.domain.discount.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "할인 공연 단일 데이터 DTO")
public class DiscountItemResponseDto {

  @Schema(description = "예매처 (INTERPARK / TIMETICKET)")
  private String site;

  @Schema(description = "공연명")
  private String title;

  @Schema(description = "공연장")
  private String venue;

  @Schema(description = "이미지 URL")
  private String imageUrl;

  @Schema(description = "할인 타입 (타임딜, 조기예매 등)")
  private String saleType;

  @Schema(description = "할인율 (%)")
  private String discountPercent;

  @Schema(description = "할인가격")
  private String discountPrice;


  @Schema(description = "시작일")
  private LocalDate startDate;

  @Schema(description = "종료일")
  private LocalDate endDate;

  @Schema(description = "상세 페이지 링크")
  private String link;

  @Schema(description = "할인 종료 시각 (타이머 계산용)")
  private LocalDateTime discountEndDatetime;
}
