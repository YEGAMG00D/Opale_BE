package yegam.opale_be.domain.banner.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yegam.opale_be.domain.banner.dto.response.MainPerformanceBannerResponseDto;
import yegam.opale_be.domain.banner.service.MainPerformanceBannerService;
import yegam.opale_be.global.response.BaseResponse;

import java.util.List;

@RestController
@RequestMapping("/api/main-performance-banners")
@RequiredArgsConstructor
public class MainPerformanceBannerController {

  private final MainPerformanceBannerService mainPerformanceBannerService;

  @Operation(summary = "메인 페이지 공연 배너 조회")
  @GetMapping
  public ResponseEntity<BaseResponse<List<MainPerformanceBannerResponseDto>>> getMainPerformanceBanners() {
    return ResponseEntity.ok(
        BaseResponse.success(
            "메인 공연 배너 조회 성공",
            mainPerformanceBannerService.getActiveBanners()
        )
    );
  }
}
