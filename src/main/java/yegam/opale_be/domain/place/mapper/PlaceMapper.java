package yegam.opale_be.domain.place.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.place.dto.response.detail.*;
import yegam.opale_be.domain.place.dto.response.list.*;
import yegam.opale_be.domain.place.entity.Place;
import yegam.opale_be.domain.place.entity.PlaceStage;
import yegam.opale_be.global.common.BasePlaceListResponseDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PlaceMapper {

  /** ✅ 페이지 변환 */
  public PlaceListResponseDto toPagedPlaceListDto(Page<Place> placePage) {
    List<PlaceSummaryResponseDto> list = placePage.getContent().stream()
        .map(this::toPlaceSummaryDto)
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

  /** ✅ 전체 리스트 변환 */
  public PlaceListResponseDto toPlaceListDto(List<Place> places) {
    List<PlaceSummaryResponseDto> list = places.stream()
        .map(this::toPlaceSummaryDto)
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

  /** ✅ 공연장 요약 DTO */
  public PlaceSummaryResponseDto toPlaceSummaryDto(Place p) {
    return PlaceSummaryResponseDto.builder()
        .placeId(p.getPlaceId())
        .name(p.getName())
        .address(p.getAddress())
        .telno(p.getTelno())
        .stageCount(p.getStageCount())
        .latitude(p.getLa())
        .longitude(p.getLo())
        .build();
  }

  /** ✅ 공연장 기본 정보 DTO */
  public PlaceBasicResponseDto toPlaceBasicDto(Place p) {
    return PlaceBasicResponseDto.builder()
        .placeId(p.getPlaceId())
        .name(p.getName())
        .address(p.getAddress())
        .telno(p.getTelno())
        .fcltychartr(p.getFcltychartr())
        .opende(p.getOpende())
        .seatscale(p.getSeatscale())
        .relateurl(p.getRelateurl())
        .stageCount(p.getStageCount())
        .la(p.getLa())
        .lo(p.getLo())
        .build();
  }

  /** ✅ 공연장 편의시설 DTO */
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

  /** ✅ 공연관 DTO */
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

  /** ✅ 공연장별 공연 DTO */
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

  /** ✅ 공통 리스트 Response 변환 (공연관/공연 등) */
  public <T> BasePlaceListResponseDto<T> toBasePlaceListResponse(Place p, List<T> items) {
    return BasePlaceListResponseDto.<T>builder()
        .placeId(p.getPlaceId())
        .placeName(p.getName())
        .address(p.getAddress())
        .totalCount(items.size())
        .items(items)
        .build();
  }

  private List<String> splitKeywords(String keywords) {
    if (keywords == null || keywords.isBlank()) return List.of();
    return List.of(keywords.split(","));
  }

  /** ✅ 좌표 기반 공연장 목록 변환 */
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
            .latitude((BigDecimal) r[3])   // ✅ BigDecimal 그대로 사용
            .longitude((BigDecimal) r[4])  // ✅ BigDecimal 그대로 사용
            .distance(((Number) r[5]).doubleValue()) // distance는 double 그대로
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



}
