package yegam.opale_be.domain.favorite.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import yegam.opale_be.domain.favorite.review.entity.FavoritePlaceReview;
import yegam.opale_be.domain.review.place.entity.PlaceReview;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritePlaceReviewRepository extends JpaRepository<FavoritePlaceReview, Long> {

  Optional<FavoritePlaceReview> findByUser_UserIdAndPlaceReview_PlaceReviewId(Long userId, Long placeReviewId);

  boolean existsByUser_UserIdAndPlaceReview_PlaceReviewIdAndIsLikedTrue(Long userId, Long placeReviewId);

  /** 마이페이지용 */
  @Query("""
      SELECT fpr.placeReview
      FROM FavoritePlaceReview fpr
      WHERE fpr.user.userId = :userId
        AND fpr.isLiked = true
        AND fpr.isDeleted = false
      """)
  List<PlaceReview> findLikedPlaceReviewsByUserId(Long userId);

  List<FavoritePlaceReview> findByUser_UserIdAndIsLikedTrue(Long userId);

  /** 목록/상세 하트 표시용 */
  @Query("""
      SELECT fpr.placeReview.placeReviewId
      FROM FavoritePlaceReview fpr
      WHERE fpr.user.userId = :userId
        AND fpr.isLiked = true
        AND fpr.isDeleted = false
        AND fpr.placeReview IS NOT NULL
      """)
  List<Long> findPlaceReviewIdsByUserId(Long userId);

  void deleteByPlaceReview_PlaceReviewId(Long placeReviewId);

  /** ⭐ 공연장 리뷰 soft delete 시 favorite도 soft delete */
  @Modifying
  @Query("""
      UPDATE FavoritePlaceReview f
      SET f.isDeleted = true,
          f.deletedAt = CURRENT_TIMESTAMP,
          f.isLiked = false
      WHERE f.placeReview.placeReviewId = :reviewId
      """)
  void softDeleteByReviewId(Long reviewId);
}
