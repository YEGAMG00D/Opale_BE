package yegam.opale_be.domain.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import yegam.opale_be.domain.email.dto.request.VerifyCodeRequestDto;
import yegam.opale_be.domain.email.dto.response.EmailResponseDto;
import yegam.opale_be.domain.email.dto.response.VerifyCodeResponseDto;
import yegam.opale_be.domain.email.entity.VerificationCode;
import yegam.opale_be.domain.email.exception.EmailErrorCode;
import yegam.opale_be.domain.email.mapper.EmailMapper;
import yegam.opale_be.domain.email.repository.EmailRepository;
import yegam.opale_be.global.exception.CustomException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmailService {

  private final JavaMailSender mailSender;
  private final EmailRepository emailRepository;
  private final EmailMapper emailMapper;

  private static final int EXPIRE_TIME_SECONDS = 300; // 5분
  private static final String SUBJECT = "[Opale] 이메일 인증번호 안내";

  private static final Pattern EMAIL_REGEX =
      Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");


  // =============================================
  // 1) 이메일 인증번호 발송 (UPDATE 방식으로 개선)
  // =============================================
  public EmailResponseDto sendVerificationCode(String email) {

    // 이메일 형식 체크
    if (email == null || !EMAIL_REGEX.matcher(email).matches()) {
      log.warn("잘못된 이메일 형식 요청: {}", email);
      throw new CustomException(EmailErrorCode.INVALID_EMAIL_FORMAT);
    }

    // 인증번호 생성
    String code = generateVerificationCode();

    // 이메일 보내기
    sendHtmlEmail(email, SUBJECT, buildHtmlContent(code));

    // 기존 레코드 조회
    VerificationCode existing = emailRepository.findByEmail(email).orElse(null);

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime expiresAt = now.plusSeconds(EXPIRE_TIME_SECONDS);

    if (existing != null) {
      // 기존 레코드 업데이트
      existing.setCode(code);
      existing.setVerified(false);
      existing.setExpiresAt(expiresAt);
      emailRepository.save(existing);

      log.info("기존 이메일 인증번호 업데이트: email={}, code={}", email, code);
    } else {
      // 새 레코드 저장
      VerificationCode newEntity =
          emailMapper.toVerificationCodeEntity(email, code, EXPIRE_TIME_SECONDS);
      emailRepository.save(newEntity);

      log.info("새 인증번호 발송 완료: email={}, code={}", email, code);
    }

    return emailMapper.toEmailResponseDto(email, EXPIRE_TIME_SECONDS);
  }


  // =============================================
  // 2) 인증번호 검증
  // =============================================
  public VerifyCodeResponseDto verifyCode(VerifyCodeRequestDto dto) {
    VerificationCode codeEntity = emailRepository.findByEmail(dto.getEmail())
        .orElseThrow(() -> new CustomException(EmailErrorCode.EMAIL_NOT_FOUND));

    if (codeEntity.isExpired()) {
      throw new CustomException(EmailErrorCode.CODE_EXPIRED);
    }

    if (!codeEntity.getCode().equals(dto.getCode())) {
      throw new CustomException(EmailErrorCode.CODE_MISMATCH);
    }

    codeEntity.setVerified(true);
    emailRepository.save(codeEntity);

    log.info("이메일 인증 성공: {}", dto.getEmail());
    return emailMapper.toVerifyCodeResponseDto(dto.getEmail(), true);
  }


  // =============================================
  // 이메일 전송
  // =============================================
  private void sendHtmlEmail(String to, String subject, String htmlContent) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper =
          new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlContent, true);
      helper.setFrom("Opale <opalebyyegam@gmail.com>");

      mailSender.send(message);

    } catch (MessagingException e) {
      log.error("이메일 전송 실패: {}", e.getMessage());
      throw new CustomException(EmailErrorCode.SEND_FAILED);
    }
  }


  // =============================================
  // HTML 템플릿
  // =============================================
  private String buildHtmlContent(String code) {
    return """
            <div style="font-family: 'Pretendard', sans-serif; max-width: 500px; margin: auto; padding: 20px; border-radius: 16px; background: #fdfdfd; border: 1px solid #ddd;">
              <h2 style="color: #5C4B99; text-align: center;">🎭 Opale 이메일 인증</h2>
              <p style="font-size: 15px; color: #333;">안녕하세요, <b>Opale</b>입니다.<br><br>
              아래의 인증번호를 입력하여 이메일 인증을 완료해주세요.</p>

              <div style="text-align: center; margin: 20px 0;">
                <span style="display: inline-block; background: #5C4B99; color: white; font-size: 28px; font-weight: bold; letter-spacing: 4px; padding: 10px 20px; border-radius: 12px;">
                  %s
                </span>
              </div>

              <p style="font-size: 14px; color: #666;">⏰ 인증번호 유효시간: <b>5분</b><br>
              이 메일을 요청하지 않았다면 무시하셔도 됩니다.</p>

              <hr style="border: none; border-top: 1px solid #eee; margin: 25px 0;">
              <p style="font-size: 13px; color: #999; text-align: center;">
                © 2025 Opale. All rights reserved.<br>
                공연 정보, 리뷰, 그리고 문화 이야기의 중심.
              </p>
            </div>
            """.formatted(code);
  }

  // =============================================
  // 인증번호 생성 (6자리)
  // =============================================
  private String generateVerificationCode() {
    int code = 100000 + new Random().nextInt(900000);
    return String.valueOf(code);
  }
}
