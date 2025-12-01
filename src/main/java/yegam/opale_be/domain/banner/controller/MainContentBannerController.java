package yegam.opale_be.domain.banner.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yegam.opale_be.domain.banner.dto.response.MainContentBannerResponseDto;
import yegam.opale_be.domain.banner.service.MainContentBannerService;
import yegam.opale_be.global.response.BaseResponse;

import java.util.List;

@RestController
@RequestMapping("/api/main-content-banners")
@RequiredArgsConstructor
public class MainContentBannerController {

  private final MainContentBannerService mainContentBannerService;

  @Operation(summary = "메인 페이지 콘텐츠 배너 조회")
  @GetMapping
  public ResponseEntity<BaseResponse<List<MainContentBannerResponseDto>>> getMainContentBanners() {
    return ResponseEntity.ok(
        BaseResponse.success(
            "메인 콘텐츠 배너 조회 성공",
            mainContentBannerService.getActiveBanners()
        )
    );
  }
}
