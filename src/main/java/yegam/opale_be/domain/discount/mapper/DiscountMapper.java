package yegam.opale_be.domain.discount.mapper;

import org.springframework.stereotype.Component;
import yegam.opale_be.domain.discount.dto.response.DiscountItemResponseDto;
import yegam.opale_be.domain.discount.entity.Discount;

@Component
public class DiscountMapper {

  public DiscountItemResponseDto toDiscountItemDto(Discount d) {
    if (d == null) return null;

    return DiscountItemResponseDto.builder()
        .site(d.getSite().name())
        .title(d.getTitle())
        .venue(d.getVenue())
        .imageUrl(d.getImageUrl())
        .saleType(d.getSaleType())
        .discountPercent(d.getDiscountPercent())
        .discountPrice(d.getDiscountPrice())
        .startDate(d.getStartDate())
        .endDate(d.getEndDate())
        .link(d.getLink())
        .discountEndDatetime(d.getDiscountEndDatetime())
        .build();
  }
}
