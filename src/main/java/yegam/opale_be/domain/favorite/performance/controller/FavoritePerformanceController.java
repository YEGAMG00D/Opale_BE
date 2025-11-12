package yegam.opale_be.domain.favorite.performance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import yegam.opale_be.domain.favorite.performance.dto.response.FavoritePerformanceResponseDto;
import yegam.opale_be.domain.favorite.performance.service.FavoritePerformanceService;
import yegam.opale_be.global.exception.CustomException;
import yegam.opale_be.global.exception.GlobalErrorCode;
import yegam.opale_be.global.response.BaseResponse;

import java.util.List;

@RestController
@RequestMapping("/api/favorites/performances")
@Tag(name = "Favorite Performance", description = "공연 관심(좋아요) API")
@RequiredArgsConstructor
public class FavoritePerformanceController {

  private final FavoritePerformanceService favoritePerformanceService;

  // 1) 토글 (로그인 필요)
  @Operation(summary = "공연 관심 토글", description = "공연 좋아요를 추가하거나 취소합니다.")
  @PostMapping("/{performanceId}")
  public ResponseEntity<BaseResponse<Boolean>> toggleFavorite(
      @AuthenticationPrincipal Long userId,
      @PathVariable String performanceId
  ) {
    if (userId == null) throw new CustomException(GlobalErrorCode.UNAUTHORIZED);
    boolean result = favoritePerformanceService.toggleFavorite(userId, performanceId);
    String message = result ? "공연을 관심 목록에 추가했습니다." : "공연 관심을 해제했습니다.";
    return ResponseEntity.ok(BaseResponse.success(message, result));
  }

  // 2) 단건 관심 여부 (비로그인 허용 → false)
  @Operation(summary = "공연 관심 여부 조회", description = "특정 공연이 관심 상태인지 확인합니다. (비로그인 시 false)")
  @GetMapping("/{performanceId}")
  public ResponseEntity<BaseResponse<Boolean>> isLiked(
      @AuthenticationPrincipal Long userId,
      @PathVariable String performanceId
  ) {
    boolean isLiked = favoritePerformanceService.isLiked(userId, performanceId);
    String message = isLiked ? "이 공연은 관심 상태입니다." : "이 공연은 관심 상태가 아닙니다.";
    return ResponseEntity.ok(BaseResponse.success(message, isLiked));
  }

  // 3) 내가 좋아요한 공연 ID 목록 (비로그인 허용 → [])
  @Operation(summary = "관심 공연 ID 목록 조회", description = "로그인 사용자가 좋아요한 공연 ID 목록을 반환합니다. (비로그인 시 빈 배열)")
  @GetMapping("/ids")
  public ResponseEntity<BaseResponse<List<String>>> getFavoritePerformanceIds(
      @AuthenticationPrincipal Long userId
  ) {
    List<String> favoriteIds = favoritePerformanceService.getFavoritePerformanceIds(userId);
    return ResponseEntity.ok(BaseResponse.success("관심 공연 ID 목록 조회 성공", favoriteIds));
  }

  // 4) 마이페이지용 상세 목록 (로그인 필요)
  @Operation(summary = "공연 관심 목록 조회", description = "사용자가 좋아요한 공연 목록을 조회합니다.")
  @GetMapping
  public ResponseEntity<BaseResponse<List<FavoritePerformanceResponseDto>>> getFavoritePerformances(
      @AuthenticationPrincipal Long userId
  ) {
    if (userId == null) throw new CustomException(GlobalErrorCode.UNAUTHORIZED);
    List<FavoritePerformanceResponseDto> response = favoritePerformanceService.getFavoritePerformances(userId);
    return ResponseEntity.ok(BaseResponse.success("공연 관심 목록 조회 성공", response));
  }
}
