package yegam.opale_be.domain.culture.performance.mapper;

import org.springframework.stereotype.Component;
import yegam.opale_be.domain.culture.performance.dto.request.admin.AdminPerformanceImageRequestDto;
import yegam.opale_be.domain.culture.performance.dto.response.admin.AdminPerformanceImageResponseDto;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.culture.performance.entity.PerformanceImage;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdminPerformanceMapper {

  /** DTO → Entity 변환 */
  public PerformanceImage toPerformanceImage(AdminPerformanceImageRequestDto dto, Performance performance) {
    return PerformanceImage.builder()
        .performance(performance)
        .imageUrl(dto.getImageUrl())
        .imageType(dto.getImageType())
        .sourceUrl(dto.getSourceUrl())
        .build();
  }

  /** Entity → DTO 변환 */
  public AdminPerformanceImageResponseDto toImageResponse(PerformanceImage img) {
    return AdminPerformanceImageResponseDto.builder()
        .performanceImageId(img.getPerformanceImageId())
        .imageUrl(img.getImageUrl())
        .imageType(img.getImageType().name())
        .sourceUrl(img.getSourceUrl())
        .build();
  }

  /** 리스트 변환 */
  public List<AdminPerformanceImageResponseDto> toImageResponseList(List<PerformanceImage> images) {
    return images.stream()
        .map(this::toImageResponse)
        .collect(Collectors.toList());
  }
}
