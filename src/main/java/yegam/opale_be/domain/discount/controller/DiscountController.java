package yegam.opale_be.domain.discount.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yegam.opale_be.domain.discount.dto.response.DiscountListResponseDto;
import yegam.opale_be.domain.discount.entity.DiscountSiteType;
import yegam.opale_be.domain.discount.service.DiscountService;
import yegam.opale_be.global.response.BaseResponse;

@RestController
@RequestMapping("/api/discounts")
@Tag(name = "Discount", description = "할인 공연 API")
@RequiredArgsConstructor
public class DiscountController {

  private final DiscountService discountService;

  @Operation(summary = "인터파크 할인 조회", description = "인터파크 최신 할인 공연 목록을 조회합니다.")
  @GetMapping("/interpark")
  public ResponseEntity<BaseResponse<DiscountListResponseDto>> getInterparkDiscounts() {

    DiscountListResponseDto response =
        discountService.getDiscountsBySite(DiscountSiteType.INTERPARK);

    return ResponseEntity.ok(
        BaseResponse.success("인터파크 할인 조회 성공", response)
    );
  }

  @Operation(summary = "타임티켓 할인 조회", description = "타임티켓 최신 할인 공연 목록을 조회합니다.")
  @GetMapping("/timeticket")
  public ResponseEntity<BaseResponse<DiscountListResponseDto>> getTimeTicketDiscounts() {

    DiscountListResponseDto response =
        discountService.getDiscountsBySite(DiscountSiteType.TIMETICKET);

    return ResponseEntity.ok(
        BaseResponse.success("타임티켓 할인 조회 성공", response)
    );
  }
}
