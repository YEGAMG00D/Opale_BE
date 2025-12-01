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


  /** ✅ 오타 허용 + 부분 검색 + 정확도 순 정렬 */
  @Transactional(readOnly = true)
  public List<PerformanceSearchDocument> search(String keyword) {

    NativeQuery query = NativeQuery.builder()
        .withQuery(q -> q
            .bool(b -> b
                .should(s -> s
                    .matchPhrasePrefix(m -> m
                        .field("title")
                        .query(keyword)
                        .boost(3.0f)   // 가장 중요한 정확 매칭
                    )
                )
                .should(s -> s
                    .prefix(p -> p
                        .field("title")
                        .value(keyword)
                        .boost(2.0f)
                    )
                )
                .should(s -> s
                    .match(m -> m
                        .field("title")
                        .query(keyword)
                        .fuzziness("AUTO")
                        .boost(1.0f)
                    )
                )
            )
        )
        .withMaxResults(20)
        .build();

    SearchHits<PerformanceSearchDocument> hits =
        elasticsearchOperations.search(query, PerformanceSearchDocument.class);

    return hits.getSearchHits()
        .stream()
        .map(SearchHit::getContent)
        .toList();
  }

  /** ✅ 자동완성 (정확도 기반 정렬 완전 적용) */
  @Transactional(readOnly = true)
  public List<PerformanceAutoCompleteResponseDto> autoComplete(String keyword) {

    NativeQuery query = NativeQuery.builder()
        .withQuery(q -> q
            .bool(b -> b
                // 1순위: 정확한 앞단어 일치
                .should(s -> s
                    .matchPhrasePrefix(m -> m
                        .field("title")
                        .query(keyword)
                        .boost(5.0f)
                    )
                )
                // 2순위: prefix
                .should(s -> s
                    .prefix(p -> p
                        .field("title")
                        .value(keyword)
                        .boost(3.0f)
                    )
                )
                // 3순위: 오타 허용
                .should(s -> s
                    .match(m -> m
                        .field("title")
                        .query(keyword)
                        .fuzziness("AUTO")
                        .boost(1.0f)
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
