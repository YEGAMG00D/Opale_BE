package yegam.opale_be.domain.culture.performance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yegam.opale_be.domain.culture.performance.dto.request.PerformanceSearchRequestDto;
import yegam.opale_be.domain.culture.performance.dto.response.detail.*;
import yegam.opale_be.domain.culture.performance.dto.response.list.*;
import yegam.opale_be.domain.culture.performance.service.PerformanceService;
import yegam.opale_be.global.common.BaseListResponseDto;
import yegam.opale_be.global.response.BaseResponse;

@RestController
@RequestMapping("/api/performances")
@Tag(name = "Performance", description = "공연 관련 API")
@RequiredArgsConstructor
public class PerformanceController {

  private final PerformanceService performanceService;

  /** ✅ 공연 목록 조회 */
  @Operation(summary = "공연 목록 조회", description = "장르, 검색어, 정렬 기준에 따라 공연 목록을 조회합니다.")
  @PostMapping
  public ResponseEntity<BaseResponse<PerformanceListResponseDto>> getPerformanceList(
      @RequestBody @Valid PerformanceSearchRequestDto dto
  ) {
    PerformanceListResponseDto response = performanceService.getPerformanceList(dto);
    return ResponseEntity.ok(BaseResponse.success("공연 목록 조회 성공", response));
  }

  /** ✅ 인기 공연 목록 조회 */
  @Operation(summary = "인기 공연 목록 조회", description = "조회수 또는 관심 순으로 공연을 조회합니다.")
  @GetMapping("/top")
  public ResponseEntity<BaseResponse<PerformanceListResponseDto>> getTopPerformances() {
    PerformanceListResponseDto response = performanceService.getTopPerformances();
    return ResponseEntity.ok(BaseResponse.success("인기 공연 목록 조회 성공", response));
  }

  /** ✅ 오늘 개막/종료 공연 조회 */
  @Operation(summary = "오늘 개막/종료 공연 조회", description = "type=start 또는 end 로 지정 가능 (둘 다 보고 싶으면 all)")
  @GetMapping("/today")
  public ResponseEntity<BaseResponse<PerformanceListResponseDto>> getTodayPerformances(
      @RequestParam(defaultValue = "all") String type
  ) {
    PerformanceListResponseDto response = performanceService.getTodayPerformances(type);
    return ResponseEntity.ok(BaseResponse.success("오늘 공연 조회 성공", response));
  }

  /** ✅ 공연 기본 정보 조회 */
  @Operation(summary = "공연 기본 정보 조회", description = "공연 ID를 통해 공연의 기본 정보를 조회합니다.")
  @GetMapping("/{performanceId}/basic")
  public ResponseEntity<BaseResponse<PerformanceBasicResponseDto>> getPerformanceBasic(
      @PathVariable String performanceId
  ) {
    PerformanceBasicResponseDto response = performanceService.getPerformanceBasic(performanceId);
    return ResponseEntity.ok(BaseResponse.success("공연 기본 정보 조회 성공", response));
  }

  /** ✅ 공연 예매처 목록 조회 */
  @Operation(summary = "공연 예매처 목록 조회", description = "공연의 예매처 목록을 조회합니다.")
  @GetMapping("/{performanceId}/relation")
  public ResponseEntity<BaseResponse<BaseListResponseDto<PerformanceRelationResponseDto>>> getPerformanceRelations(
      @PathVariable String performanceId
  ) {
    BaseListResponseDto<PerformanceRelationResponseDto> response = performanceService.getPerformanceRelations(performanceId);
    return ResponseEntity.ok(BaseResponse.success("공연 예매처 목록 조회 성공", response));
  }

  /** ✅ 공연 영상 목록 조회 */
  @Operation(summary = "공연 관련 영상 조회", description = "공연의 관련 유튜브 영상을 조회합니다.")
  @GetMapping("/{performanceId}/video")
  public ResponseEntity<BaseResponse<BaseListResponseDto<PerformanceVideoResponseDto>>> getPerformanceVideos(
      @PathVariable String performanceId
  ) {
    BaseListResponseDto<PerformanceVideoResponseDto> response = performanceService.getPerformanceVideos(performanceId);
    return ResponseEntity.ok(BaseResponse.success("공연 영상 목록 조회 성공", response));
  }

  /** ✅ 공연 예매 정보 조회 */
  @Operation(summary = "공연 예매 정보 조회", description = "공연의 티켓 가격 및 좌석/캐스팅 이미지 정보를 조회합니다.")
  @GetMapping("/{performanceId}/booking")
  public ResponseEntity<BaseResponse<PerformanceDetailResponseDto>> getPerformanceBooking(
      @PathVariable String performanceId
  ) {
    PerformanceDetailResponseDto response = performanceService.getPerformanceBooking(performanceId);
    return ResponseEntity.ok(BaseResponse.success("공연 예매 정보 조회 성공", response));
  }

  /** ✅ 공연 소개 이미지 조회 (공식 KOPIS 이미지) */
  @Operation(summary = "공연 소개 이미지 조회", description = "공연의 공식 소개 이미지를 순서대로 조회합니다.")
  @GetMapping("/{performanceId}/infoImage")
  public ResponseEntity<BaseResponse<BaseListResponseDto<PerformanceInfoImageResponseDto>>> getPerformanceInfoImages(
      @PathVariable String performanceId
  ) {
    BaseListResponseDto<PerformanceInfoImageResponseDto> response = performanceService.getPerformanceInfoImages(performanceId);
    return ResponseEntity.ok(BaseResponse.success("공연 소개 이미지 조회 성공", response));
  }
}
