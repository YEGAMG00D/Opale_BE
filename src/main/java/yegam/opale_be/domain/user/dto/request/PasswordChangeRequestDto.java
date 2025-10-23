package yegam.opale_be.domain.user.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordChangeRequestDto {

  @NotBlank(message = "현재 비밀번호를 입력해주세요.")
  private String currentPassword;

  @NotBlank(message = "새 비밀번호를 입력해주세요.")
  @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
  private String newPassword;
}
