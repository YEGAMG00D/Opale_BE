package yegam.opale_be.domain.search.performance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yegam.opale_be.domain.search.performance.service.PerformanceSearchIndexService;
import yegam.opale_be.global.response.BaseResponse;

@RestController
@RequestMapping("/api/search/index")
@RequiredArgsConstructor
public class PerformanceSearchIndexController {

  private final PerformanceSearchIndexService indexService;

  /** ✅ MySQL → Elasticsearch 전체 색인 */
  @PostMapping("/performances")
  public ResponseEntity<BaseResponse<String>> syncPerformances() {

    indexService.syncAllToElasticsearch();

    return ResponseEntity.ok(
        BaseResponse.success("Elasticsearch 색인 완료", "OK")
    );
  }


}
