package yegam.opale_be.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "CheckEmailRequest DTO", description = "이메일 인증번호 전송을 위한 데이터 전송")
public class CheckEmailRequestDto {

  @Schema(description = "이메일 주소", example = "user@example.com")
  @Email(message = "이메일 형식이 올바르지 않습니다.")
  @NotBlank(message = "이메일은 필수 입력값입니다.")
  private String email;
}
