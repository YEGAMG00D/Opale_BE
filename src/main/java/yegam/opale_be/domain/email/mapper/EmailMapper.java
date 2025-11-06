package yegam.opale_be.domain.email.mapper;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import yegam.opale_be.domain.email.dto.response.EmailResponseDto;
import yegam.opale_be.domain.email.dto.response.VerifyCodeResponseDto;
import yegam.opale_be.domain.email.entity.VerificationCode;

@Component
public class EmailMapper {

  /** 인증번호 저장용 Entity 생성 */
  public VerificationCode toVerificationCodeEntity(String email, String code, int expiresInSeconds) {
    return VerificationCode.builder()
        .email(email)
        .code(code)
        .expiresAt(LocalDateTime.now().plusSeconds(expiresInSeconds))
        .verified(false)
        .build();
  }

  /** 인증번호 발송 결과 → EmailResponseDto 변환 */
  public EmailResponseDto toEmailResponseDto(String email, int expiresInSeconds) {
    return EmailResponseDto.builder()
        .email(email)
        .message("인증번호가 발송되었습니다.")
        .expiresIn(expiresInSeconds)
        .build();
  }

  /** 인증번호 확인 결과 → VerifyCodeResponseDto 변환 */
  public VerifyCodeResponseDto toVerifyCodeResponseDto(String email, boolean verified) {
    return VerifyCodeResponseDto.builder()
        .email(email)
        .verified(verified)
        .message(verified ? "이메일 인증이 완료되었습니다." : "인증번호가 일치하지 않습니다.")
        .build();
  }
}
