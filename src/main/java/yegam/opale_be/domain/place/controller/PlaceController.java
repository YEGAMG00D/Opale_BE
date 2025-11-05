package yegam.opale_be.domain.place.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yegam.opale_be.domain.place.dto.request.PlaceSearchRequestDto;
import yegam.opale_be.domain.place.dto.response.detail.*;
import yegam.opale_be.domain.place.dto.response.list.PlaceListResponseDto;
import yegam.opale_be.domain.place.service.PlaceService;
import yegam.opale_be.global.common.BasePlaceListResponseDto;
import yegam.opale_be.global.response.BaseResponse;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
@Tag(name = "Place", description = "공연장 관련 API")
public class PlaceController {

  private final PlaceService placeService;

  /** ✅ 공연장 목록 조회 */
  @Operation(summary = "공연장 목록 조회", description = "검색어, 지역, 장르, 정렬 기준에 따라 공연장 목록을 조회합니다.")
  @PostMapping
  public ResponseEntity<BaseResponse<PlaceListResponseDto>> getPlaceList(
      @RequestBody @Valid PlaceSearchRequestDto dto
  ) {
    PlaceListResponseDto response = placeService.getPlaceList(dto);
    return ResponseEntity.ok(BaseResponse.success("공연장 목록 조회 성공", response));
  }

  /** ✅ 좌표 기반 근처 공연장 목록 조회 */
  @Operation(summary = "좌표 기반 근처 공연장 목록 조회", description = "지도 기반으로 반경 내 공연장을 조회합니다.")
  @PostMapping("/nearby")
  public ResponseEntity<BaseResponse<PlaceListResponseDto>> getNearbyPlaces(
      @RequestBody @Valid PlaceSearchRequestDto dto
  ) {
    PlaceListResponseDto response = placeService.getNearbyPlaces(dto);
    return ResponseEntity.ok(BaseResponse.success("근처 공연장 목록 조회 성공", response));
  }

  /** ✅ 공연장 기본 정보 조회 */
  @Operation(summary = "공연장 기본 정보 조회", description = "공연장 ID를 통해 기본 정보를 조회합니다.")
  @GetMapping("/{placeId}/basic")
  public ResponseEntity<BaseResponse<PlaceBasicResponseDto>> getPlaceBasic(
      @PathVariable String placeId
  ) {
    PlaceBasicResponseDto response = placeService.getPlaceBasic(placeId);
    return ResponseEntity.ok(BaseResponse.success("공연장 기본 정보 조회 성공", response));
  }

  /** ✅ 공연장 내 공연관 목록 조회 */
  @Operation(summary = "공연장 내 공연관(무대) 목록 조회", description = "공연장에 포함된 공연관(무대) 정보를 조회합니다.")
  @GetMapping("/{placeId}/stages")
  public ResponseEntity<BaseResponse<BasePlaceListResponseDto<PlaceStageResponseDto>>> getPlaceStages(
      @PathVariable String placeId
  ) {
    BasePlaceListResponseDto<PlaceStageResponseDto> response = placeService.getPlaceStages(placeId);
    return ResponseEntity.ok(BaseResponse.success("공연장 내 공연관 목록 조회 성공", response));
  }

  /** ✅ 공연장 편의시설 정보 조회 */
  @Operation(summary = "공연장 편의시설 정보 조회", description = "공연장 내의 편의시설(Y/N)을 조회합니다.")
  @GetMapping("/{placeId}/facilities")
  public ResponseEntity<BaseResponse<PlaceFacilityResponseDto>> getPlaceFacilities(
      @PathVariable String placeId
  ) {
    PlaceFacilityResponseDto response = placeService.getPlaceFacilities(placeId);
    return ResponseEntity.ok(BaseResponse.success("공연장 편의시설 조회 성공", response));
  }

  /** ✅ 공연장별 공연 목록 조회 */
  @Operation(summary = "공연장별 공연 목록 조회", description = "특정 공연장에서 진행되는 공연 목록을 조회합니다.")
  @GetMapping("/{placeId}/performances")
  public ResponseEntity<BaseResponse<BasePlaceListResponseDto<PlacePerformanceResponseDto>>> getPlacePerformances(
      @PathVariable String placeId
  ) {
    BasePlaceListResponseDto<PlacePerformanceResponseDto> response = placeService.getPlacePerformances(placeId);
    return ResponseEntity.ok(BaseResponse.success("공연장별 공연 목록 조회 성공", response));
  }
}
