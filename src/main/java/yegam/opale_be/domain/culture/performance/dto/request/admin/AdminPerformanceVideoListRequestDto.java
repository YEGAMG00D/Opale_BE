package yegam.opale_be.domain.culture.performance.dto.request.admin;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminPerformanceVideoListRequestDto {

  private List<AdminPerformanceVideoRequestDto> videos;
}
