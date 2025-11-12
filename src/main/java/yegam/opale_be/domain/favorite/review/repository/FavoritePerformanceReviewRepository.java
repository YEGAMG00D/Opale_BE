package yegam.opale_be.domain.favorite.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
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

  /** 마이페이지용: 좋아요한 PerformanceReview 엔티티 목록 */
  @Query("SELECT fpr.performanceReview FROM FavoritePerformanceReview fpr " +
      "WHERE fpr.user.userId = :userId AND fpr.isLiked = true")
  List<PerformanceReview> findLikedPerformanceReviewsByUserId(Long userId);

  /** 목록/상세 하트 표시용: 좋아요한 performanceReviewId 목록 */
  @Query("SELECT fpr.performanceReview.performanceReviewId FROM FavoritePerformanceReview fpr " +
      "WHERE fpr.user.userId = :userId AND fpr.isLiked = true")
  List<Long> findPerformanceReviewIdsByUserId(Long userId);
}
