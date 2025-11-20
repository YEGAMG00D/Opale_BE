package yegam.opale_be.domain.review.place.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yegam.opale_be.domain.review.common.ReviewType;
import yegam.opale_be.domain.review.place.entity.PlaceReview;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceReviewRepository extends JpaRepository<PlaceReview, Long> {

  /** 공연장별 리뷰 목록 */
  @Query("""
      SELECT r FROM PlaceReview r
      WHERE r.place.placeId = :placeId
        AND r.isDeleted = false
      ORDER BY r.createdAt DESC
  """)
  List<PlaceReview> findAllByPlaceId(@Param("placeId") String placeId);

  /** 공연장별 + 리뷰타입별 리뷰 목록 */
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

  /** 작성한 본인 공연장 리뷰 목록 */
  @Query("""
      SELECT r FROM PlaceReview r
      WHERE r.user.userId = :userId
        AND r.isDeleted = false
      ORDER BY r.createdAt DESC
  """)
  List<PlaceReview> findAllByUserId(@Param("userId") Long userId);

  /** 작성한 본인 + 리뷰타입별 목록 */
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

  /** 공연장 평균 평점 */
  @Query("""
      SELECT AVG(r.rating)
      FROM PlaceReview r
      WHERE r.place.placeId = :placeId
        AND r.isDeleted = false
        AND r.rating IS NOT NULL
  """)
  Double calculateAverageRating(@Param("placeId") String placeId);

  /** 공연장 리뷰 개수 (특정 타입만) */
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

  /** 공연장 평균 평점 (특정 타입만) */
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


}
