package yegam.opale_be.domain.banner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import yegam.opale_be.domain.banner.dto.request.admin.AdminMainContentBannerRequestDto;
import yegam.opale_be.domain.banner.dto.response.MainContentBannerResponseDto;
import yegam.opale_be.domain.banner.dto.response.admin.AdminMainContentBannerResponseDto;
import yegam.opale_be.domain.banner.entity.MainContentBanner;
import yegam.opale_be.domain.banner.exception.BannerErrorCode;
import yegam.opale_be.domain.banner.mapper.MainContentBannerMapper;
import yegam.opale_be.domain.banner.repository.MainContentBannerRepository;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.culture.performance.repository.PerformanceRepository;
import yegam.opale_be.global.exception.CustomException;
import yegam.opale_be.global.storage.FileStorageService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainContentBannerService {

  private final MainContentBannerRepository mainContentBannerRepository;
  private final PerformanceRepository performanceRepository;
  private final MainContentBannerMapper mainContentBannerMapper;
  private final FileStorageService fileStorageService;

  /** ✅ 메인 콘텐츠 배너 등록 */
  @Transactional
  public AdminMainContentBannerResponseDto create(
      AdminMainContentBannerRequestDto dto,
      MultipartFile file
  ) {

    Performance performance = null;
    String imageUrl = null;

    // ✅ 공연 연결
    if (dto.getPerformanceId() != null && !dto.getPerformanceId().isBlank()) {
      performance = performanceRepository.findById(dto.getPerformanceId())
          .orElseThrow(() -> new CustomException(BannerErrorCode.BANNER_DATA_ERROR));
    }

    // ✅ 이미지 결정 로직
    if (file != null && !file.isEmpty()) {
      imageUrl = fileStorageService.saveFileAndReturnUrl(file, "main-content-banners");
    } else if (performance != null) {
      imageUrl = performance.getPoster();
    }

    MainContentBanner banner =
        mainContentBannerMapper.toEntity(dto, performance, imageUrl);

    MainContentBanner saved =
        mainContentBannerRepository.save(banner);

    return mainContentBannerMapper.toAdminDto(saved);
  }

  /** ✅ 메인 콘텐츠 배너 수정 */
  @Transactional
  public AdminMainContentBannerResponseDto update(
      Long contentBannerId,
      AdminMainContentBannerRequestDto dto,
      MultipartFile file
  ) {

    MainContentBanner banner =
        mainContentBannerRepository.findById(contentBannerId)
            .orElseThrow(() -> new CustomException(BannerErrorCode.BANNER_NOT_FOUND));

    Performance performance = null;

    if (dto.getPerformanceId() != null && !dto.getPerformanceId().isBlank()) {
      performance = performanceRepository.findById(dto.getPerformanceId())
          .orElseThrow(() -> new CustomException(BannerErrorCode.BANNER_DATA_ERROR));
    }

    if (file != null && !file.isEmpty()) {
      String imageUrl = fileStorageService.saveFileAndReturnUrl(file, "main-content-banners");
      banner.setImageUrl(imageUrl);
    } else if (performance != null) {
      banner.setImageUrl(performance.getPoster());
    }

    banner.setTitle(dto.getTitle());
    banner.setContent(dto.getContent());
    banner.setLinkUrl(dto.getLinkUrl());
    banner.setPerformance(performance);
    banner.setDisplayOrder(dto.getDisplayOrder());
    banner.setIsActive(dto.getIsActive());

    return mainContentBannerMapper.toAdminDto(banner);
  }

  /** ✅ 메인 콘텐츠 배너 삭제 */
  @Transactional
  public void delete(Long contentBannerId) {

    if (!mainContentBannerRepository.existsById(contentBannerId)) {
      throw new CustomException(BannerErrorCode.BANNER_NOT_FOUND);
    }

    mainContentBannerRepository.deleteById(contentBannerId);
  }

  /** ✅ 관리자용 전체 조회 */
  public List<AdminMainContentBannerResponseDto> getAll() {
    return mainContentBannerRepository.findAll()
        .stream()
        .map(mainContentBannerMapper::toAdminDto)
        .collect(Collectors.toList());
  }

  /** ✅ 메인 페이지용 활성 조회 */
  public List<MainContentBannerResponseDto> getActiveBanners() {
    return mainContentBannerRepository
        .findByIsActiveTrueOrderByDisplayOrderAsc()
        .stream()
        .map(mainContentBannerMapper::toMainDto)
        .collect(Collectors.toList());
  }
}
