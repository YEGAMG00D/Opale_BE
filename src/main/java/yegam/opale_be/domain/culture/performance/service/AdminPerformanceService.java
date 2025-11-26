package yegam.opale_be.domain.culture.performance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import yegam.opale_be.domain.culture.performance.dto.response.admin.*;
import yegam.opale_be.domain.culture.performance.entity.*;
import yegam.opale_be.domain.culture.performance.exception.PerformanceErrorCode;
import yegam.opale_be.domain.culture.performance.mapper.AdminPerformanceMapper;
import yegam.opale_be.domain.culture.performance.repository.PerformanceRepository;
import yegam.opale_be.domain.culture.performance.repository.PerformanceImageRepository;
import yegam.opale_be.global.exception.CustomException;
import yegam.opale_be.global.storage.FileStorageService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminPerformanceService {

  private final PerformanceRepository performanceRepository;
  private final PerformanceImageRepository performanceImageRepository;
  private final AdminPerformanceMapper mapper;
  private final FileStorageService fileStorageService;

  private Performance getPerformance(String performanceId) {
    return performanceRepository.findById(performanceId)
        .orElseThrow(() -> new CustomException(PerformanceErrorCode.PERFORMANCE_NOT_FOUND));
  }

  // ---------------- 1) 이미지 목록 조회 ----------------
  public AdminPerformanceImageListResponseDto getImages(String performanceId) {
    Performance performance = getPerformance(performanceId);
    List<PerformanceImage> images =
        performanceImageRepository.findByPerformance_PerformanceId(performanceId);

    List<PerformanceImage> discount = images.stream()
        .filter(i -> i.getImageType() == PerformanceImage.ImageType.DISCOUNT)
        .collect(Collectors.toList());

    List<PerformanceImage> casting = images.stream()
        .filter(i -> i.getImageType() == PerformanceImage.ImageType.CASTING)
        .collect(Collectors.toList());

    List<PerformanceImage> seat = images.stream()
        .filter(i -> i.getImageType() == PerformanceImage.ImageType.SEAT)
        .collect(Collectors.toList());

    List<PerformanceImage> notice = images.stream()
        .filter(i -> i.getImageType() == PerformanceImage.ImageType.NOTICE)
        .collect(Collectors.toList());

    List<PerformanceImage> other = images.stream()
        .filter(i -> i.getImageType() == PerformanceImage.ImageType.기타)
        .collect(Collectors.toList());

    return AdminPerformanceImageListResponseDto.builder()
        .performanceId(performance.getPerformanceId())
        .title(performance.getTitle())
        .placeName(performance.getPlaceName())
        .startDate(performance.getStartDate())
        .endDate(performance.getEndDate())

        .totalCount(images.size())
        .discountCount(discount.size())
        .castingCount(casting.size())
        .seatCount(seat.size())
        .noticeCount(notice.size())
        .otherCount(other.size())

        .discountImages(mapper.toImageResponseList(discount))
        .castingImages(mapper.toImageResponseList(casting))
        .seatImages(mapper.toImageResponseList(seat))
        .noticeImages(mapper.toImageResponseList(notice))
        .otherImages(mapper.toImageResponseList(other))
        .build();
  }

  // ---------------- 2) 이미지 파일 업로드 ----------------
  @Transactional
  public AdminPerformanceImageResponseDto uploadImageFile(
      String performanceId,
      MultipartFile file,
      PerformanceImage.ImageType imageType,
      String sourceUrl
  ) {
    Performance performance = getPerformance(performanceId);

    String uploadedUrl =
        fileStorageService.saveFileAndReturnUrl(file, "performance/images");

    PerformanceImage image = PerformanceImage.builder()
        .performance(performance)
        .imageUrl(uploadedUrl)
        .imageType(imageType)
        .sourceUrl(sourceUrl)
        .build();

    PerformanceImage saved = performanceImageRepository.save(image);
    return mapper.toImageResponse(saved);
  }

  // ---------------- 3) 이미지 삭제 ----------------
  @Transactional
  public void deleteImage(Long imageId) {
    if (!performanceImageRepository.existsById(imageId)) {
      throw new CustomException(PerformanceErrorCode.PERFORMANCE_IMAGE_NOT_FOUND);
    }
    performanceImageRepository.deleteById(imageId);
  }
}
