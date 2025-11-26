package yegam.opale_be.domain.culture.performance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yegam.opale_be.domain.culture.performance.entity.PerformanceVideo;

import java.util.List;

@Repository
public interface PerformanceVideoRepository extends JpaRepository<PerformanceVideo, Long> {

  List<PerformanceVideo> findByPerformance_PerformanceId(String performanceId);

  void deleteByPerformance_PerformanceId(String performanceId); // 전체 Replace 용
}
