package yegam.opale_be.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "CheckCodeRequest DTO", description = "이메일 인증번호 확인을 위한 데이터 전송")
public class CheckCodeRequestDto {

  @Schema(description = "이메일 주소", example = "user@example.com")
  @Email(message = "이메일 형식이 올바르지 않습니다.")
  private String email;

  @Schema(description = "인증번호", example = "482913")
  @NotBlank(message = "인증번호는 필수 입력값입니다.")
  @Size(min = 4, max = 6, message = "인증번호는 4~6자리 숫자여야 합니다.")
  private String code;
}
