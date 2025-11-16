package yegam.opale_be.domain.analytics.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 사용자 행동 로그 검색 조건 DTO
 * - GET /api/logs 의 쿼리 파라미터와 매핑
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "UserEventLogSearchRequest DTO", description = "사용자 행동 로그 검색 조건 DTO")
public class UserEventLogSearchRequestDto {

  @Schema(description = "사용자 ID (선택)", example = "10")
  private Long userId;

  @Schema(description = "이벤트 타입 (선택, VIEW/FAVORITE/BOOKED/REVIEW_WRITE 등)", example = "VIEW")
  private String eventType;

  @Schema(description = "타겟 타입 (선택, PERFORMANCE/PLACE/REVIEW)", example = "PERFORMANCE")
  private String targetType;

  @Schema(description = "타겟 ID (선택, 공연/장소/리뷰 ID)", example = "PF12345")
  private String targetId;

  @Schema(description = "시작 날짜 (yyyy-MM-dd, 선택)", example = "2025-01-01")
  private String startDate;

  @Schema(description = "종료 날짜 (yyyy-MM-dd, 선택)", example = "2025-01-31")
  private String endDate;

  @Schema(description = "페이지 번호 (1부터 시작)", example = "1")
  private Integer page;

  @Schema(description = "페이지당 로그 개수", example = "20")
  private Integer size;
}
