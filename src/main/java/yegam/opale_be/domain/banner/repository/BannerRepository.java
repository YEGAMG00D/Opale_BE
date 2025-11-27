package yegam.opale_be.domain.banner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yegam.opale_be.domain.banner.entity.MainBanner;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<MainBanner, Long> {

  /** 메인 노출용 배너 조회 (활성 + 정렬순) */
  List<MainBanner> findByIsActiveTrueOrderByDisplayOrderAsc();
}
