package yegam.opale_be.domain.banner.mapper;

import org.springframework.stereotype.Component;
import yegam.opale_be.domain.banner.dto.request.admin.AdminBannerRequestDto;
import yegam.opale_be.domain.banner.dto.response.MainBannerResponseDto;
import yegam.opale_be.domain.banner.dto.response.admin.AdminBannerResponseDto;
import yegam.opale_be.domain.banner.entity.MainBanner;

@Component
public class BannerMapper {

  /** ✅ 관리자 요청 DTO → Entity */
  public MainBanner toEntity(AdminBannerRequestDto dto, String imageUrl) {
    return MainBanner.builder()
        .imageUrl(imageUrl)
        .performanceId(dto.getPerformanceId())
        .titleText(dto.getTitleText())
        .subtitleText(dto.getSubtitleText())
        .descriptionText(dto.getDescriptionText())
        .dateText(dto.getDateText())
        .placeText(dto.getPlaceText())
        .displayOrder(dto.getDisplayOrder())
        .isActive(dto.getIsActive())
        .linkUrl(dto.getLinkUrl())   // ✅ 여기 정상
        .build();
  }

  /** ✅ Entity → 관리자 응답 DTO */
  public AdminBannerResponseDto toAdminDto(MainBanner banner) {
    return AdminBannerResponseDto.builder()
        .bannerId(banner.getBannerId())
        .imageUrl(banner.getImageUrl())
        .performanceId(banner.getPerformanceId())
        .titleText(banner.getTitleText())
        .subtitleText(banner.getSubtitleText())
        .descriptionText(banner.getDescriptionText())
        .dateText(banner.getDateText())
        .placeText(banner.getPlaceText())
        .displayOrder(banner.getDisplayOrder())
        .isActive(banner.getIsActive())
        .build();
  }

  /** ✅ Entity → 메인 페이지 응답 DTO */
  public MainBannerResponseDto toMainDto(MainBanner banner) {
    return MainBannerResponseDto.builder()
        .bannerId(banner.getBannerId())
        .imageUrl(banner.getImageUrl())
        .performanceId(banner.getPerformanceId())
        .titleText(banner.getTitleText())
        .subtitleText(banner.getSubtitleText())
        .descriptionText(banner.getDescriptionText())
        .dateText(banner.getDateText())
        .placeText(banner.getPlaceText())
        .build();
  }
}
