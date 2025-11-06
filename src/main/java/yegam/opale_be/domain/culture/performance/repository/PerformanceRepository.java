package yegam.opale_be.domain.culture.performance.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yegam.opale_be.domain.culture.performance.entity.Performance;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, String> {

  /** 통합 검색: 장르 + 키워드 + 지역 (AND 조건) + 정렬 + 페이징 */
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

  /** 오늘 개막/종료 공연 조회 */
  @Query("""
    SELECT p FROM Performance p
    WHERE 
      (:type = 'start' AND p.startDate = :today)
      OR (:type = 'end' AND p.endDate = :today)
      OR (:type = 'all' AND (p.startDate = :today OR p.endDate = :today))
  """)
  List<Performance> findPerformancesByTypeAndDate(@Param("type") String type, @Param("today") LocalDate today);

  /** 최신순 Top10 (임시 인기 대용) */
  List<Performance> findTop10ByOrderByUpdatedateDesc();

  // ---------------------------------------------------------------------
  // 상세 페이지 용
  // ---------------------------------------------------------------------

  /** 예매처 전용 Fetch Join */
  @Query("""
      SELECT DISTINCT p FROM Performance p
      LEFT JOIN FETCH p.performanceRelations
      WHERE p.performanceId = :performanceId
  """)
  Optional<Performance> findByIdWithRelations(@Param("performanceId") String performanceId);

  /** 영상 전용 Fetch Join */
  @Query("""
      SELECT DISTINCT p FROM Performance p
      LEFT JOIN FETCH p.performanceVideos
      WHERE p.performanceId = :performanceId
  """)
  Optional<Performance> findByIdWithVideos(@Param("performanceId") String performanceId);

  /** 수집 이미지 전용 Fetch Join */
  @Query("""
      SELECT DISTINCT p FROM Performance p
      LEFT JOIN FETCH p.performanceImages
      WHERE p.performanceId = :performanceId
  """)
  Optional<Performance> findByIdWithImages(@Param("performanceId") String performanceId);

  /** 소개 이미지 전용 Fetch Join */
  @Query("""
      SELECT DISTINCT p FROM Performance p
      LEFT JOIN FETCH p.performanceInfoImages
      WHERE p.performanceId = :performanceId
  """)
  Optional<Performance> findByIdWithInfoImages(@Param("performanceId") String performanceId);
}
