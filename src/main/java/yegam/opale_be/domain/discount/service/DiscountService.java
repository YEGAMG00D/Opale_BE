package yegam.opale_be.domain.discount.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.discount.dto.response.DiscountItemResponseDto;
import yegam.opale_be.domain.discount.dto.response.DiscountListResponseDto;
import yegam.opale_be.domain.discount.entity.Discount;
import yegam.opale_be.domain.discount.entity.DiscountSiteType;
import yegam.opale_be.domain.discount.exception.DiscountErrorCode;
import yegam.opale_be.domain.discount.mapper.DiscountMapper;
import yegam.opale_be.domain.discount.repository.DiscountRepository;
import yegam.opale_be.global.exception.CustomException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscountService {

  private final DiscountRepository discountRepository;
  private final DiscountMapper discountMapper;

  /**
   * 사이트별 최신 할인 데이터 조회
   */
  public DiscountListResponseDto getDiscountsBySite(DiscountSiteType siteType) {

    // 🔹 여기! name() 쓰지 말고 enum 그대로 전달
    String latestBatch = discountRepository.findLatestBatchIdBySite(siteType);
    if (latestBatch == null) {
      throw new CustomException(DiscountErrorCode.BATCH_NOT_FOUND);
    }

    List<Discount> discounts =
        discountRepository.findBySiteAndBatchIdOrderByCreatedAtDesc(siteType, latestBatch);

    if (discounts.isEmpty()) {
      throw new CustomException(DiscountErrorCode.DISCOUNT_NOT_FOUND);
    }

    List<DiscountItemResponseDto> dtoList =
        discounts.stream()
            .map(discountMapper::toDiscountItemDto)
            .toList();

    return DiscountListResponseDto.builder()
        .totalCount(dtoList.size())
        .items(dtoList)
        .build();
  }
}
