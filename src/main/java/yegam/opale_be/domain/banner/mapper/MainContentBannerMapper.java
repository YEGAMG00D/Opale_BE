package yegam.opale_be.domain.banner.mapper;

import org.springframework.stereotype.Component;
import yegam.opale_be.domain.banner.dto.request.admin.AdminMainContentBannerRequestDto;
import yegam.opale_be.domain.banner.dto.response.MainContentBannerResponseDto;
import yegam.opale_be.domain.banner.dto.response.admin.AdminMainContentBannerResponseDto;
import yegam.opale_be.domain.banner.entity.MainContentBanner;
import yegam.opale_be.domain.culture.performance.entity.Performance;

@Component
public class MainContentBannerMapper {

  /** ✅ 관리자 요청 DTO → Entity */
  public MainContentBanner toEntity(
      AdminMainContentBannerRequestDto dto,
      Performance performance,
      String imageUrl
  ) {
    return MainContentBanner.builder()
        .title(dto.getTitle())
        .content(dto.getContent())
        .imageUrl(imageUrl)
        .linkUrl(dto.getLinkUrl())
        .performance(performance)
        .displayOrder(dto.getDisplayOrder())
        .isActive(dto.getIsActive())
        .build();
  }

  public AdminMainContentBannerResponseDto toAdminDto(MainContentBanner banner) {
    return AdminMainContentBannerResponseDto.builder()
        .contentBannerId(banner.getContentBannerId())
        .title(banner.getTitle())
        .content(banner.getContent())
        .imageUrl(banner.getImageUrl())
        .linkUrl(banner.getLinkUrl())
        .performanceId(
            banner.getPerformance() != null
                ? banner.getPerformance().getPerformanceId()
                : null
        )
        .displayOrder(banner.getDisplayOrder())
        .isActive(banner.getIsActive())
        .build();
  }

  public MainContentBannerResponseDto toMainDto(MainContentBanner banner) {
    return MainContentBannerResponseDto.builder()
        .contentBannerId(banner.getContentBannerId())
        .title(banner.getTitle())
        .content(banner.getContent())
        .imageUrl(banner.getImageUrl())
        .linkUrl(banner.getLinkUrl())
        .performanceId(
            banner.getPerformance() != null
                ? banner.getPerformance().getPerformanceId()
                : null
        )
        .build();
  }
}
