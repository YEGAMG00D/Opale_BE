package yegam.opale_be.domain.review.performance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yegam.opale_be.domain.review.common.ReviewType;
import yegam.opale_be.domain.review.performance.entity.PerformanceReview;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {

  /** ⭐ Soft Delete 단건 조회 전용 */
  Optional<PerformanceReview> findByPerformanceReviewIdAndIsDeletedFalse(Long reviewId);

  /** 공연별 리뷰 목록 */
  @Query("""
      SELECT r FROM PerformanceReview r
      WHERE r.performance.performanceId = :performanceId
        AND r.isDeleted = false
      ORDER BY r.createdAt DESC
  """)
  List<PerformanceReview> findAllByPerformanceId(@Param("performanceId") String performanceId);

  /** 공연별 + 리뷰타입별 리뷰 목록 */
  @Query("""
      SELECT r FROM PerformanceReview r
      WHERE r.performance.performanceId = :performanceId
        AND r.reviewType = :reviewType
        AND r.isDeleted = false
      ORDER BY r.createdAt DESC
  """)
  List<PerformanceReview> findAllByPerformanceIdAndType(
      @Param("performanceId") String performanceId,
      @Param("reviewType") ReviewType reviewType
  );

  /** 작성한 본인 리뷰 목록 */
  @Query("""
      SELECT r FROM PerformanceReview r
      WHERE r.user.userId = :userId
        AND r.isDeleted = false
      ORDER BY r.createdAt DESC
  """)
  List<PerformanceReview> findAllByUserId(@Param("userId") Long userId);

  /** 작성한 본인 + 리뷰타입별 목록 */
  @Query("""
      SELECT r FROM PerformanceReview r
      WHERE r.user.userId = :userId
        AND r.reviewType = :reviewType
        AND r.isDeleted = false
      ORDER BY r.createdAt DESC
  """)
  List<PerformanceReview> findAllByUserIdAndType(
      @Param("userId") Long userId,
      @Param("reviewType") ReviewType reviewType
  );

  /** 공연 평균 평점 */
  @Query("""
      SELECT AVG(r.rating)
      FROM PerformanceReview r
      WHERE r.performance.performanceId = :performanceId
        AND r.isDeleted = false
        AND r.rating IS NOT NULL
  """)
  Double calculateAverageRating(@Param("performanceId") String performanceId);

  /** 리뷰 개수 */
  @Query("""
    SELECT COUNT(r)
    FROM PerformanceReview r
    WHERE r.performance.performanceId = :performanceId
      AND r.reviewType = :type
      AND r.isDeleted = false
  """)
  Long countByPerformanceIdAndType(String performanceId, ReviewType type);

  Optional<PerformanceReview> findByTicket_TicketId(Long ticketId);

  /** ⭐ 리뷰 물리 삭제는 사용하지 않음 */
  void deleteByTicket_TicketId(Long ticketId);

  /** ⭐ Soft Delete */
  @Modifying
  @Query("""
      UPDATE PerformanceReview r
      SET r.isDeleted = true,
          r.deletedAt = CURRENT_TIMESTAMP
      WHERE r.performanceReviewId = :reviewId
  """)
  void softDelete(@Param("reviewId") Long reviewId);
}
