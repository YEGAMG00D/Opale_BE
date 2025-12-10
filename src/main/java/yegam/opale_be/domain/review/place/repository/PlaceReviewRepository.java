package yegam.opale_be.domain.review.place.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yegam.opale_be.domain.review.common.ReviewType;
import yegam.opale_be.domain.review.place.entity.PlaceReview;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceReviewRepository extends JpaRepository<PlaceReview, Long> {

  /** ⭐ Soft Delete 제외 + 단건 조회 */
  Optional<PlaceReview> findByPlaceReviewIdAndIsDeletedFalse(Long reviewId);

  /** 공연장별 리뷰 목록 */
  @Query("""
      SELECT r FROM PlaceReview r
      WHERE r.place.placeId = :placeId
        AND r.isDeleted = false
      ORDER BY r.createdAt DESC
  """)
  List<PlaceReview> findAllByPlaceId(@Param("placeId") String placeId);

  /** 공연장 + 타입 필터링 */
  @Query("""
      SELECT r FROM PlaceReview r
      WHERE r.place.placeId = :placeId
        AND r.reviewType = :reviewType
        AND r.isDeleted = false
      ORDER BY r.createdAt DESC
  """)
  List<PlaceReview> findAllByPlaceIdAndType(
      @Param("placeId") String placeId,
      @Param("reviewType") ReviewType reviewType
  );

  /** 유저별 리뷰 목록 */
  @Query("""
      SELECT r FROM PlaceReview r
      WHERE r.user.userId = :userId
        AND r.isDeleted = false
      ORDER BY r.createdAt DESC
  """)
  List<PlaceReview> findAllByUserId(@Param("userId") Long userId);

  @Query("""
      SELECT r FROM PlaceReview r
      WHERE r.user.userId = :userId
        AND r.reviewType = :reviewType
        AND r.isDeleted = false
      ORDER BY r.createdAt DESC
  """)
  List<PlaceReview> findAllByUserIdAndType(
      @Param("userId") Long userId,
      @Param("reviewType") ReviewType reviewType
  );

  /** 전체 평균 평점 */
  @Query("""
      SELECT AVG(r.rating)
      FROM PlaceReview r
      WHERE r.place.placeId = :placeId
        AND r.isDeleted = false
        AND r.rating IS NOT NULL
  """)
  Double calculateAverageRating(@Param("placeId") String placeId);

  /** 타입별 리뷰 개수 */
  @Query("""
      SELECT COUNT(r)
      FROM PlaceReview r
      WHERE r.place.placeId = :placeId
        AND r.reviewType = :reviewType
        AND r.isDeleted = false
  """)
  Long countByPlaceIdAndType(
      @Param("placeId") String placeId,
      @Param("reviewType") ReviewType reviewType
  );

  /** 타입별 평균 평점 */
  @Query("""
      SELECT AVG(r.rating)
      FROM PlaceReview r
      WHERE r.place.placeId = :placeId
        AND r.reviewType = :reviewType
        AND r.isDeleted = false
        AND r.rating IS NOT NULL
  """)
  Double avgRatingByPlaceIdAndType(
      @Param("placeId") String placeId,
      @Param("reviewType") ReviewType reviewType
  );

  Optional<PlaceReview> findByTicket_TicketId(Long ticketId);

  void deleteByTicket_TicketId(Long ticketId);

  /** Soft Delete */
  @Modifying
  @Query("""
      UPDATE PlaceReview r
      SET r.isDeleted = true,
          r.deletedAt = CURRENT_TIMESTAMP
      WHERE r.placeReviewId = :reviewId
  """)
  void softDelete(@Param("reviewId") Long reviewId);
}
