package yegam.opale_be.domain.recommendation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import yegam.opale_be.domain.recommendation.dto.response.RecommendationListResponseDto;
import yegam.opale_be.domain.recommendation.service.RecommendationService;
import yegam.opale_be.global.exception.CustomException;
import yegam.opale_be.global.exception.GlobalErrorCode;
import yegam.opale_be.global.response.BaseResponse;

@RestController
@RequestMapping("/api/recommendations")
@Tag(name = "Recommendation", description = "공연 추천 API")
@RequiredArgsConstructor
public class RecommendationController {

  private final RecommendationService recommendationService;

  /* ---------------------------------------------------------
     1) 개인화 추천 (로그인 사용자)
     --------------------------------------------------------- */
  @Operation(summary = "개인화 추천", description = "사용자의 선호 벡터 기반 공연 추천을 제공합니다.")
  @GetMapping("/user")
  public ResponseEntity<BaseResponse<RecommendationListResponseDto>> getUserRecommendations(
      @AuthenticationPrincipal Long userId,
      @RequestParam(required = false) Integer size,
      @RequestParam(required = false) String sort
  ) {
    if (userId == null) throw new CustomException(GlobalErrorCode.UNAUTHORIZED);

    RecommendationListResponseDto response =
        recommendationService.getUserRecommendations(userId, size, sort);

    return ResponseEntity.ok(BaseResponse.success("개인화 추천 조회 성공", response));
  }

  /* ---------------------------------------------------------
     2) 운영자용 사용자 추천 (userId 직접 입력)
     --------------------------------------------------------- */
  @Operation(summary = "운영자용 개인화 추천", description = "특정 사용자의 추천 결과를 userId로 조회합니다.")
  @GetMapping("/user/{userId}")
  public ResponseEntity<BaseResponse<RecommendationListResponseDto>> getUserRecommendationsByAdmin(
      @PathVariable Long userId,
      @RequestParam(required = false) Integer size,
      @RequestParam(required = false) String sort
  ) {
    RecommendationListResponseDto response =
        recommendationService.getUserRecommendationsByAdmin(userId, size, sort);

    return ResponseEntity.ok(BaseResponse.success("운영자용 개인화 추천 조회 성공", response));
  }

  /* ---------------------------------------------------------
     3) 특정 공연과 비슷한 공연 추천
     --------------------------------------------------------- */
  @Operation(summary = "비슷한 공연 추천", description = "특정 공연과 유사한 공연을 추천합니다.")
  @GetMapping("/performance/{performanceId}")
  public ResponseEntity<BaseResponse<RecommendationListResponseDto>> getSimilarPerformances(
      @PathVariable String performanceId,
      @RequestParam(required = false) Integer size,
      @RequestParam(required = false) String sort
  ) {
    RecommendationListResponseDto response =
        recommendationService.getSimilarPerformances(performanceId, size, sort);

    return ResponseEntity.ok(BaseResponse.success("비슷한 공연 추천 조회 성공", response));
  }

  /* ---------------------------------------------------------
     4) 장르 기반 추천
     --------------------------------------------------------- */
  @Operation(summary = "장르 기반 추천", description = "특정 장르 내에서 인기/최신 공연을 추천합니다.")
  @GetMapping("/genre")
  public ResponseEntity<BaseResponse<RecommendationListResponseDto>> getGenreRecommendations(
      @RequestParam String genre,
      @RequestParam(required = false) Integer size,
      @RequestParam(required = false) String sort
  ) {
    RecommendationListResponseDto response =
        recommendationService.getGenreRecommendations(genre, size, sort);

    return ResponseEntity.ok(BaseResponse.success("장르 기반 추천 조회 성공", response));
  }

  /* ---------------------------------------------------------
     5) 인기 공연 추천
     --------------------------------------------------------- */
  @Operation(summary = "인기 공연 추천", description = "사이트 전체에서 인기 많은 공연을 추천합니다.")
  @GetMapping("/popular")
  public ResponseEntity<BaseResponse<RecommendationListResponseDto>> getPopularRecommendations(
      @RequestParam(required = false) Integer size
  ) {
    RecommendationListResponseDto response =
        recommendationService.getPopularRecommendations(size);

    return ResponseEntity.ok(BaseResponse.success("인기 공연 추천 조회 성공", response));
  }

  /* ---------------------------------------------------------
     6) 최신 공연 추천
     --------------------------------------------------------- */
  @Operation(summary = "최신 공연 추천", description = "가장 최신 업데이트된 공연을 추천합니다.")
  @GetMapping("/latest")
  public ResponseEntity<BaseResponse<RecommendationListResponseDto>> getLatestRecommendations(
      @RequestParam(required = false) Integer size
  ) {
    RecommendationListResponseDto response =
        recommendationService.getLatestRecommendations(size);

    return ResponseEntity.ok(BaseResponse.success("최신 공연 추천 조회 성공", response));
  }
}
