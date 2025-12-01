package yegam.opale_be.domain.favorite.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
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

  /** 마이페이지용: 좋아요한 PlaceReview 엔티티 목록 */
  @Query("SELECT fpr.placeReview FROM FavoritePlaceReview fpr " +
      "WHERE fpr.user.userId = :userId AND fpr.isLiked = true")
  List<PlaceReview> findLikedPlaceReviewsByUserId(Long userId);

  // ✅ 마이페이지용 (Favorite 기준)
  List<FavoritePlaceReview> findByUser_UserIdAndIsLikedTrue(Long userId);

  /** 목록/상세 하트 표시용: 좋아요한 placeReviewId 목록 */
  @Query("SELECT fpr.placeReview.placeReviewId FROM FavoritePlaceReview fpr " +
      "WHERE fpr.user.userId = :userId AND fpr.isLiked = true")
  List<Long> findPlaceReviewIdsByUserId(Long userId);
}
