package yegam.opale_be.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "AdminUserListResponse DTO", description = "관리자용 전체 회원 목록 응답 DTO")
public class AdminUserListResponseDto {

  @Schema(description = "총 회원 수", example = "120")
  private long totalCount;

  @Schema(description = "회원 목록")
  private List<AdminUserResponseDto> users;
}
