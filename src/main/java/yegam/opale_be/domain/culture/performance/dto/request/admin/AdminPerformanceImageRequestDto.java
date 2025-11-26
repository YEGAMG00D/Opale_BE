package yegam.opale_be.domain.culture.performance.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import yegam.opale_be.domain.culture.performance.entity.PerformanceImage;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminPerformanceImageRequestDto {

  @NotBlank
  private String imageUrl;

  @NotNull
  private PerformanceImage.ImageType imageType;

  private String sourceUrl;
}
