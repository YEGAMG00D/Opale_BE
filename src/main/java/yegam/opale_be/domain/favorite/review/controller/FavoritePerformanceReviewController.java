package yegam.opale_be.domain.favorite.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import yegam.opale_be.domain.favorite.review.dto.response.FavoritePerformanceReviewResponseDto;
import yegam.opale_be.domain.favorite.review.service.FavoritePerformanceReviewService;
import yegam.opale_be.global.exception.CustomException;
import yegam.opale_be.global.exception.GlobalErrorCode;
import yegam.opale_be.global.response.BaseResponse;

import java.util.List;

@RestController
@RequestMapping("/api/favorites/performance-reviews")
@Tag(name = "Favorite PerformanceReview", description = "공연 리뷰 관심(좋아요) API")
@RequiredArgsConstructor
public class FavoritePerformanceReviewController {

  private final FavoritePerformanceReviewService favoritePerformanceReviewService;

  // 1) 토글 (로그인 필요)
  @Operation(summary = "공연 리뷰 관심 토글", description = "공연 리뷰 좋아요를 추가하거나 취소합니다.")
  @PostMapping("/{performanceReviewId}")
  public ResponseEntity<BaseResponse<Boolean>> toggleFavorite(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long performanceReviewId
  ) {
    if (userId == null) throw new CustomException(GlobalErrorCode.UNAUTHORIZED);
    boolean result = favoritePerformanceReviewService.toggleFavorite(userId, performanceReviewId);
    String message = result ? "공연 리뷰를 관심 목록에 추가했습니다." : "공연 리뷰 관심을 해제했습니다.";
    return ResponseEntity.ok(BaseResponse.success(message, result));
  }

  // 2) 단건 관심 여부 (비로그인 → false)
  @Operation(summary = "공연 리뷰 관심 여부 조회", description = "특정 공연 리뷰가 관심 상태인지 확인합니다. (비로그인 시 false)")
  @GetMapping("/{performanceReviewId}")
  public ResponseEntity<BaseResponse<Boolean>> isLiked(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long performanceReviewId
  ) {
    boolean isLiked = favoritePerformanceReviewService.isLiked(userId, performanceReviewId);
    String message = isLiked ? "이 공연 리뷰는 관심 상태입니다." : "이 공연 리뷰는 관심 상태가 아닙니다.";
    return ResponseEntity.ok(BaseResponse.success(message, isLiked));
  }

  // 3) 내가 좋아요한 공연 리뷰 ID 목록 (비로그인 → [])
  @Operation(summary = "관심 공연 리뷰 ID 목록 조회", description = "로그인 사용자가 좋아요한 공연 리뷰 ID 목록을 반환합니다. (비로그인 시 빈 배열)")
  @GetMapping("/ids")
  public ResponseEntity<BaseResponse<List<Long>>> getFavoriteReviewIds(
      @AuthenticationPrincipal Long userId
  ) {
    List<Long> favoriteIds = favoritePerformanceReviewService.getFavoriteReviewIds(userId);
    return ResponseEntity.ok(BaseResponse.success("관심 공연 리뷰 ID 목록 조회 성공", favoriteIds));
  }

  // 4) 마이페이지 상세 목록 (로그인 필요)
  @Operation(summary = "공연 리뷰 관심 목록 조회", description = "사용자가 좋아요한 공연 리뷰 목록을 조회합니다.")
  @GetMapping
  public ResponseEntity<BaseResponse<List<FavoritePerformanceReviewResponseDto>>> getFavoriteReviews(
      @AuthenticationPrincipal Long userId
  ) {
    if (userId == null) throw new CustomException(GlobalErrorCode.UNAUTHORIZED);
    List<FavoritePerformanceReviewResponseDto> response = favoritePerformanceReviewService.getFavoriteReviews(userId);
    return ResponseEntity.ok(BaseResponse.success("공연 리뷰 관심 목록 조회 성공", response));
  }
}
