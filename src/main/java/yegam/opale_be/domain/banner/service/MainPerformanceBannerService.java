package yegam.opale_be.domain.banner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.banner.dto.request.admin.AdminMainPerformanceBannerRequestDto;
import yegam.opale_be.domain.banner.dto.response.MainPerformanceBannerResponseDto;
import yegam.opale_be.domain.banner.dto.response.admin.AdminMainPerformanceBannerResponseDto;
import yegam.opale_be.domain.banner.entity.MainPerformanceBanner;
import yegam.opale_be.domain.banner.exception.BannerErrorCode;
import yegam.opale_be.domain.banner.mapper.MainPerformanceBannerMapper;
import yegam.opale_be.domain.banner.repository.MainPerformanceBannerRepository;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.culture.performance.repository.PerformanceRepository;
import yegam.opale_be.global.exception.CustomException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainPerformanceBannerService {

  private final MainPerformanceBannerRepository mainPerformanceBannerRepository;
  private final PerformanceRepository performanceRepository;
  private final MainPerformanceBannerMapper mainPerformanceBannerMapper;

  /** ✅ 메인 공연 배너 등록 */
  @Transactional
  public AdminMainPerformanceBannerResponseDto create(AdminMainPerformanceBannerRequestDto dto) {

    Performance performance = performanceRepository.findById(dto.getPerformanceId())
        .orElseThrow(() -> new CustomException(BannerErrorCode.BANNER_DATA_ERROR));

    MainPerformanceBanner banner =
        mainPerformanceBannerMapper.toEntity(dto, performance);

    MainPerformanceBanner saved =
        mainPerformanceBannerRepository.save(banner);

    return mainPerformanceBannerMapper.toAdminDto(saved);
  }

  /** ✅ 메인 공연 배너 수정 */
  @Transactional
  public AdminMainPerformanceBannerResponseDto update(
      Long bannerId,
      AdminMainPerformanceBannerRequestDto dto
  ) {

    MainPerformanceBanner banner =
        mainPerformanceBannerRepository.findById(bannerId)
            .orElseThrow(() -> new CustomException(BannerErrorCode.BANNER_NOT_FOUND));

    Performance performance =
        performanceRepository.findById(dto.getPerformanceId())
            .orElseThrow(() -> new CustomException(BannerErrorCode.BANNER_DATA_ERROR));

    banner.setPerformance(performance);
    banner.setDisplayOrder(dto.getDisplayOrder());
    banner.setIsActive(dto.getIsActive());

    return mainPerformanceBannerMapper.toAdminDto(banner);
  }

  /** ✅ 메인 공연 배너 삭제 */
  @Transactional
  public void delete(Long bannerId) {

    if (!mainPerformanceBannerRepository.existsById(bannerId)) {
      throw new CustomException(BannerErrorCode.BANNER_NOT_FOUND);
    }

    mainPerformanceBannerRepository.deleteById(bannerId);
  }

  /** ✅ 관리자용 전체 공연 배너 조회 */
  public List<AdminMainPerformanceBannerResponseDto> getAll() {
    return mainPerformanceBannerRepository.findAll()
        .stream()
        .map(mainPerformanceBannerMapper::toAdminDto)
        .collect(Collectors.toList());
  }

  /** ✅ 메인 페이지용 활성 공연 배너 조회 */
  public List<MainPerformanceBannerResponseDto> getActiveBanners() {
    return mainPerformanceBannerRepository
        .findByIsActiveTrueOrderByDisplayOrderAsc()
        .stream()
        .map(mainPerformanceBannerMapper::toMainDto)
        .collect(Collectors.toList());
  }
}
