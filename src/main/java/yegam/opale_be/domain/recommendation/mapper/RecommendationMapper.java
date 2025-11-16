package yegam.opale_be.domain.recommendation.mapper;

import org.springframework.stereotype.Component;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.recommendation.dto.response.RecommendedPerformanceDto;

@Component
public class RecommendationMapper {

  public RecommendedPerformanceDto toRecommendedPerformanceDto(Performance p, Double score) {

    return RecommendedPerformanceDto.builder()
        .performanceId(p.getPerformanceId())
        .title(p.getTitle())
        .genre(p.getGenrenm())
        .poster(p.getPoster())
        .startDate(p.getStartDate() != null ? p.getStartDate().toLocalDate() : null)
        .endDate(p.getEndDate() != null ? p.getEndDate().toLocalDate() : null)
        .rating(p.getRating())
        .score(score)   // null일 수도 있음
        .build();
  }
}
