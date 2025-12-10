package yegam.opale_be.domain.favorite.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.favorite.review.dto.response.FavoritePerformanceReviewResponseDto;
import yegam.opale_be.domain.favorite.review.entity.FavoritePerformanceReview;
import yegam.opale_be.domain.favorite.review.mapper.FavoritePerformanceReviewMapper;
import yegam.opale_be.domain.favorite.review.repository.FavoritePerformanceReviewRepository;
import yegam.opale_be.domain.review.performance.entity.PerformanceReview;
import yegam.opale_be.domain.review.performance.exception.PerformanceReviewErrorCode;
import yegam.opale_be.domain.review.performance.repository.PerformanceReviewRepository;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.domain.user.exception.UserErrorCode;
import yegam.opale_be.domain.user.repository.UserRepository;
import yegam.opale_be.global.exception.CustomException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FavoritePerformanceReviewService {

  private final FavoritePerformanceReviewRepository favoritePerformanceReviewRepository;
  private final PerformanceReviewRepository performanceReviewRepository;
  private final UserRepository userRepository;
  private final FavoritePerformanceReviewMapper favoritePerformanceReviewMapper;

  /** ⭐ 토글 (soft delete 사용 안 함) */
  public boolean toggleFavorite(Long userId, Long performanceReviewId) {

    // 삭제된 리뷰에 대한 토글 요청 방어
    if (!performanceReviewRepository.existsById(performanceReviewId)) {
      log.warn("⚠️ 삭제된 공연 리뷰에 대한 관심 요청 차단 reviewId={}", performanceReviewId);
      return false;
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    PerformanceReview review = performanceReviewRepository.findById(performanceReviewId)
        .orElseThrow(() -> new CustomException(PerformanceReviewErrorCode.REVIEW_NOT_FOUND));

    FavoritePerformanceReview favorite = favoritePerformanceReviewRepository
        .findByUser_UserIdAndPerformanceReview_PerformanceReviewId(userId, performanceReviewId)
        .orElse(null);

    // 신규 생성
    if (favorite == null) {
      favoritePerformanceReviewRepository.save(
          FavoritePerformanceReview.builder()
              .user(user)
              .performanceReview(review)
              .isLiked(true)
              .isDeleted(false)
              .build()
      );
      log.info("💖 공연 리뷰 관심 등록 userId={}, reviewId={}", userId, performanceReviewId);
      return true;
    }

    // soft delete → 복구
    if (favorite.getIsDeleted()) {
      favorite.setIsDeleted(false);
      favorite.setDeletedAt(null);
      favorite.setIsLiked(true);
      log.info("♻️ soft delete 복구 userId={}, reviewId={}", userId, performanceReviewId);
      return true;
    }

    // 일반 토글
    boolean newState = !favorite.getIsLiked();
    favorite.setIsLiked(newState);
    log.info("🔁 공연 리뷰 관심 토글 userId={}, reviewId={}, now={}", userId, performanceReviewId, newState);

    return newState;
  }

  /** 단건 조회 */
  @Transactional(readOnly = true)
  public boolean isLiked(Long userId, Long reviewId) {
    if (userId == null) return false;

    return favoritePerformanceReviewRepository
        .existsByUser_UserIdAndPerformanceReview_PerformanceReviewIdAndIsLikedTrue(userId, reviewId);
  }

  /** ID 목록 */
  @Transactional(readOnly = true)
  public List<Long> getFavoriteReviewIds(Long userId) {
    if (userId == null) return List.of();
    return favoritePerformanceReviewRepository.findPerformanceReviewIdsByUserId(userId);
  }

  /** 마이페이지 */
  @Transactional(readOnly = true)
  public List<FavoritePerformanceReviewResponseDto> getFavoriteReviews(Long userId) {

    userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    List<FavoritePerformanceReview> likedFavorites =
        favoritePerformanceReviewRepository.findByUser_UserIdAndIsLikedTrue(userId);

    if (likedFavorites.isEmpty()) return List.of();

    return favoritePerformanceReviewMapper.toResponseDtoList(likedFavorites);
  }
}
