package yegam.opale_be.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "UserListResponse DTO", description = "전체 회원 조회를 위한 데이터 전송")
public class UserListResponseDto {

  @Schema(description = "사용자 ID", example = "user_0001")
  private Long userId;

  @Schema(description = "이메일 주소", example = "user@example.com")
  private String email;

  @Schema(description = "이름", example = "김유저")
  private String name;

  @Schema(description = "닉네임", example = "문화덕후")
  private String nickname;

  @Schema(description = "생년월일", example = "1998-07-15")
  private LocalDate birth;

  @Schema(description = "회원 역할", example = "USER")
  private String role;

  @Schema(description = "탈퇴 여부", example = "false")
  private Boolean isDeleted;

  @Schema(description = "가입일", example = "2025-10-31T12:30:00")
  private LocalDateTime createdAt;
}
