package yegam.opale_be.domain.banner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import yegam.opale_be.domain.banner.dto.request.admin.AdminBannerRequestDto;
import yegam.opale_be.domain.banner.dto.response.MainBannerResponseDto;
import yegam.opale_be.domain.banner.dto.response.admin.AdminBannerResponseDto;
import yegam.opale_be.domain.banner.entity.MainBanner;
import yegam.opale_be.domain.banner.exception.BannerErrorCode;
import yegam.opale_be.domain.banner.mapper.BannerMapper;
import yegam.opale_be.domain.banner.repository.BannerRepository;
import yegam.opale_be.global.exception.CustomException;
import yegam.opale_be.global.storage.FileStorageService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BannerService {

  private final BannerRepository bannerRepository;
  private final BannerMapper bannerMapper;
  private final FileStorageService fileStorageService;

  /** ✅ 배너 등록 */
  @Transactional
  public AdminBannerResponseDto createBanner(AdminBannerRequestDto dto, MultipartFile file) {

    String imageUrl = fileStorageService.saveFileAndReturnUrl(file, "banners");

    MainBanner banner = bannerMapper.toEntity(dto, imageUrl);
    MainBanner saved = bannerRepository.save(banner);

    return bannerMapper.toAdminDto(saved);
  }

  /** ✅ 배너 수정 */
  @Transactional
  public AdminBannerResponseDto updateBanner(
      Long bannerId,
      AdminBannerRequestDto dto,
      MultipartFile file
  ) {

    MainBanner banner = bannerRepository.findById(bannerId)
        .orElseThrow(() -> new CustomException(BannerErrorCode.BANNER_NOT_FOUND));

    // ✅ 이미지 교체
    if (file != null && !file.isEmpty()) {
      String imageUrl = fileStorageService.saveFileAndReturnUrl(file, "banners");
      banner.setImageUrl(imageUrl);
    }

    // ✅ 텍스트 필드들 (엔티티 필드명 기준)
    banner.setPerformanceId(dto.getPerformanceId());
    banner.setTitleText(dto.getTitleText());
    banner.setSubtitleText(dto.getSubtitleText());
    banner.setDescriptionText(dto.getDescriptionText());
    banner.setDateText(dto.getDateText());
    banner.setPlaceText(dto.getPlaceText());
    banner.setDisplayOrder(dto.getDisplayOrder());
    banner.setIsActive(dto.getIsActive());

    return bannerMapper.toAdminDto(banner);
  }

  /** ✅ 배너 삭제 */
  @Transactional
  public void deleteBanner(Long bannerId) {

    if (!bannerRepository.existsById(bannerId)) {
      throw new CustomException(BannerErrorCode.BANNER_NOT_FOUND);
    }

    bannerRepository.deleteById(bannerId);
  }

  /** ✅ 관리자용 전체 배너 조회 */
  public List<AdminBannerResponseDto> getAllBanners() {
    return bannerRepository.findAll()
        .stream()
        .map(bannerMapper::toAdminDto)
        .collect(Collectors.toList());
  }

  /** ✅ 메인 페이지용 활성 배너 조회 */
  public List<MainBannerResponseDto> getMainBanners() {
    return bannerRepository.findByIsActiveTrueOrderByDisplayOrderAsc()
        .stream()
        .map(bannerMapper::toMainDto)
        .collect(Collectors.toList());
  }
}
