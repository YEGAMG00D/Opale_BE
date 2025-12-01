package yegam.opale_be.domain.search.performance.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import yegam.opale_be.domain.search.performance.document.PerformanceSearchDocument;

public interface PerformanceSearchRepository
    extends ElasticsearchRepository<PerformanceSearchDocument, String> {
}
