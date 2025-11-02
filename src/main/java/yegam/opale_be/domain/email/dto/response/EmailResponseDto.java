package yegam.opale_be.domain.email.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "EmailResponse DTO", description = "이메일 인증 관련 응답 DTO")
public class EmailResponseDto {

  @Schema(description = "이메일 주소", example = "user@example.com")
  private String email;

  @Schema(description = "요청 처리 결과 메시지", example = "인증번호가 발송되었습니다.")
  private String message;

  @Schema(description = "인증번호 만료까지 남은 시간(초)", example = "300")
  private Integer expiresIn;
}
