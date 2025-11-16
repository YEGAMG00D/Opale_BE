package yegam.opale_be.domain.place.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.place.entity.Place;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, String> {

  /** ⭐ 공연장 조회수 증가 */
  @Modifying
  @Transactional
  @Query("UPDATE Place p SET p.viewCount = p.viewCount + 1 WHERE p.placeId = :placeId")
  void incrementViewCount(@Param("placeId") String placeId);

  /** 공연장 검색 */
  @Query("""
      SELECT p FROM Place p
      WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:area IS NULL OR LOWER(p.address) LIKE LOWER(CONCAT('%', :area, '%')))
      """)
  Page<Place> search(@Param("keyword") String keyword, @Param("area") String area, Pageable pageable);

  /** ⭐ 인기 공연장 (조회수 + 평점 + 공연장 이름) */
  @Query("""
      SELECT p FROM Place p
      ORDER BY p.viewCount DESC, p.rating DESC NULLS LAST, p.name ASC
      """)
  List<Place> findPopularPlaces(Pageable pageable);

  /** 임시용 근처 공연장 (테스트용) */
  List<Place> findTop10ByOrderByNameAsc();

  /** 좌표 기반 근처 공연장 조회 */
  @Query(value = """
      SELECT 
        p.place_id AS placeId,
        p.name AS name,
        p.address AS address,
        p.la AS latitude,
        p.lo AS longitude,
        ST_Distance_Sphere(point(p.lo, p.la), point(:longitude, :latitude)) AS distance
      FROM places p
      WHERE ST_Distance_Sphere(point(p.lo, p.la), point(:longitude, :latitude)) <= :radius
      ORDER BY distance ASC
      """, nativeQuery = true)
  List<Object[]> findNearbyPlacesWithDistance(
      @Param("latitude") double latitude,
      @Param("longitude") double longitude,
      @Param("radius") int radius
  );

  @Query("""
      SELECT p FROM Place p
      WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
      ORDER BY LENGTH(p.name) ASC
      """)
  Optional<Place> findFirstByNameContainingIgnoreCase(@Param("keyword") String keyword);
}
