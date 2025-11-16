package yegam.opale_be.domain.preference.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 사용자 선호 벡터 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "UserPreferenceVectorResponse DTO", description = "사용자 선호 벡터 응답 DTO")
public class UserPreferenceVectorResponseDto {

  @Schema(description = "사용자 ID", example = "10")
  private Long userId;

  @Schema(description = "임베딩 벡터(JSON 문자열)", example = "[0.15, -0.20, 0.93...]")
  private String embeddingVector;

  @Schema(description = "업데이트 시간", example = "2025-02-05T16:11:40")
  private String updatedAt;
}
