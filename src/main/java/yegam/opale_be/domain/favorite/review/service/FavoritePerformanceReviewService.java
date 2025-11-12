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

  // 1️⃣ 토글
  public boolean toggleFavorite(Long userId, Long performanceReviewId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    PerformanceReview review = performanceReviewRepository.findById(performanceReviewId)
        .orElseThrow(() -> new CustomException(PerformanceReviewErrorCode.REVIEW_NOT_FOUND));

    FavoritePerformanceReview favorite = favoritePerformanceReviewRepository
        .findByUser_UserIdAndPerformanceReview_PerformanceReviewId(userId, performanceReviewId)
        .orElse(null);

    if (favorite == null) {
      FavoritePerformanceReview newFavorite = FavoritePerformanceReview.builder()
          .user(user)
          .performanceReview(review)
          .isLiked(true)
          .build();
      favoritePerformanceReviewRepository.save(newFavorite);
      log.info("💖 공연 리뷰 관심 등록: userId={}, reviewId={}", userId, performanceReviewId);
      return true;
    }

    favorite.setIsLiked(!favorite.getIsLiked());
    log.info("🔁 공연 리뷰 관심 토글: userId={}, reviewId={}, now={}", userId, performanceReviewId, favorite.getIsLiked());
    return favorite.getIsLiked();
  }

  // 2️⃣ 단건 관심 여부
  @Transactional(readOnly = true)
  public boolean isLiked(Long userId, Long reviewId) {
    if (userId == null) return false;
    return favoritePerformanceReviewRepository
        .existsByUser_UserIdAndPerformanceReview_PerformanceReviewIdAndIsLikedTrue(userId, reviewId);
  }

  // 3️⃣ ID 리스트 (비로그인 → 빈 배열)
  @Transactional(readOnly = true)
  public List<Long> getFavoriteReviewIds(Long userId) {
    if (userId == null) return List.of();
    return favoritePerformanceReviewRepository.findPerformanceReviewIdsByUserId(userId);
  }

  // 4️⃣ 마이페이지 상세 목록 (빈 배열 반환)
  @Transactional(readOnly = true)
  public List<FavoritePerformanceReviewResponseDto> getFavoriteReviews(Long userId) {
    userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    List<PerformanceReview> likedReviews =
        favoritePerformanceReviewRepository.findLikedPerformanceReviewsByUserId(userId);

    if (likedReviews.isEmpty()) return List.of();

    // ✅ Mapper 변환으로 일관성 있게 처리
    return favoritePerformanceReviewMapper.toResponseDtoList(likedReviews);
  }
}
