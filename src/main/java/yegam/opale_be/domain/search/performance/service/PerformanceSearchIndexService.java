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
import yegam.opale_be.domain.search.performance.mapper.PerformanceAutoCompleteMapper;
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

                .startDate(p.getStartDate() != null ? p.getStartDate().getTime() : null)
                .endDate(p.getEndDate() != null ? p.getEndDate().getTime() : null)

                .aiSummary(p.getAiSummary())

                .aiKeywords(
                    p.getAiKeywords() == null
                        ? List.of()
                        : Arrays.stream(p.getAiKeywords().split(","))
                            .map(String::trim)
                            .toList()
                )

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
                .should(s -> s.matchPhrasePrefix(m -> m.field("title").query(keyword).boost(3.0f)))
                .should(s -> s.prefix(p -> p.field("title").value(keyword).boost(2.0f)))
                .should(s -> s.match(m -> m.field("title").query(keyword).fuzziness("AUTO").boost(1.0f)))
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


  /** ✅ 자동완성 (MySQL에서 추가 필드까지 끌어오기) */
  @Transactional(readOnly = true)
  public List<PerformanceAutoCompleteResponseDto> autoComplete(String keyword) {

    NativeQuery query = NativeQuery.builder()
        .withQuery(q -> q
            .bool(b -> b
                .should(s -> s.matchPhrasePrefix(m -> m.field("title").query(keyword).boost(5.0f)))
                .should(s -> s.prefix(p -> p.field("title").value(keyword).boost(3.0f)))
                .should(s -> s.match(m -> m.field("title").query(keyword).fuzziness("AUTO").boost(1.0f)))
            )
        )
        .withMaxResults(10)
        .build();

    SearchHits<PerformanceSearchDocument> hits =
        elasticsearchOperations.search(query, PerformanceSearchDocument.class);

    return hits.getSearchHits()
        .stream()
        .map(hit -> {
          String id = hit.getContent().getPerformanceId();

          return performanceRepository.findById(id)
              .map(PerformanceAutoCompleteMapper::toDto)
              .orElseGet(() -> new PerformanceAutoCompleteResponseDto(
                  id,
                  hit.getContent().getTitle(),
                  null,
                  null,
                  null
              ));
        })
        .toList();
  }


  /** ✅ 정확도 순 performanceId 리스트 반환 (ES 전용) */
  @Transactional(readOnly = true)
  public List<String> searchIdsByAccuracy(String keyword) {

    NativeQuery query = NativeQuery.builder()
        .withQuery(q -> q
            .bool(b -> b
                .should(s -> s.matchPhrasePrefix(m -> m.field("title").query(keyword).boost(5.0f)))
                .should(s -> s.prefix(p -> p.field("title").value(keyword).boost(3.0f)))
                .should(s -> s.match(m -> m.field("title").query(keyword).fuzziness("AUTO").boost(1.0f)))
            )
        )
        .withMaxResults(100)
        .build();

    SearchHits<PerformanceSearchDocument> hits =
        elasticsearchOperations.search(query, PerformanceSearchDocument.class);

    return hits.getSearchHits()
        .stream()
        .map(hit -> hit.getContent().getPerformanceId())
        .toList();
  }

}
