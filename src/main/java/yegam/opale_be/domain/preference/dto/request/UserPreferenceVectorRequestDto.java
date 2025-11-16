package yegam.opale_be.domain.preference.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 사용자 선호 벡터 생성/갱신 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "UserPreferenceVectorRequest DTO", description = "사용자 선호 벡터 생성/업데이트 요청 DTO")
public class UserPreferenceVectorRequestDto {

  @Schema(description = "임베딩 벡터(JSON 문자열)",
      example = "[0.12, -0.33, 0.88, ... ]",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String embeddingVector;
}
