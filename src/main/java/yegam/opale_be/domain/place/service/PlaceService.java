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
import yegam.opale_be.domain.review.common.ReviewType;
import yegam.opale_be.domain.review.place.repository.PlaceReviewRepository;
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
  private final PlaceReviewRepository placeReviewRepository;

  // -----------------------------------------------------------
  // 공연장 목록 조회 (리뷰 통계 포함)
  // -----------------------------------------------------------
  public PlaceListResponseDto getPlaceList(PlaceListRequestDto dto) {

    String keyword = emptyToNull(dto.getKeyword());
    String areaFilter = emptyToNull(dto.getArea());

    int page = (dto.getPage() != null && dto.getPage() > 0) ? dto.getPage() - 1 : 0;
    int size = (dto.getSize() != null && dto.getSize() > 0) ? dto.getSize() : 20;

    PageRequest pageable = PageRequest.of(page, size);

    // 1) 프론트에서 오는 area 값(전체/서울/경기/충청/강원/경상/전라/제주)을
    //    DB에 저장된 실제 area 값 리스트(서울특별시, 경기도, 부산광역시 등)로 변환
    List<String> areaList = resolveAreaFilter(areaFilter);
    List<String> areasParam = areaList.isEmpty() ? null : areaList;

    Page<Place> pageResult =
        placeRepository.search(keyword, areasParam, pageable);

    return placeMapper.toPagedPlaceListDtoWithStats(pageResult, placeReviewRepository);
  }

  // -----------------------------------------------------------
  // 근처 공연장 조회
  // -----------------------------------------------------------
  public PlaceNearbyListResponseDto getNearbyPlaces(PlaceNearbyRequestDto dto) {
    if (dto.getLatitude() == null || dto.getLongitude() == null) {
      throw new CustomException(PlaceErrorCode.INVALID_COORDINATE);
    }

    double lat = dto.getLatitude().doubleValue();
    double lon = dto.getLongitude().doubleValue();
    int radius = dto.getRadius() != null ? dto.getRadius() : 3000;

    String sortType =
        (dto.getSortType() != null && !dto.getSortType().isBlank())
            ? dto.getSortType()
            : "거리순";

    List<Object[]> result =
        placeRepository.findNearbyPlacesWithDistance(lat, lon, radius);

    PlaceNearbyListResponseDto response =
        placeMapper.toNearbyListDto(
            result,
            dto.getLatitude(),
            dto.getLongitude(),
            radius,
            sortType
        );

    // 리뷰 개수 / 평점 추가
    response.getPlaces().forEach(p -> {
      Long count = placeReviewRepository.countByPlaceIdAndType(p.getPlaceId(), ReviewType.PLACE);
      Double avg = placeReviewRepository.avgRatingByPlaceIdAndType(p.getPlaceId(), ReviewType.PLACE);

      p.setReviewCount(count != null ? count : 0L);
      p.setRating(avg != null ? avg : 0.0);
    });

    if ("이름순".equals(sortType)) {
      response.getPlaces().sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
    } else {
      response.getPlaces().sort((a, b) -> Double.compare(a.getDistance(), b.getDistance()));
    }

    return response;
  }

  // -----------------------------------------------------------
  // 공연장 기본 정보 조회 (+ 리뷰 통계)
  // -----------------------------------------------------------
  public PlaceBasicResponseDto getPlaceBasic(String placeId) {
    Place place = findPlace(placeId);
    return placeMapper.toPlaceBasicDtoWithStats(place, placeReviewRepository);
  }

  // -----------------------------------------------------------
  // 공연관 목록
  // -----------------------------------------------------------
  public BasePlaceListResponseDto<PlaceStageResponseDto> getPlaceStages(String placeId) {
    Place place = findPlace(placeId);

    List<PlaceStageResponseDto> stages =
        place.getPlaceStages().stream()
            .map(placeMapper::toPlaceStageDto)
            .collect(Collectors.toList());

    return placeMapper.toBasePlaceListResponse(place, stages);
  }

  // -----------------------------------------------------------
  // 편의시설
  // -----------------------------------------------------------
  public PlaceFacilityResponseDto getPlaceFacilities(String placeId) {
    Place place = findPlace(placeId);
    return placeMapper.toPlaceFacilityDto(place);
  }

  // -----------------------------------------------------------
  // 공연장별 공연 목록
  // -----------------------------------------------------------
  public BasePlaceListResponseDto<PlacePerformanceResponseDto> getPlacePerformances(String placeId) {
    Place place = findPlace(placeId);

    List<PlacePerformanceResponseDto> performances =
        place.getPerformances().stream()
            .map(placeMapper::toPlacePerformanceDto)
            .collect(Collectors.toList());

    return placeMapper.toBasePlaceListResponse(place, performances);
  }

  // -----------------------------------------------------------
  // util
  // -----------------------------------------------------------
  private Place findPlace(String id) {
    return placeRepository.findById(id)
        .orElseThrow(() -> new CustomException(PlaceErrorCode.PLACE_NOT_FOUND));
  }

  private String emptyToNull(String s) {
    return (s == null || s.isBlank()) ? null : s;
  }

  /**
   * 프론트에서 넘어오는 area 필터값을 DB area 값 리스트로 변환
   *
   * 프론트 값: 전체 / 서울 / 경기 / 충청 / 강원 / 경상 / 전라 / 제주
   * DB 값:    서울특별시, 경기도, 인천광역시, 부산광역시, 경상북도, 경상남도, ...
   */
  private List<String> resolveAreaFilter(String areaFilter) {
    if (areaFilter == null || areaFilter.isBlank() || "전체".equals(areaFilter)) {
      // 필터 없이 전체 검색
      return List.of();
    }

    switch (areaFilter) {
      case "서울":
        return List.of("서울특별시");

      case "경기":
        // 수도권: 경기도 + 인천광역시
        return List.of("경기도", "인천광역시");

      case "충청":
        return List.of(
            "충청북도",
            "충청남도",
            "세종특별자치시",
            "대전광역시"
        );

      case "강원":
        // 데이터는 강원도/강원특별자치도 섞여 있을 수 있지만
        // 강원특별자치도도 혹시 모를 상황 대비해서 함께 포함해도 됨
        return List.of("강원도", "강원특별자치도");

      case "경상":
        return List.of(
            "경상북도",
            "경상남도",
            "부산광역시",
            "대구광역시",
            "울산광역시"
        );

      case "전라":
        return List.of(
            "전라북도",
            "전북특별자치도",
            "전라남도",
            "광주광역시"
        );

      case "제주":
        return List.of("제주특별자치도");

      default:
        // 혹시 프론트에서 이미 "서울특별시" 같은 풀네임을 보내는 경우도
        // 안전하게 처리해주기 위한 fallback
        return List.of(areaFilter);
    }
  }
}
