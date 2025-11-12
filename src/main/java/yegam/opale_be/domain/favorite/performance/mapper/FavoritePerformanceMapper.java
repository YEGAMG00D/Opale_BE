package yegam.opale_be.domain.favorite.performance.mapper;

import org.springframework.stereotype.Component;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.favorite.performance.dto.response.FavoritePerformanceResponseDto;
import yegam.opale_be.domain.favorite.performance.entity.FavoritePerformance;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 공연 관심 Mapper
 * - Entity ↔ DTO 변환 전담
 */
@Component
public class FavoritePerformanceMapper {

  /** 공연 Entity → 관심 Response DTO */
  public FavoritePerformanceResponseDto toResponseDto(Performance entity, boolean isLiked) {
    if (entity == null) return null;

    // ✅ aiKeywords(COMMA 구분 문자열) → List<String> 변환
    List<String> keywords = splitKeywords(entity.getAiKeywords());

    // ✅ java.sql.Date → LocalDate 변환
    LocalDate startDate = entity.getStartDate() != null ? entity.getStartDate().toLocalDate() : null;
    LocalDate endDate = entity.getEndDate() != null ? entity.getEndDate().toLocalDate() : null;

    return FavoritePerformanceResponseDto.builder()
        .performanceId(entity.getPerformanceId())
        .title(entity.getTitle())
        .genrenm(entity.getGenrenm())
        .poster(entity.getPoster())
        .placeName(entity.getPlaceName())
        .startDate(startDate)
        .endDate(endDate)
        .rating(entity.getRating() != null ? entity.getRating() : 0.0)
        .keywords(keywords)
        .aiSummary(entity.getAiSummary())
        .isLiked(isLiked)
        .build();
  }

  /** 공연 Entity List → 관심 DTO List */
  public List<FavoritePerformanceResponseDto> toResponseDtoList(List<Performance> performances) {
    if (performances == null) return Collections.emptyList();
    return performances.stream()
        .map(p -> toResponseDto(p, true)) // 이미 관심 목록이므로 isLiked=true
        .collect(Collectors.toList());
  }

  /** FavoritePerformance Entity → Response DTO */
  public FavoritePerformanceResponseDto toResponseDto(FavoritePerformance favorite) {
    if (favorite == null || favorite.getPerformance() == null) return null;
    return toResponseDto(favorite.getPerformance(), favorite.getIsLiked());
  }

  /** ✅ 키워드 문자열 분리 함수 (PerformanceMapper와 동일) */
  private List<String> splitKeywords(String keywords) {
    if (keywords == null || keywords.isBlank()) return List.of();
    return Arrays.stream(keywords.split(","))
        .map(String::trim)
        .filter(k -> !k.isEmpty())
        .collect(Collectors.toList());
  }
}
