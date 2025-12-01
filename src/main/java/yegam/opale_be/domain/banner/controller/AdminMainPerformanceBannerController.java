package yegam.opale_be.domain.banner.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yegam.opale_be.domain.banner.dto.request.admin.AdminMainPerformanceBannerRequestDto;
import yegam.opale_be.domain.banner.dto.response.admin.AdminMainPerformanceBannerResponseDto;
import yegam.opale_be.domain.banner.service.MainPerformanceBannerService;
import yegam.opale_be.global.response.BaseResponse;

import java.util.List;

@RestController
@RequestMapping("/api/admin/main-performance-banners")
@RequiredArgsConstructor
public class AdminMainPerformanceBannerController {

  private final MainPerformanceBannerService mainPerformanceBannerService;

  @Operation(summary = "메인 공연 배너 등록")
  @PostMapping
  public ResponseEntity<BaseResponse<AdminMainPerformanceBannerResponseDto>> createMainPerformanceBanner(
      @RequestBody AdminMainPerformanceBannerRequestDto dto
  ) {
    return ResponseEntity.ok(
        BaseResponse.success(
            "메인 공연 배너 등록 완료",
            mainPerformanceBannerService.create(dto)
        )
    );
  }

  @Operation(summary = "메인 공연 배너 수정")
  @PutMapping("/{bannerId}")
  public ResponseEntity<BaseResponse<AdminMainPerformanceBannerResponseDto>> updateMainPerformanceBanner(
      @PathVariable Long bannerId,
      @RequestBody AdminMainPerformanceBannerRequestDto dto
  ) {
    return ResponseEntity.ok(
        BaseResponse.success(
            "메인 공연 배너 수정 완료",
            mainPerformanceBannerService.update(bannerId, dto)
        )
    );
  }

  @Operation(summary = "메인 공연 배너 삭제")
  @DeleteMapping("/{bannerId}")
  public ResponseEntity<BaseResponse<Void>> deleteMainPerformanceBanner(
      @PathVariable Long bannerId
  ) {
    mainPerformanceBannerService.delete(bannerId);
    return ResponseEntity.ok(BaseResponse.success("메인 공연 배너 삭제 완료", null));
  }

  @Operation(summary = "메인 공연 배너 목록 조회 (관리자)")
  @GetMapping
  public ResponseEntity<BaseResponse<List<AdminMainPerformanceBannerResponseDto>>> getAllMainPerformanceBanners() {
    return ResponseEntity.ok(
        BaseResponse.success(
            "메인 공연 배너 목록 조회",
            mainPerformanceBannerService.getAll()
        )
    );
  }
}
