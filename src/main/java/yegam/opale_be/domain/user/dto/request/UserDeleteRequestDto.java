package yegam.opale_be.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "UserDeleteRequest DTO", description = "회원 탈퇴(본인)를 위한 데이터 전송")
public class UserDeleteRequestDto {

  @Schema(description = "비밀번호", example = "qqqq1234!")
  @NotBlank(message = "비밀번호는 필수 입력값입니다.")
  private String password;
}
