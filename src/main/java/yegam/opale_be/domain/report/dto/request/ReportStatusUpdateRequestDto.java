package yegam.opale_be.domain.report.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import yegam.opale_be.domain.report.entity.ReportStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "ReportStatusUpdateRequest DTO", description = "신고 처리 상태 변경 요청 DTO")
public class ReportStatusUpdateRequestDto {

  @Schema(description = "변경할 신고 상태 (APPROVED / REJECTED)", example = "APPROVED")
  @NotNull(message = "신고 상태는 필수입니다.")
  private ReportStatus status;

  @Schema(description = "관리자 메모", example = "1회 경고 처리, 재발 시 이용 제한 예정")
  private String adminMemo;
}
