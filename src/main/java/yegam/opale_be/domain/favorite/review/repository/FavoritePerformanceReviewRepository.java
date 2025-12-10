package yegam.opale_be.domain.favorite.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import yegam.opale_be.domain.favorite.review.entity.FavoritePerformanceReview;
import yegam.opale_be.domain.review.performance.entity.PerformanceReview;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritePerformanceReviewRepository extends JpaRepository<FavoritePerformanceReview, Long> {

  Optional<FavoritePerformanceReview> findByUser_UserIdAndPerformanceReview_PerformanceReviewId(Long userId, Long performanceReviewId);

  boolean existsByUser_UserIdAndPerformanceReview_PerformanceReviewIdAndIsLikedTrue(Long userId, Long performanceReviewId);

  /** 마이페이지용 */
  @Query("""
      SELECT fpr.performanceReview
      FROM FavoritePerformanceReview fpr
      WHERE fpr.user.userId = :userId
        AND fpr.isLiked = true
        AND fpr.isDeleted = false
      """)
  List<PerformanceReview> findLikedPerformanceReviewsByUserId(Long userId);

  List<FavoritePerformanceReview> findByUser_UserIdAndIsLikedTrue(Long userId);

  /** 목록/상세 하트 표시용 */
  @Query("""
      SELECT fpr.performanceReview.performanceReviewId
      FROM FavoritePerformanceReview fpr
      WHERE fpr.user.userId = :userId
        AND fpr.isLiked = true
        AND fpr.isDeleted = false
        AND fpr.performanceReview IS NOT NULL
      """)
  List<Long> findPerformanceReviewIdsByUserId(Long userId);

  void deleteByPerformanceReview_PerformanceReviewId(Long performanceReviewId);

  /** ⭐ 공연 리뷰 soft delete 시 favorite도 soft delete */
  @Modifying
  @Query("""
      UPDATE FavoritePerformanceReview f
      SET f.isDeleted = true,
          f.deletedAt = CURRENT_TIMESTAMP,
          f.isLiked = false
      WHERE f.performanceReview.performanceReviewId = :reviewId
      """)
  void softDeleteByReviewId(Long reviewId);
}
