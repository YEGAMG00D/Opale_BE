package yegam.opale_be.domain.analytics.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * 사용자 행동 로그 목록 + 페이징 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "UserEventLogListResponse DTO", description = "사용자 행동 로그 목록 및 페이지 정보 응답 DTO")
public class UserEventLogListResponseDto {

  @Schema(description = "총 로그 개수", example = "1523")
  private long totalCount;

  @Schema(description = "현재 페이지 (1부터 시작)", example = "1")
  private int currentPage;

  @Schema(description = "페이지당 로그 수", example = "20")
  private int pageSize;

  @Schema(description = "전체 페이지 수", example = "77")
  private int totalPages;

  @Schema(description = "다음 페이지 존재 여부", example = "true")
  private boolean hasNext;

  @Schema(description = "이전 페이지 존재 여부", example = "false")
  private boolean hasPrev;

  @Schema(description = "로그 목록 데이터")
  private List<UserEventLogResponseDto> logs;
}
