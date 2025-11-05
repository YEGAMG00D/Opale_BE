package yegam.opale_be.domain.place.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yegam.opale_be.domain.place.entity.Place;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, String> {

  /** ✅ 공연장 검색 */
  @Query("""
      SELECT p FROM Place p
      WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:area IS NULL OR LOWER(p.address) LIKE LOWER(CONCAT('%', :area, '%')))
      """)
  Page<Place> search(@Param("keyword") String keyword, @Param("area") String area, Pageable pageable);

  /** ✅ 임시용 근처 공연장 (테스트용) */
  List<Place> findTop10ByOrderByNameAsc();
}
