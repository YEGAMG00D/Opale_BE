package yegam.opale_be.domain.search.performance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yegam.opale_be.domain.search.performance.document.PerformanceSearchDocument;
import yegam.opale_be.domain.search.performance.service.PerformanceSearchIndexService;
import yegam.opale_be.domain.search.performance.dto.PerformanceAutoCompleteResponseDto;
import yegam.opale_be.global.response.BaseResponse;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class PerformanceSearchQueryController {

  private final PerformanceSearchIndexService indexService;

  /** ✅ 자동완성 + 오타허용 공연 검색 */
  @GetMapping("/performances")
  public ResponseEntity<BaseResponse<List<PerformanceSearchDocument>>> search(
      @RequestParam String keyword
  ) {
    return ResponseEntity.ok(
        BaseResponse.success(
            "검색 성공",
            indexService.search(keyword)
        )
    );
  }



  /** ✅ 공연 자동완성 */
  @GetMapping("/performances/suggest")
  public ResponseEntity<BaseResponse<List<PerformanceAutoCompleteResponseDto>>> autoComplete(
      @RequestParam String keyword
  ) {
    return ResponseEntity.ok(
        BaseResponse.success(
            "자동완성 성공",
            indexService.autoComplete(keyword)
        )
    );
  }




}
