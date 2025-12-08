package yegam.opale_be.domain.search.performance.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.core.suggest.Completion;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "performances", createIndex = false)
public class PerformanceSearchDocument {

  @Id
  private String performanceId;

  private String title;
  private String genrenm;
  private String placeName;

  // ✅ 반드시 Long (epoch millis)
  private Long startDate;
  private Long endDate;

  private String aiSummary;
  private List<String> aiKeywords;

  // ✅ 자동완성용
  @CompletionField
  private Completion titleSuggest;
}
