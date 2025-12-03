package yegam.opale_be.domain.report.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import yegam.opale_be.domain.report.entity.ReportTargetType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "ReportCreateRequest DTO", description = "신고 생성 요청 DTO")
public class ReportCreateRequestDto {

  @Schema(description = "신고 대상 유저 ID", example = "7")
  @NotNull(message = "신고 대상 유저 ID는 필수입니다.")
  private Long targetUserId;

  @Schema(description = "신고 대상 타입 (CHAT_MESSAGE / PERFORMANCE_REVIEW / PLACE_REVIEW / USER)", example = "CHAT_MESSAGE")
  @NotNull(message = "신고 대상 타입은 필수입니다.")
  private ReportTargetType targetType;

  @Schema(description = "신고 대상 ID (채팅 메시지 ID 등)", example = "123")
  @NotNull(message = "신고 대상 ID는 필수입니다.")
  private Long targetId;

  @Schema(description = "신고 사유 (간단 요약)", example = "욕설 및 비방")
  @NotBlank(message = "신고 사유는 필수입니다.")
  private String reason;

  @Schema(description = "신고 상세 내용", example = "지속적으로 욕설과 인신공격을 하고 있습니다.")
  private String detail;
}
