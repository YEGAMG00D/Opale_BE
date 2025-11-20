package yegam.opale_be.domain.preference.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "UserPreferenceVectorResponse DTO", description = "사용자 선호 벡터 응답 DTO")
public class UserPreferenceVectorResponseDto {

  @Schema(description = "선호 벡터 ID(=유저 ID 동일)", example = "10")
  private Long userPreferenceVectorId;

  @Schema(description = "임베딩 벡터(JSON 문자열)")
  private String embeddingVector;

  @Schema(description = "업데이트 시간")
  private String updatedAt;
}
