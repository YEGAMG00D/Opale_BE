package yegam.opale_be.domain.favorite.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.favorite.review.dto.response.FavoritePlaceReviewResponseDto;
import yegam.opale_be.domain.favorite.review.entity.FavoritePlaceReview;
import yegam.opale_be.domain.favorite.review.mapper.FavoritePlaceReviewMapper;
import yegam.opale_be.domain.favorite.review.repository.FavoritePlaceReviewRepository;
import yegam.opale_be.domain.review.place.entity.PlaceReview;
import yegam.opale_be.domain.review.place.exception.PlaceReviewErrorCode;
import yegam.opale_be.domain.review.place.repository.PlaceReviewRepository;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.domain.user.exception.UserErrorCode;
import yegam.opale_be.domain.user.repository.UserRepository;
import yegam.opale_be.global.exception.CustomException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FavoritePlaceReviewService {

  private final FavoritePlaceReviewRepository favoritePlaceReviewRepository;
  private final PlaceReviewRepository placeReviewRepository;
  private final UserRepository userRepository;
  private final FavoritePlaceReviewMapper favoritePlaceReviewMapper;

  /** ⭐ 토글 (soft delete 사용 안 함) */
  public boolean toggleFavorite(Long userId, Long placeReviewId) {

    // 삭제된 리뷰 체크
    if (!placeReviewRepository.existsById(placeReviewId)) {
      log.warn("⚠️ 삭제된 공연장 리뷰 관심 요청 차단 reviewId={}", placeReviewId);
      return false;
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    PlaceReview review = placeReviewRepository.findById(placeReviewId)
        .orElseThrow(() -> new CustomException(PlaceReviewErrorCode.REVIEW_NOT_FOUND));

    FavoritePlaceReview favorite = favoritePlaceReviewRepository
        .findByUser_UserIdAndPlaceReview_PlaceReviewId(userId, placeReviewId)
        .orElse(null);

    // 신규 생성
    if (favorite == null) {
      favoritePlaceReviewRepository.save(
          FavoritePlaceReview.builder()
              .user(user)
              .placeReview(review)
              .isLiked(true)
              .isDeleted(false)
              .build()
      );
      log.info("💖 공연장 리뷰 관심 등록 userId={}, reviewId={}", userId, placeReviewId);
      return true;
    }

    // soft delete → 복원
    if (favorite.getIsDeleted()) {
      favorite.setIsDeleted(false);
      favorite.setDeletedAt(null);
      favorite.setIsLiked(true);
      log.info("♻️ 공연장 리뷰 soft-delete 복구 userId={}, reviewId={}", userId, placeReviewId);
      return true;
    }

    // 일반 토글
    boolean newState = !favorite.getIsLiked();
    favorite.setIsLiked(newState);
    log.info("🔁 공연장 리뷰 관심 토글 userId={}, reviewId={}, now={}", userId, placeReviewId, newState);

    return newState;
  }

  /** 단건 조회 */
  @Transactional(readOnly = true)
  public boolean isLiked(Long userId, Long reviewId) {
    if (userId == null) return false;

    return favoritePlaceReviewRepository
        .existsByUser_UserIdAndPlaceReview_PlaceReviewIdAndIsLikedTrue(userId, reviewId);
  }

  /** ID 목록 */
  @Transactional(readOnly = true)
  public List<Long> getFavoriteReviewIds(Long userId) {
    if (userId == null) return List.of();
    return favoritePlaceReviewRepository.findPlaceReviewIdsByUserId(userId);
  }

  /** 마이페이지 */
  @Transactional(readOnly = true)
  public List<FavoritePlaceReviewResponseDto> getFavoriteReviews(Long userId) {

    userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    List<FavoritePlaceReview> likedFavorites =
        favoritePlaceReviewRepository.findByUser_UserIdAndIsLikedTrue(userId);

    if (likedFavorites.isEmpty()) return List.of();

    return favoritePlaceReviewMapper.toResponseDtoList(likedFavorites);
  }
}
