package yegam.opale_be.domain.review.place.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yegam.opale_be.domain.review.place.entity.PlaceReview;

import java.util.List;

@Repository
public interface PlaceReviewRepository extends JpaRepository<PlaceReview, Long> {

  /** 공연장별 리뷰 목록 */
  @Query("SELECT r FROM PlaceReview r WHERE r.place.placeId = :placeId AND r.isDeleted = false ORDER BY r.createdAt DESC")
  List<PlaceReview> findAllByPlaceId(String placeId);

  /** 작성한 본인 공연장 리뷰 목록 */
  @Query("SELECT r FROM PlaceReview r WHERE r.user.userId = :userId AND r.isDeleted = false ORDER BY r.createdAt DESC")
  List<PlaceReview> findAllByUserId(Long userId);

  /** 특정 회원의 리뷰 목록 (리뷰 타입 포함) */
  @Query("SELECT r FROM PlaceReview r WHERE r.user.userId = :userId AND r.isDeleted = false AND r.reviewType = :reviewType ORDER BY r.createdAt DESC")
  List<PlaceReview> findAllByUserIdAndType(Long userId, yegam.opale_be.domain.review.common.ReviewType reviewType);

  /** 리뷰들의 평균 평점을 계산하는 쿼리 */
  @Query("SELECT AVG(r.rating) FROM PlaceReview r WHERE r.place.placeId = :placeId AND r.isDeleted = false AND r.rating IS NOT NULL")
  Double calculateAverageRating(@Param("placeId") String placeId);


}
