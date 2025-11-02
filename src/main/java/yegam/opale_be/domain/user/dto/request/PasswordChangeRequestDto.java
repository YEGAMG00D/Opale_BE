package yegam.opale_be.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PasswordChangeRequest DTO", description = "비밀번호 변경을 위한 데이터 전송")
public class PasswordChangeRequestDto {

  @Schema(description = "현재 비밀번호", example = "qqqq1234!")
  @NotBlank(message = "현재 비밀번호는 필수 입력값입니다.")
  private String currentPassword;

  @Schema(description = "새 비밀번호", example = "newpass123!")
  @NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
  @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
  @Pattern(
      regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=]).{8,}$",
      message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
  )
  private String newPassword;
}
