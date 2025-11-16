package yegam.opale_be.domain.analytics.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 사용자 행동 로그 단건 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "UserEventLogResponse DTO", description = "사용자 행동 로그 단일 응답 DTO")
public class UserEventLogResponseDto {

  @Schema(description = "로그 ID", example = "123")
  private Long logId;

  @Schema(description = "사용자 ID", example = "10")
  private Long userId;

  @Schema(description = "이벤트 타입", example = "VIEW")
  private String eventType;

  @Schema(description = "타겟 타입", example = "PERFORMANCE")
  private String targetType;

  @Schema(description = "타겟 ID", example = "PF12345")
  private String targetId;

  @Schema(description = "이벤트 가중치", example = "5")
  private Integer weight;

  @Schema(description = "발생 일시 (ISO-8601 문자열)", example = "2025-01-23T12:33:55")
  private String createdAt;
}
