package yegam.opale_be.domain.report.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import yegam.opale_be.domain.report.entity.Report;
import yegam.opale_be.domain.report.entity.ReportStatus;

public interface ReportRepository extends JpaRepository<Report, Long> {

  Page<Report> findByStatus(ReportStatus status, Pageable pageable);
}
