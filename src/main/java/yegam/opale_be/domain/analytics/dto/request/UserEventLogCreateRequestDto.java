package yegam.opale_be.domain.analytics.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 사용자 행동 로그 생성 요청 DTO
 * - 조회, 관심, 예매, 리뷰 작성 등 이벤트 발생 시 호출
 * - userId는 DTO가 아니라 @AuthenticationPrincipal 로 주입됨
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "UserEventLogCreateRequest DTO", description = "사용자 행동 로그 생성 요청 DTO")
public class UserEventLogCreateRequestDto {

  @Schema(description = "이벤트 타입 (VIEW, FAVORITE, REVIEW_WRITE, BOOKED)",
      example = "VIEW", requiredMode = Schema.RequiredMode.REQUIRED)
  private String eventType;

  @Schema(description = "타겟 타입 (PERFORMANCE, PLACE, REVIEW)",
      example = "PERFORMANCE", requiredMode = Schema.RequiredMode.REQUIRED)
  private String targetType;

  @Schema(description = "대상 ID (공연ID / 장소ID / 리뷰ID 등)",
      example = "PF12345", requiredMode = Schema.RequiredMode.REQUIRED)
  private String targetId;

  @Schema(description = "가중치(옵션) — 미전달 시 이벤트 타입에 따라 자동 설정",
      example = "5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private Integer weight;
}
