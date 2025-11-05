package yegam.opale_be.domain.place.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.place.dto.request.PlaceSearchRequestDto;
import yegam.opale_be.domain.place.dto.response.detail.*;
import yegam.opale_be.domain.place.dto.response.list.PlaceListResponseDto;
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

  /** ✅ 공연장 목록 조회 */
  public PlaceListResponseDto getPlaceList(PlaceSearchRequestDto dto) {
    String keyword = emptyToNull(dto.getKeyword());
    String area = emptyToNull(dto.getArea());

    int page = (dto.getPage() != null && dto.getPage() > 0) ? dto.getPage() - 1 : 0;
    int size = (dto.getSize() != null && dto.getSize() > 0) ? dto.getSize() : 20;

    PageRequest pageable = PageRequest.of(page, size);
    Page<Place> pageResult = placeRepository.search(keyword, area, pageable);

    return placeMapper.toPagedPlaceListDto(pageResult);
  }

  /** ✅ 근처 공연장 목록 조회 */
  public PlaceListResponseDto getNearbyPlaces(PlaceSearchRequestDto dto) {
    List<Place> places = placeRepository.findTop10ByOrderByNameAsc();
    return placeMapper.toPlaceListDto(places);
  }

  /** ✅ 공연장 기본 정보 조회 */
  public PlaceBasicResponseDto getPlaceBasic(String placeId) {
    Place place = findPlace(placeId);
    return placeMapper.toPlaceBasicDto(place);
  }

  /** ✅ 공연장 내 공연관 목록 */
  public BasePlaceListResponseDto<PlaceStageResponseDto> getPlaceStages(String placeId) {
    Place place = findPlace(placeId);
    List<PlaceStageResponseDto> stages = place.getPlaceStages().stream()
        .map(placeMapper::toPlaceStageDto)
        .collect(Collectors.toList());
    return placeMapper.toBasePlaceListResponse(place, stages);
  }

  /** ✅ 공연장 편의시설 목록 */
  public PlaceFacilityResponseDto getPlaceFacilities(String placeId) {
    Place place = findPlace(placeId);
    return placeMapper.toPlaceFacilityDto(place);
  }

  /** ✅ 공연장별 공연 목록 */
  public BasePlaceListResponseDto<PlacePerformanceResponseDto> getPlacePerformances(String placeId) {
    Place place = findPlace(placeId);
    List<PlacePerformanceResponseDto> performances = place.getPerformances().stream()
        .map(placeMapper::toPlacePerformanceDto)
        .collect(Collectors.toList());
    return placeMapper.toBasePlaceListResponse(place, performances);
  }

  private Place findPlace(String id) {
    return placeRepository.findById(id)
        .orElseThrow(() -> new CustomException(PlaceErrorCode.PLACE_NOT_FOUND));
  }

  private String emptyToNull(String s) {
    return (s == null || s.isBlank()) ? null : s;
  }
}
