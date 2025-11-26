package yegam.opale_be.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "PasswordResetResponse DTO", description = "임시 비밀번호 발급 결과")
public class PasswordResetResponseDto {

  @Schema(description = "임시 비밀번호 발급된 사용자 이메일")
  private String email;

  @Schema(description = "임시 비밀번호 발급 성공 여부")
  private boolean success;
}
