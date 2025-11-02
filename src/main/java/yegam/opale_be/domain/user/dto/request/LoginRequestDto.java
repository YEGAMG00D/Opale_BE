package yegam.opale_be.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "LoginRequest DTO", description = "로그인을 위한 데이터 전송")
public class LoginRequestDto {

  @Schema(description = "이메일 주소", example = "user@example.com")
  @Email(message = "이메일 형식이 올바르지 않습니다.")
  @NotBlank(message = "이메일은 필수 입력값입니다.")
  private String email;

  @Schema(description = "비밀번호", example = "qqqq1234!")
  @NotBlank(message = "비밀번호는 필수 입력값입니다.")
  private String password;
}
