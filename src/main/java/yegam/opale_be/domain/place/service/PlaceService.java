package yegam.opale_be.domain.place.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.place.dto.request.*;
import yegam.opale_be.domain.place.dto.response.detail.*;
import yegam.opale_be.domain.place.dto.response.list.*;
import yegam.opale_be.domain.place.entity.Place;
import yegam.opale_be.domain.place.entity.PlaceStage;
import yegam.opale_be.domain.place.exception.PlaceErrorCode;
import yegam.opale_be.domain.place.mapper.PlaceMapper;
import yegam.opale_be.domain.place.repository.PlaceRepository;
import yegam.opale_be.global.common.BasePlaceListResponseDto;
import yegam.opale_be.global.exception.CustomException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

  private final PlaceRepository placeRepository;
  private final PlaceMapper placeMapper;

  // ---------------------------------------------------------------------
  // 공연장 목록 용
  // ---------------------------------------------------------------------

  /** 공연장 목록 조회 */
  public PlaceListResponseDto getPlaceList(PlaceListRequestDto dto) {
    String keyword = emptyToNull(dto.getKeyword());
    String area = emptyToNull(dto.getArea());

    int page = (dto.getPage() != null && dto.getPage() > 0) ? dto.getPage() - 1 : 0;
    int size = (dto.getSize() != null && dto.getSize() > 0) ? dto.getSize() : 20;

    PageRequest pageable = PageRequest.of(page, size);
    Page<Place> pageResult = placeRepository.search(keyword, area, pageable);

    // 위도, 경도 포함된 리스트로 변환
    return placeMapper.toPagedPlaceListDto(pageResult);
  }


  /** 좌표 기반 근처 공연장 목록 조회 */
  public PlaceNearbyListResponseDto getNearbyPlaces(PlaceNearbyRequestDto dto) {
    if (dto.getLatitude() == null || dto.getLongitude() == null) {
      throw new CustomException(PlaceErrorCode.INVALID_COORDINATE);
    }

    double lat = dto.getLatitude().doubleValue();   // 위도
    double lon = dto.getLongitude().doubleValue();  // 경도
    int radius = dto.getRadius() != null ? dto.getRadius() : 3000; // 기본 반경 3km
    String sortType = (dto.getSortType() != null && !dto.getSortType().isBlank())
        ? dto.getSortType()
        : "거리순";

    List<Object[]> result = placeRepository.findNearbyPlacesWithDistance(lat, lon, radius);

    PlaceNearbyListResponseDto response =
        placeMapper.toNearbyListDto(result, dto.getLatitude(), dto.getLongitude(), radius, sortType);

    // 정렬 처리 (이름순 / 거리순)
    if ("이름순".equals(sortType)) {
      response.getPlaces().sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
    } else {
      response.getPlaces().sort((a, b) -> Double.compare(a.getDistance(), b.getDistance()));
    }

    return response;
  }


  // ---------------------------------------------------------------------
  // 공연장 상세 페이지 용
  // ---------------------------------------------------------------------

  /** 공연장 기본 정보 조회 */
  public PlaceBasicResponseDto getPlaceBasic(String placeId) {
    Place place = findPlace(placeId);
    return placeMapper.toPlaceBasicDto(place);
  }

  /** 공연장 내 공연관 목록 조회 */
  public BasePlaceListResponseDto<PlaceStageResponseDto> getPlaceStages(String placeId) {
    Place place = findPlace(placeId);
    List<PlaceStageResponseDto> stages = place.getPlaceStages().stream()
        .map(placeMapper::toPlaceStageDto)
        .collect(Collectors.toList());
    return placeMapper.toBasePlaceListResponse(place, stages);
  }

  /** 공연장 편의시설 목록 조회 */
  /**
   *
   * @param placeId
   * @return {restaurant, cafe, store, nolibang, suyu, parkbarrier, restbarrier, runwbarrier, elevbarrier, parkinglot}
   */
  public PlaceFacilityResponseDto getPlaceFacilities(String placeId) {
    Place place = findPlace(placeId);
    return placeMapper.toPlaceFacilityDto(place);
  }

  /** 공연장별 공연 목록 조회 */
  public BasePlaceListResponseDto<PlacePerformanceResponseDto> getPlacePerformances(String placeId) {
    Place place = findPlace(placeId);
    List<PlacePerformanceResponseDto> performances = place.getPerformances().stream()
        .map(placeMapper::toPlacePerformanceDto)
        .collect(Collectors.toList());
    return placeMapper.toBasePlaceListResponse(place, performances);
  }

  /** place_id로 공연장 객체 찾아서 리턴 */
  private Place findPlace(String id) {
    return placeRepository.findById(id)
        .orElseThrow(() -> new CustomException(PlaceErrorCode.PLACE_NOT_FOUND));
  }

  /** 내용이 null이거나 블랭크라면 null로 리턴, 아니면 내용 리턴 */
  private String emptyToNull(String s) {
    return (s == null || s.isBlank()) ? null : s;
  }


}
