package yegam.opale_be.domain.favorite.performance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import yegam.opale_be.domain.favorite.performance.entity.FavoritePerformance;
import yegam.opale_be.domain.culture.performance.entity.Performance;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritePerformanceRepository extends JpaRepository<FavoritePerformance, Long> {

  Optional<FavoritePerformance> findByUser_UserIdAndPerformance_PerformanceId(Long userId, String performanceId);

  boolean existsByUser_UserIdAndPerformance_PerformanceIdAndIsLikedTrue(Long userId, String performanceId);

  /** 마이페이지용: 좋아요한 Performance 엔티티 목록 */
  @Query("SELECT fp.performance FROM FavoritePerformance fp " +
      "WHERE fp.user.userId = :userId AND fp.isLiked = true")
  List<Performance> findLikedPerformancesByUserId(Long userId);

  /** 목록 페이지 하트 표시용: 좋아요한 performanceId 목록 */
  @Query("SELECT fp.performance.performanceId FROM FavoritePerformance fp " +
      "WHERE fp.user.userId = :userId AND fp.isLiked = true")
  List<String> findLikedPerformanceIdsByUserId(Long userId);
}
