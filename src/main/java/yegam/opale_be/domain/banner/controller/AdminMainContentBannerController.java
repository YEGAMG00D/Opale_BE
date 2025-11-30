package yegam.opale_be.domain.banner.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yegam.opale_be.domain.banner.dto.request.admin.AdminMainContentBannerRequestDto;
import yegam.opale_be.domain.banner.dto.response.admin.AdminMainContentBannerResponseDto;
import yegam.opale_be.domain.banner.service.MainContentBannerService;
import yegam.opale_be.global.response.BaseResponse;

import java.util.List;

@RestController
@RequestMapping("/api/admin/main-content-banners")
@RequiredArgsConstructor
public class AdminMainContentBannerController {

  private final MainContentBannerService mainContentBannerService;

  // ✅ ✅ ✅ 1. 파일 "없는" 등록 (JSON 전용)
  @Operation(summary = "메인 콘텐츠 배너 등록 (파일 없음)")
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<AdminMainContentBannerResponseDto>> createWithoutFile(
      @RequestBody AdminMainContentBannerRequestDto dto
  ) {
    return ResponseEntity.ok(
        BaseResponse.success(
            "메인 콘텐츠 배너 등록 완료 (파일 없음)",
            mainContentBannerService.create(dto, null)
        )
    );
  }

  // ✅ ✅ ✅ 2. 파일 "있는" 등록 (multipart)
  @Operation(summary = "메인 콘텐츠 배너 등록 (파일 포함)")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<AdminMainContentBannerResponseDto>> createWithFile(
      @RequestPart("data") AdminMainContentBannerRequestDto dto,
      @RequestPart(value = "file", required = false) MultipartFile file
  ) {
    return ResponseEntity.ok(
        BaseResponse.success(
            "메인 콘텐츠 배너 등록 완료",
            mainContentBannerService.create(dto, file)
        )
    );
  }

  // ✅ ✅ ✅ 3. 수정 (파일 포함)
  @Operation(summary = "메인 콘텐츠 배너 수정")
  @PutMapping(value = "/{contentBannerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<AdminMainContentBannerResponseDto>> update(
      @PathVariable Long contentBannerId,
      @RequestPart("data") AdminMainContentBannerRequestDto dto,
      @RequestPart(value = "file", required = false) MultipartFile file
  ) {
    return ResponseEntity.ok(
        BaseResponse.success(
            "메인 콘텐츠 배너 수정 완료",
            mainContentBannerService.update(contentBannerId, dto, file)
        )
    );
  }

  @Operation(summary = "메인 콘텐츠 배너 삭제")
  @DeleteMapping("/{contentBannerId}")
  public ResponseEntity<BaseResponse<Void>> delete(
      @PathVariable Long contentBannerId
  ) {
    mainContentBannerService.delete(contentBannerId);
    return ResponseEntity.ok(BaseResponse.success("메인 콘텐츠 배너 삭제 완료", null));
  }

  @Operation(summary = "메인 콘텐츠 배너 목록 조회 (관리자)")
  @GetMapping
  public ResponseEntity<BaseResponse<List<AdminMainContentBannerResponseDto>>> getAll() {
    return ResponseEntity.ok(
        BaseResponse.success(
            "메인 콘텐츠 배너 목록 조회",
            mainContentBannerService.getAll()
        )
    );
  }
}
