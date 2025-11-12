package yegam.opale_be.domain.culture.performance.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import yegam.opale_be.domain.culture.performance.dto.response.detail.*;
import yegam.opale_be.domain.culture.performance.dto.response.list.*;
import yegam.opale_be.domain.culture.performance.entity.*;
import yegam.opale_be.global.common.BasePerformanceListResponseDto;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class PerformanceMapper {

  /** 공연 목록 변환 */
  public PerformanceListResponseDto toPagedPerformanceListDto(Page<Performance> performancePage) {
    List<PerformanceResponseDto> dtoList = performancePage.getContent().stream()
        .map(this::toPerformanceResponseDto)
        .collect(Collectors.toList());

    return PerformanceListResponseDto.builder()
        .totalCount(performancePage.getTotalElements())
        .currentPage(performancePage.getNumber() + 1)
        .pageSize(performancePage.getSize())
        .totalPages(performancePage.getTotalPages())
        .hasNext(performancePage.hasNext())
        .hasPrev(performancePage.hasPrevious())
        .performances(dtoList)
        .build();
  }

  /** BaseListResponseDto 공통 변환 */
  public <T> BasePerformanceListResponseDto<T> toBaseListResponse(Performance performance, List<T> list) {
    return BasePerformanceListResponseDto.<T>builder()
        .performanceId(performance.getPerformanceId())
        .title(performance.getTitle())
        .totalCount(list.size())
        .items(list)
        .build();
  }

  /** 공연 단건 → 목록용 DTO */
  public PerformanceResponseDto toPerformanceResponseDto(Performance p) {
    if (p == null) return null;
    return PerformanceResponseDto.builder()
        .performanceId(p.getPerformanceId())
        .title(p.getTitle())
        .genrenm(p.getGenrenm())
        .poster(p.getPoster())
        .placeName(p.getPlaceName())
        .startDate(p.getStartDate() != null ? p.getStartDate().toLocalDate() : null)
        .endDate(p.getEndDate() != null ? p.getEndDate().toLocalDate() : null)
        .rating(0.0)
        .keywords(splitKeywords(p.getAiKeywords()))
        .aiSummary(p.getAiSummary())
        .build();
  }

  /** 리스트 전용 변환 */
  public PerformanceListResponseDto toPerformanceListDto(List<Performance> performances) {
    List<PerformanceResponseDto> dtoList = performances.stream()
        .map(this::toPerformanceResponseDto)
        .collect(Collectors.toList());
    return PerformanceListResponseDto.builder()
        .totalCount(dtoList.size())
        .performances(dtoList)
        .currentPage(1)
        .pageSize(dtoList.size())
        .totalPages(1)
        .hasNext(false)
        .hasPrev(false)
        .build();
  }

  /** 공연 기본 정보 변환 */
  public PerformanceBasicResponseDto toPerformanceBasicDto(Performance p) {
    return PerformanceBasicResponseDto.builder()
        .performanceId(p.getPerformanceId())
        .title(p.getTitle())
        .genrenm(p.getGenrenm())
        .poster(p.getPoster())
        .placeName(p.getPlaceName())
        .placeAddress(p.getPlace() != null ? p.getPlace().getAddress() : null)
        .startDate(p.getStartDate() != null ? p.getStartDate().toLocalDate() : null)
        .endDate(p.getEndDate() != null ? p.getEndDate().toLocalDate() : null)
        .rating(0.0)
        .keywords(splitKeywords(p.getAiKeywords()))
        .aiSummary(p.getAiSummary())
        .prfruntime(p.getPrfruntime())
        .prfage(p.getPrfage())
        .price(p.getPrice())
        .prfstate(p.getPrfstate())
        .build();
  }

  /** 공연 예매 정보 묶음 */
  public PerformanceDetailResponseDto toPerformanceDetailDto(
      Performance p,
      List<PerformanceImage> images,
      List<PerformanceRelation> relations,
      List<PerformanceVideo> videos
  ) {
    Map<PerformanceImage.ImageType, List<PerformanceImageResponseDto>> imageMap =
        images.stream()
            .collect(Collectors.groupingBy(
                PerformanceImage::getImageType,
                Collectors.mapping(this::toPerformanceImageDto, Collectors.toList())
            ));

    return PerformanceDetailResponseDto.builder()
        .performanceId(p.getPerformanceId())
        .title(p.getTitle())
        .price(p.getPrice())
        .discountImages(imageMap.getOrDefault(PerformanceImage.ImageType.DISCOUNT, List.of()))
        .seatImages(imageMap.getOrDefault(PerformanceImage.ImageType.SEAT, List.of()))
        .castingImages(imageMap.getOrDefault(PerformanceImage.ImageType.CASTING, List.of()))
        .noticeImages(imageMap.getOrDefault(PerformanceImage.ImageType.NOTICE, List.of()))
        .otherImages(imageMap.getOrDefault(PerformanceImage.ImageType.기타, List.of()))
        .build();
  }

  public PerformanceRelationResponseDto toPerformanceRelationDto(PerformanceRelation r) {
    return PerformanceRelationResponseDto.builder()
        .relationId(r.getRelationId())
        .siteName(r.getSiteName())
        .siteUrl(r.getSiteUrl())
        .build();
  }

  public PerformanceVideoResponseDto toPerformanceVideoDto(PerformanceVideo v) {
    return PerformanceVideoResponseDto.builder()
        .performanceVideoId(v.getPerformanceVideoId())
        .youtubeVideoId(v.getYoutubeVideoId())
        .title(v.getTitle())
        .sourceUrl(v.getSourceUrl())
        .thumbnailUrl(v.getThumbnailUrl())
        .embedUrl(v.getEmbedUrl())
        .build();
  }

  public PerformanceImageResponseDto toPerformanceImageDto(PerformanceImage img) {
    return PerformanceImageResponseDto.builder()
        .performanceImageId(img.getPerformanceImageId())
        .imageUrl(img.getImageUrl())
        .imageType(img.getImageType() != null ? img.getImageType().name() : "기타")
        .build();
  }

  private List<String> splitKeywords(String keywords) {
    if (keywords == null || keywords.isBlank()) return List.of();
    return Arrays.stream(keywords.split(","))
        .map(String::trim)
        .filter(k -> !k.isEmpty())
        .collect(Collectors.toList());
  }
}
