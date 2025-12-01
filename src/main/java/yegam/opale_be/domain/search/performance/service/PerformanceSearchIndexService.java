package yegam.opale_be.domain.search.performance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;

import org.springframework.data.elasticsearch.core.suggest.Completion;

import yegam.opale_be.domain.culture.performance.repository.PerformanceRepository;
import yegam.opale_be.domain.search.performance.document.PerformanceSearchDocument;
import yegam.opale_be.domain.search.performance.dto.PerformanceAutoCompleteResponseDto;
import yegam.opale_be.domain.search.performance.repository.PerformanceSearchRepository;

import java.util.Arrays;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PerformanceSearchIndexService {

  private final PerformanceRepository performanceRepository;
  private final PerformanceSearchRepository searchRepository;
  private final ElasticsearchOperations elasticsearchOperations;

  /** ✅ MySQL → Elasticsearch 전체 색인 */
  @Transactional
  public void syncAllToElasticsearch() {

    List<PerformanceSearchDocument> docs =
        performanceRepository.findAll().stream()
            .map(p -> PerformanceSearchDocument.builder()
                .performanceId(p.getPerformanceId())
                .title(p.getTitle())
                .genrenm(p.getGenrenm())
                .placeName(p.getPlaceName())

                // ✅ java.sql.Date 안전 변환 (toInstant 절대 사용 금지)
                .startDate(
                    p.getStartDate() != null
                        ? p.getStartDate().getTime()
                        : null
                )

                .endDate(
                    p.getEndDate() != null
                        ? p.getEndDate().getTime()
                        : null
                )

                .aiSummary(p.getAiSummary())

                .aiKeywords(
                    p.getAiKeywords() == null
                        ? List.of()
                        : Arrays.stream(p.getAiKeywords().split(","))
                            .map(String::trim)
                            .toList()
                )

                // ✅ 자동완성용 (Completion은 builder 없음 → 생성자 사용)
                .titleSuggest(new Completion(List.of(p.getTitle())))

                .build())
            .toList();

    searchRepository.saveAll(docs);
  }

  /** ✅ 오타 허용 + 부분 검색 (fuzzy + multi match) */
  @Transactional(readOnly = true)
  public List<PerformanceSearchDocument> search(String keyword) {

    NativeQuery query = NativeQuery.builder()
        .withQuery(q -> q
            .multiMatch(m -> m
                .query(keyword)
                .fields("title", "aiSummary", "aiKeywords")
                .fuzziness("AUTO")
            )
        )
        .build();

    SearchHits<PerformanceSearchDocument> hits =
        elasticsearchOperations.search(query, PerformanceSearchDocument.class);

    return hits.getSearchHits()
        .stream()
        .map(SearchHit::getContent)
        .toList();
  }


  /** ✅ 자동완성 (title prefix 기반 - Suggester 미사용, 네 버전 호환) */
  @Transactional(readOnly = true)
  public List<PerformanceAutoCompleteResponseDto> autoComplete(String keyword) {

    NativeQuery query = NativeQuery.builder()
        .withQuery(q -> q
            .bool(b -> b
                .should(s -> s
                    .prefix(p -> p
                        .field("title")
                        .value(keyword)
                    )
                )
                .should(s -> s
                    .multiMatch(m -> m
                        .query(keyword)
                        .fields("title")
                        .fuzziness("AUTO")
                    )
                )
            )
        )
        .withMaxResults(10)
        .build();

    SearchHits<PerformanceSearchDocument> hits =
        elasticsearchOperations.search(query, PerformanceSearchDocument.class);

    return hits.getSearchHits()
        .stream()
        .map(hit -> new PerformanceAutoCompleteResponseDto(
            hit.getContent().getPerformanceId(),
            hit.getContent().getTitle()
        ))
        .toList();
  }





}
