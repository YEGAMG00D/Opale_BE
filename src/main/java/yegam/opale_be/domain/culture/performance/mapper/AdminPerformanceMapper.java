package yegam.opale_be.domain.culture.performance.mapper;

import org.springframework.stereotype.Component;
import yegam.opale_be.domain.culture.performance.dto.request.admin.AdminPerformanceImageRequestDto;
import yegam.opale_be.domain.culture.performance.dto.request.admin.AdminPerformanceVideoRequestDto;
import yegam.opale_be.domain.culture.performance.dto.response.admin.AdminPerformanceImageResponseDto;
import yegam.opale_be.domain.culture.performance.dto.response.admin.AdminPerformanceVideoResponseDto;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.culture.performance.entity.PerformanceImage;
import yegam.opale_be.domain.culture.performance.entity.PerformanceVideo;

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

  /* ===========================================================
     ✅ 아래부터 영상(Video) 관련 로직 추가
  =========================================================== */

  /** Video DTO → Entity */
  public PerformanceVideo toPerformanceVideo(
      AdminPerformanceVideoRequestDto dto,
      Performance performance
  ) {
    return PerformanceVideo.builder()
        .performance(performance)
        .youtubeVideoId(dto.getYoutubeVideoId())
        .title(dto.getTitle())
        .thumbnailUrl(dto.getThumbnailUrl())
        .sourceUrl(dto.getSourceUrl())
        .embedUrl(dto.getEmbedUrl())
        .build();
  }

  /** Video Entity → DTO */
  public AdminPerformanceVideoResponseDto toVideoResponse(PerformanceVideo video) {
    return AdminPerformanceVideoResponseDto.builder()
        .performanceVideoId(video.getPerformanceVideoId())
        .youtubeVideoId(video.getYoutubeVideoId())
        .title(video.getTitle())
        .thumbnailUrl(video.getThumbnailUrl())
        .sourceUrl(video.getSourceUrl())
        .embedUrl(video.getEmbedUrl())
        .build();
  }

  /** Video 리스트 변환 */
  public List<AdminPerformanceVideoResponseDto> toVideoResponseList(List<PerformanceVideo> videos) {
    return videos.stream()
        .map(this::toVideoResponse)
        .collect(Collectors.toList());
  }
}
