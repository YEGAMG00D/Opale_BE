package yegam.opale_be.domain.banner.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yegam.opale_be.domain.banner.dto.request.admin.AdminBannerRequestDto;
import yegam.opale_be.domain.banner.dto.response.admin.AdminBannerResponseDto;
import yegam.opale_be.domain.banner.service.BannerService;
import yegam.opale_be.global.response.BaseResponse;

import java.util.List;

@RestController
@RequestMapping("/api/admin/banners")
@RequiredArgsConstructor
public class AdminBannerController {

  private final BannerService bannerService;

  // ✅ 1. 파일 없는 등록 (JSON)
  @Operation(summary = "배너 등록 (파일 없음)")
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<AdminBannerResponseDto>> createWithoutFile(
      @RequestBody AdminBannerRequestDto dto
  ) {
    return ResponseEntity.ok(
        BaseResponse.success(
            "배너 등록 완료 (파일 없음)",
            bannerService.createBanner(dto, null)
        )
    );
  }

  // ✅ 2. 파일 있는 등록 (multipart)
  @Operation(summary = "배너 등록 (파일 포함)")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<AdminBannerResponseDto>> createWithFile(
      @RequestPart("data") AdminBannerRequestDto dto,
      @RequestPart(value = "file", required = false) MultipartFile file
  ) {
    return ResponseEntity.ok(
        BaseResponse.success(
            "배너 등록 완료",
            bannerService.createBanner(dto, file)
        )
    );
  }

  // ✅ 수정 (기존 그대로 유지)
  @Operation(summary = "배너 수정")
  @PutMapping(value = "/{bannerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<AdminBannerResponseDto>> updateBanner(
      @PathVariable Long bannerId,
      @RequestPart("data") AdminBannerRequestDto dto,
      @RequestPart(value = "file", required = false) MultipartFile file
  ) {
    return ResponseEntity.ok(
        BaseResponse.success(
            "배너 수정 완료",
            bannerService.updateBanner(bannerId, dto, file)
        )
    );
  }

  // ✅ 삭제
  @Operation(summary = "배너 삭제")
  @DeleteMapping("/{bannerId}")
  public ResponseEntity<BaseResponse<Void>> deleteBanner(@PathVariable Long bannerId) {
    bannerService.deleteBanner(bannerId);
    return ResponseEntity.ok(BaseResponse.success("배너 삭제 완료", null));
  }

  // ✅ 전체 조회
  @Operation(summary = "배너 목록 조회 (관리자)")
  @GetMapping
  public ResponseEntity<BaseResponse<List<AdminBannerResponseDto>>> getAllBanners() {
    return ResponseEntity.ok(
        BaseResponse.success("배너 목록 조회", bannerService.getAllBanners())
    );
  }
}
