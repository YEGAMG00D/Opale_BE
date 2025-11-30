package yegam.opale_be.domain.banner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yegam.opale_be.domain.banner.entity.MainPerformanceBanner;

import java.util.List;

@Repository
public interface MainPerformanceBannerRepository extends JpaRepository<MainPerformanceBanner, Long> {

  /** ✅ 메인 노출용 공연 배너 조회 (활성 + 정렬순) */
  List<MainPerformanceBanner> findByIsActiveTrueOrderByDisplayOrderAsc();
}
