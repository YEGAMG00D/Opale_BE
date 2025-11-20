package yegam.opale_be.domain.review.performance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import yegam.opale_be.domain.review.common.ReviewType;
import yegam.opale_be.domain.review.performance.dto.request.PerformanceReviewRequestDto;
import yegam.opale_be.domain.review.performance.dto.response.PerformanceReviewResponseDto;
import yegam.opale_be.domain.review.performance.dto.response.PerformanceReviewListResponseDto;
import yegam.opale_be.domain.review.performance.service.PerformanceReviewService;
import yegam.opale_be.global.exception.CustomException;
import yegam.opale_be.global.exception.GlobalErrorCode;
import yegam.opale_be.global.response.BaseResponse;

@RestController
@RequestMapping("/api/reviews/performances")
@Tag(name = "Performance Review", description = "공연 리뷰 API")
@RequiredArgsConstructor
public class PerformanceReviewController {

  private final PerformanceReviewService reviewService;

  /** 🎭 단일 공연 리뷰 조회 (비로그인 가능) */
  @Operation(summary = "공연 리뷰 단건 조회", description = "특정 공연 리뷰 1건을 조회합니다.")
  @GetMapping("/{reviewId}")
  public ResponseEntity<BaseResponse<PerformanceReviewResponseDto>> getReview(@PathVariable Long reviewId) {
    PerformanceReviewResponseDto response = reviewService.getReview(reviewId);
    return ResponseEntity.ok(BaseResponse.success("공연 리뷰 조회 성공", response));
  }

  /** 공연별 리뷰 목록 조회 (비로그인 가능) */
  @Operation(summary = "공연별 리뷰 목록 조회", description = "공연 ID 기준으로 등록된 리뷰 목록을 조회합니다. (reviewType 옵션)")
  @GetMapping("/performance/{performanceId}")
  public ResponseEntity<BaseResponse<PerformanceReviewListResponseDto>> getReviewsByPerformance(
      @PathVariable String performanceId,
      @RequestParam(required = false) ReviewType reviewType
  ) {
    PerformanceReviewListResponseDto response = reviewService.getReviewsByPerformance(performanceId, reviewType);
    return ResponseEntity.ok(BaseResponse.success("공연별 리뷰 목록 조회 성공", response));
  }

  /** 작성한 본인 공연 리뷰 목록 조회 (로그인 필요) */
  @Operation(summary = "작성한 본인 공연 리뷰 목록 조회", description = "사용자가 작성한 공연 리뷰 목록을 조회합니다.")
  @GetMapping("/me")
  public ResponseEntity<BaseResponse<PerformanceReviewListResponseDto>> getMyReviews(
      @AuthenticationPrincipal Long userId,
      @RequestParam(required = false) ReviewType reviewType
  ) {
    if (userId == null) throw new CustomException(GlobalErrorCode.UNAUTHORIZED);
    PerformanceReviewListResponseDto response = reviewService.getReviewsByUser(userId, reviewType);
    return ResponseEntity.ok(BaseResponse.success("내 공연 리뷰 목록 조회 성공", response));
  }

  /** 특정 회원의 공연 리뷰 목록 조회 (비로그인 가능) */
  @Operation(summary = "특정 회원의 공연 리뷰 목록 조회", description = "회원 ID와 리뷰 타입을 기준으로 해당 사용자가 작성한 공연 리뷰 목록을 조회합니다.")
  @GetMapping("/user/{userId}")
  public ResponseEntity<BaseResponse<PerformanceReviewListResponseDto>> getReviewsByUserPublic(
      @PathVariable Long userId,
      @RequestParam(required = false) ReviewType reviewType
  ) {
    PerformanceReviewListResponseDto response = reviewService.getReviewsByUser(userId, reviewType);
    return ResponseEntity.ok(BaseResponse.success("회원 공연 리뷰 목록 조회 성공", response));
  }

  /** 공연 리뷰 작성 (로그인 필요) */
  @Operation(summary = "공연 리뷰 작성", description = "새 공연 리뷰를 작성합니다.")
  @PostMapping
  public ResponseEntity<BaseResponse<PerformanceReviewResponseDto>> createReview(
      @AuthenticationPrincipal Long userId,
      @RequestBody PerformanceReviewRequestDto dto
  ) {
    if (userId == null) throw new CustomException(GlobalErrorCode.UNAUTHORIZED);
    PerformanceReviewResponseDto response = reviewService.createReview(userId, dto);
    return ResponseEntity.ok(BaseResponse.success("공연 리뷰 작성 성공", response));
  }

  /** 공연 리뷰 수정 (로그인 필요) */
  @Operation(summary = "공연 리뷰 수정", description = "본인이 작성한 공연 리뷰를 수정합니다.")
  @PutMapping("/{reviewId}")
  public ResponseEntity<BaseResponse<PerformanceReviewResponseDto>> updateReview(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long reviewId,
      @RequestBody PerformanceReviewRequestDto dto
  ) {
    if (userId == null) throw new CustomException(GlobalErrorCode.UNAUTHORIZED);
    PerformanceReviewResponseDto response = reviewService.updateReview(userId, reviewId, dto);
    return ResponseEntity.ok(BaseResponse.success("공연 리뷰 수정 성공", response));
  }

  /** 공연 리뷰 삭제 (로그인 필요) */
  @Operation(summary = "공연 리뷰 삭제", description = "본인이 작성한 공연 리뷰를 삭제합니다.")
  @DeleteMapping("/{reviewId}")
  public ResponseEntity<BaseResponse<String>> deleteReview(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long reviewId
  ) {
    if (userId == null) throw new CustomException(GlobalErrorCode.UNAUTHORIZED);
    reviewService.deleteReview(userId, reviewId);
    return ResponseEntity.ok(BaseResponse.success("공연 리뷰 삭제 성공", null));
  }
}
