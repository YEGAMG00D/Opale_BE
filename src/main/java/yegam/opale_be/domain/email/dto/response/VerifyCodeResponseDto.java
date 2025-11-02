package yegam.opale_be.domain.email.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "이메일 인증 확인 응답 DTO", description = "입력한 인증번호 검증 결과를 반환")
public class VerifyCodeResponseDto {

  @Schema(description = "인증 메일 대상 이메일", example = "user@example.com")
  private String email;

  @Schema(description = "인증 성공 여부", example = "true")
  private boolean verified;

  @Schema(description = "결과 메시지", example = "이메일 인증이 완료되었습니다.")
  private String message;
}
