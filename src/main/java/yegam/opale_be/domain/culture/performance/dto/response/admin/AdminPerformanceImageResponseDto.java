package yegam.opale_be.domain.culture.performance.dto.response.admin;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminPerformanceImageResponseDto {

  private Long performanceImageId;
  private String imageUrl;
  private String imageType;
  private String sourceUrl;
}
