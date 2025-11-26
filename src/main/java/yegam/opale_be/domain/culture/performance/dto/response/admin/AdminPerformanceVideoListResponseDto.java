package yegam.opale_be.domain.culture.performance.dto.response.admin;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminPerformanceVideoListResponseDto {

  private List<AdminPerformanceVideoResponseDto> videos;
}
