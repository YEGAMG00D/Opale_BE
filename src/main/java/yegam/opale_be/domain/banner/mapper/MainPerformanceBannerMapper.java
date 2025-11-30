package yegam.opale_be.domain.banner.mapper;

import org.springframework.stereotype.Component;
import yegam.opale_be.domain.banner.dto.request.admin.AdminMainPerformanceBannerRequestDto;
import yegam.opale_be.domain.banner.dto.response.MainPerformanceBannerResponseDto;
import yegam.opale_be.domain.banner.dto.response.admin.AdminMainPerformanceBannerResponseDto;
import yegam.opale_be.domain.banner.entity.MainPerformanceBanner;
import yegam.opale_be.domain.culture.performance.entity.Performance;

@Component
public class MainPerformanceBannerMapper {

  /** ✅ 관리자 요청 DTO → Entity */
  public MainPerformanceBanner toEntity(
      AdminMainPerformanceBannerRequestDto dto,
      Performance performance
  ) {
    return MainPerformanceBanner.builder()
        .performance(performance)
        .displayOrder(dto.getDisplayOrder())
        .isActive(dto.getIsActive())
        .build();
  }

  /** ✅ Entity → 관리자 응답 DTO */
  public AdminMainPerformanceBannerResponseDto toAdminDto(MainPerformanceBanner banner) {
    return AdminMainPerformanceBannerResponseDto.builder()
        .bannerId(banner.getBannerId())
        .performanceId(banner.getPerformance().getPerformanceId())
        .performanceTitle(banner.getPerformance().getTitle())
        .displayOrder(banner.getDisplayOrder())
        .isActive(banner.getIsActive())
        .build();
  }

  /** ✅ Entity → 메인 페이지 응답 DTO */
  public MainPerformanceBannerResponseDto toMainDto(MainPerformanceBanner banner) {
    Performance p = banner.getPerformance();

    return MainPerformanceBannerResponseDto.builder()
        .bannerId(banner.getBannerId())
        .performanceId(p.getPerformanceId())
        .title(p.getTitle())
        .startDate(p.getStartDate().toString())
        .endDate(p.getEndDate().toString())
        .placeName(p.getPlaceName())
        .posterUrl(p.getPoster())
        .genrenm(p.getGenrenm())
        .rating(p.getRating() != null ? p.getRating() : 0.0)
        .build();
  }
}
