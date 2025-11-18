package yegam.opale_be.domain.place.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.place.dto.response.detail.*;
import yegam.opale_be.domain.place.dto.response.list.*;
import yegam.opale_be.domain.place.entity.Place;
import yegam.opale_be.domain.place.entity.PlaceStage;
import yegam.opale_be.domain.review.common.ReviewType;
import yegam.opale_be.domain.review.place.repository.PlaceReviewRepository;
import yegam.opale_be.global.common.BasePlaceListResponseDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PlaceMapper {

  // -----------------------------------------------------------
  // 📌 페이지네이션 + 리뷰 통계 포함
  // -----------------------------------------------------------
  public PlaceListResponseDto toPagedPlaceListDtoWithStats(
      Page<Place> placePage,
      PlaceReviewRepository reviewRepo
  ) {
    List<PlaceSummaryResponseDto> list = placePage.getContent().stream()
        .map(place -> injectSummaryStats(place, reviewRepo))
        .collect(Collectors.toList());

    return PlaceListResponseDto.builder()
        .totalCount(placePage.getTotalElements())
        .currentPage(placePage.getNumber() + 1)
        .pageSize(placePage.getSize())
        .totalPages(placePage.getTotalPages())
        .hasNext(placePage.hasNext())
        .hasPrev(placePage.hasPrevious())
        .places(list)
        .build();
  }

  // -----------------------------------------------------------
  // 📌 일반 리스트 + 리뷰 통계 포함
  // -----------------------------------------------------------
  public PlaceListResponseDto toPlaceListDtoWithStats(
      List<Place> places,
      PlaceReviewRepository reviewRepo
  ) {
    List<PlaceSummaryResponseDto> list = places.stream()
        .map(place -> injectSummaryStats(place, reviewRepo))
        .collect(Collectors.toList());

    return PlaceListResponseDto.builder()
        .totalCount(list.size())
        .currentPage(1)
        .pageSize(list.size())
        .totalPages(1)
        .hasNext(false)
        .hasPrev(false)
        .places(list)
        .build();
  }

  // -----------------------------------------------------------
  // 📌 요약 DTO + 리뷰 통계 주입 함수 (중복 제거)
  // -----------------------------------------------------------
  private PlaceSummaryResponseDto injectSummaryStats(
      Place place,
      PlaceReviewRepository reviewRepo
  ) {
    Long reviewCount =
        reviewRepo.countByPlaceIdAndType(place.getPlaceId(), ReviewType.PLACE);

    Double rating =
        reviewRepo.calculateAverageRating(place.getPlaceId());

    return PlaceSummaryResponseDto.builder()
        .placeId(place.getPlaceId())
        .name(place.getName())
        .address(place.getAddress())
        .telno(place.getTelno())
        .stageCount(place.getStageCount())
        .latitude(place.getLa())
        .longitude(place.getLo())
        .rating(rating != null ? rating : 0.0)
        .reviewCount(reviewCount != null ? reviewCount : 0L)
        .build();
  }

  // -----------------------------------------------------------
  // 📌 공연장 기본 정보 DTO + 리뷰 통계 포함
  // -----------------------------------------------------------
  public PlaceBasicResponseDto toPlaceBasicDtoWithStats(
      Place place,
      PlaceReviewRepository reviewRepo
  ) {
    Long reviewCount =
        reviewRepo.countByPlaceIdAndType(place.getPlaceId(), ReviewType.PLACE);

    Double rating =
        reviewRepo.calculateAverageRating(place.getPlaceId());

    return PlaceBasicResponseDto.builder()
        .placeId(place.getPlaceId())
        .name(place.getName())
        .address(place.getAddress())
        .telno(place.getTelno())
        .fcltychartr(place.getFcltychartr())
        .opende(place.getOpende())
        .seatscale(place.getSeatscale())
        .relateurl(place.getRelateurl())
        .stageCount(place.getStageCount())
        .la(place.getLa())
        .lo(place.getLo())
        .rating(rating != null ? rating : 0.0)
        .reviewCount(reviewCount != null ? reviewCount : 0L)
        .build();
  }

  // -----------------------------------------------------------
  // 📌 공연장 편의시설 DTO — 수정하면 안되는 부분
  // -----------------------------------------------------------
  public PlaceFacilityResponseDto toPlaceFacilityDto(Place p) {
    return PlaceFacilityResponseDto.builder()
        .restaurant(p.getRestaurant())
        .cafe(p.getCafe())
        .store(p.getStore())
        .nolibang(p.getNolibang())
        .suyu(p.getSuyu())
        .parkbarrier(p.getParkbarrier())
        .restbarrier(p.getRestbarrier())
        .runwbarrier(p.getRunwbarrier())
        .elevbarrier(p.getElevbarrier())
        .parkinglot(p.getParkinglot())
        .build();
  }

  // -----------------------------------------------------------
  // 📌 공연관 DTO
  // -----------------------------------------------------------
  public PlaceStageResponseDto toPlaceStageDto(PlaceStage s) {
    return PlaceStageResponseDto.builder()
        .stageId(s.getStageId())
        .name(s.getName())
        .seatscale(s.getSeatscale())
        .stagearea(s.getStagearea())
        .disabledseatscale(s.getDisabledseatscale())
        .stageorchat(s.getStageorchat())
        .stagepracat(s.getStagepracat())
        .stagedresat(s.getStagedresat())
        .stageoutdrat(s.getStageoutdrat())
        .build();
  }

  // -----------------------------------------------------------
  // 📌 공연장별 공연 DTO
  // -----------------------------------------------------------
  public PlacePerformanceResponseDto toPlacePerformanceDto(Performance p) {
    return PlacePerformanceResponseDto.builder()
        .performanceId(p.getPerformanceId())
        .title(p.getTitle())
        .genrenm(p.getGenrenm())
        .poster(p.getPoster())
        .startDate(p.getStartDate() != null ? p.getStartDate().toLocalDate() : null)
        .endDate(p.getEndDate() != null ? p.getEndDate().toLocalDate() : null)
        .prfstate(p.getPrfstate())
        .aiSummary(p.getAiSummary())
        .keywords(splitKeywords(p.getAiKeywords()))
        .build();
  }

  // -----------------------------------------------------------
  // 📌 공통 리스트 Response 변환
  // -----------------------------------------------------------
  public <T> BasePlaceListResponseDto<T> toBasePlaceListResponse(Place p, List<T> items) {
    return BasePlaceListResponseDto.<T>builder()
        .placeId(p.getPlaceId())
        .placeName(p.getName())
        .address(p.getAddress())
        .totalCount(items.size())
        .items(items)
        .build();
  }

  // -----------------------------------------------------------
  // 📌 근처 공연장 목록
  // -----------------------------------------------------------
  public PlaceNearbyListResponseDto toNearbyListDto(
      List<Object[]> rows,
      BigDecimal latitude,
      BigDecimal longitude,
      int radius,
      String sortType
  ) {
    List<PlaceNearbyResponseDto> places = rows.stream()
        .map(r -> PlaceNearbyResponseDto.builder()
            .placeId((String) r[0])
            .name((String) r[1])
            .address((String) r[2])
            .latitude((BigDecimal) r[3])
            .longitude((BigDecimal) r[4])
            .distance(((Number) r[5]).doubleValue())
            .rating(0.0)       // 기본값 (필요하면 서비스에서 주입)
            .reviewCount(0L)   // 기본값
            .build())
        .collect(Collectors.toList());

    return PlaceNearbyListResponseDto.builder()
        .totalCount(places.size())
        .currentPage(1)
        .pageSize(places.size())
        .totalPages(1)
        .sortType(sortType)
        .searchLatitude(latitude)
        .searchLongitude(longitude)
        .searchRadius(radius)
        .places(places)
        .build();
  }

  private List<String> splitKeywords(String keywords) {
    if (keywords == null || keywords.isBlank()) return List.of();
    return List.of(keywords.split(","));
  }
}
