package yegam.opale_be.domain.discount.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yegam.opale_be.domain.discount.entity.Discount;
import yegam.opale_be.domain.discount.entity.DiscountSiteType;

import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, Long> {

  /**
   * 사이트별 최신 batch_id 조회 (createdAt 최신값 기준)
   */
  @Query("""
        SELECT d.batchId
        FROM Discount d
        WHERE d.site = :site
        GROUP BY d.batchId
        ORDER BY MAX(d.createdAt) DESC
        LIMIT 1
    """)
  String findLatestBatchIdBySite(@Param("site") DiscountSiteType site);

  /**
   * 사이트 + batch 기준 데이터 조회
   */
  List<Discount> findBySiteAndBatchIdOrderByCreatedAtDesc(
      DiscountSiteType site,
      String batchId
  );
}
