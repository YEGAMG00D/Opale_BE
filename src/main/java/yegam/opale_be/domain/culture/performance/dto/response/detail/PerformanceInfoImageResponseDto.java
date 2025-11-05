package yegam.opale_be.domain.culture.performance.dto.response.detail;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 공연 소개 이미지 단일 항목 DTO
 * - 공연 한 개의 이미지 데이터 (URL + 순서)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PerformanceInfoImage DTO", description = "공연 소개 이미지 단일 항목 DTO")
public class PerformanceInfoImageResponseDto {

  @Schema(description = "이미지 URL", example = "http://www.kopis.or.kr/upload/pfmIntroImage/2025/12345.jpg")
  private String imageUrl;

  @Schema(description = "이미지 순서", example = "1")
  private Integer orderIndex;
}
