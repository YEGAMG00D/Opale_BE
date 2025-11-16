package yegam.opale_be.domain.culture.performance.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.culture.performance.entity.Performance;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, String> {

  /** ⭐ 공연 조회수 증가 */
  @Modifying
  @Transactional
  @Query("UPDATE Performance p SET p.viewCount = p.viewCount + 1 WHERE p.performanceId = :performanceId")
  void incrementViewCount(@Param("performanceId") String performanceId);

  /** 공연 검색 */
  @Query("""
      SELECT p FROM Performance p
      WHERE
        (:genre IS NULL OR :genre = '' OR p.genrenm = :genre)
        AND (
          :keyword IS NULL OR :keyword = '' OR
          LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
          LOWER(p.placeName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        AND (:area IS NULL OR :area = '' OR LOWER(p.area) LIKE LOWER(CONCAT('%', :area, '%')))
      ORDER BY
        CASE WHEN :sortType = '인기' THEN p.updatedate END DESC,
        CASE WHEN :sortType = '최신' THEN p.updatedate END DESC
      """)
  Page<Performance> search(
      @Param("genre") String genre,
      @Param("keyword") String keyword,
      @Param("area") String area,
      @Param("sortType") String sortType,
      Pageable pageable
  );

  @Query("""
    SELECT p FROM Performance p
    WHERE 
      (:type = 'start' AND p.startDate = :today)
      OR (:type = 'end' AND p.endDate = :today)
      OR (:type = 'all' AND (p.startDate = :today OR p.endDate = :today))
  """)
  List<Performance> findPerformancesByTypeAndDate(@Param("type") String type, @Param("today") LocalDate today);

  List<Performance> findTop10ByOrderByUpdatedateDesc();

  @Query("""
      SELECT DISTINCT p FROM Performance p
      LEFT JOIN FETCH p.performanceRelations
      WHERE p.performanceId = :performanceId
  """)
  Optional<Performance> findByIdWithRelations(@Param("performanceId") String performanceId);

  @Query("""
      SELECT DISTINCT p FROM Performance p
      LEFT JOIN FETCH p.performanceVideos
      WHERE p.performanceId = :performanceId
  """)
  Optional<Performance> findByIdWithVideos(@Param("performanceId") String performanceId);

  @Query("""
      SELECT DISTINCT p FROM Performance p
      LEFT JOIN FETCH p.performanceImages
      WHERE p.performanceId = :performanceId
  """)
  Optional<Performance> findByIdWithImages(@Param("performanceId") String performanceId);

  @Query("""
      SELECT DISTINCT p FROM Performance p
      LEFT JOIN FETCH p.performanceInfoImages
      WHERE p.performanceId = :performanceId
  """)
  Optional<Performance> findByIdWithInfoImages(@Param("performanceId") String performanceId);

  @Query("""
      SELECT p FROM Performance p
      WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        AND (:date IS NULL OR :date BETWEEN p.startDate AND p.endDate)
      ORDER BY LENGTH(p.title) ASC
      """)
  Optional<Performance> findFirstByTitleAndDateRange(
      @Param("keyword") String keyword,
      @Param("date") LocalDate date
  );

  List<Performance> findByPerformanceIdIn(List<String> performanceIds);

  @Query("""
      SELECT p FROM Performance p
      WHERE (:genre IS NULL OR :genre = '' OR p.genrenm = :genre)
      ORDER BY p.rating DESC NULLS LAST, p.updatedate DESC
      """)
  List<Performance> findPopularByGenre(
      @Param("genre") String genre,
      Pageable pageable
  );

  @Query("""
      SELECT p FROM Performance p
      WHERE (:genre IS NULL OR :genre = '' OR p.genrenm = :genre)
      ORDER BY p.updatedate DESC
      """)
  List<Performance> findLatestByGenre(
      @Param("genre") String genre,
      Pageable pageable
  );

  @Query("""
      SELECT p FROM Performance p
      ORDER BY p.rating DESC NULLS LAST, p.updatedate DESC
      """)
  List<Performance> findPopularPerformances(Pageable pageable);

  @Query("""
      SELECT p FROM Performance p
      ORDER BY p.updatedate DESC
      """)
  List<Performance> findLatestPerformances(Pageable pageable);

}
