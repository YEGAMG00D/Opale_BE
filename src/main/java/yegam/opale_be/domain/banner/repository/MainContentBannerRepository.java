package yegam.opale_be.domain.banner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yegam.opale_be.domain.banner.entity.MainContentBanner;

import java.util.List;

@Repository
public interface MainContentBannerRepository extends JpaRepository<MainContentBanner, Long> {

  /** ✅ 메인 노출용 콘텐츠 배너 조회 (활성 + 정렬순) */
  List<MainContentBanner> findByIsActiveTrueOrderByDisplayOrderAsc();
}
