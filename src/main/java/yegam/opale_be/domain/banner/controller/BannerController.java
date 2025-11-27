package yegam.opale_be.domain.banner.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yegam.opale_be.domain.banner.dto.response.MainBannerResponseDto;
import yegam.opale_be.domain.banner.service.BannerService;
import yegam.opale_be.global.response.BaseResponse;

import java.util.List;

@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
public class BannerController {

  private final BannerService bannerService;

  @Operation(summary = "메인 페이지 배너 조회")
  @GetMapping("/main")
  public ResponseEntity<BaseResponse<List<MainBannerResponseDto>>> getMainBanners() {
    return ResponseEntity.ok(
        BaseResponse.success(
            "메인 배너 조회 성공",
            bannerService.getMainBanners()
        )
    );
  }
}
