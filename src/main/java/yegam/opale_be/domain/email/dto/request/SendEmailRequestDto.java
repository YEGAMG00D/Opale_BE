package yegam.opale_be.domain.email.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "이메일 인증번호 발송 요청 DTO")
public class SendEmailRequestDto {

  @Email(message = "유효한 이메일 주소를 입력해주세요.")
  @NotBlank(message = "이메일은 필수 입력값입니다.")
  @Schema(description = "사용자 이메일 주소", example = "user@example.com")
  private String email;
}
