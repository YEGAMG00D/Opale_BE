package yegam.opale_be.domain.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import yegam.opale_be.domain.email.dto.request.VerifyCodeRequestDto;
import yegam.opale_be.domain.email.dto.response.EmailResponseDto;
import yegam.opale_be.domain.email.dto.response.VerifyCodeResponseDto;
import yegam.opale_be.domain.email.exception.EmailErrorCode;
import yegam.opale_be.global.exception.CustomException;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmailService {

  private final JavaMailSender mailSender;
  private final StringRedisTemplate redisTemplate;

  private static final int EXPIRE_TIME_SECONDS = 300;
  private static final String SUBJECT = "[Opale] 이메일 인증번호 안내";
  private static final String EMAIL_KEY_PREFIX = "email:verify:";
  private static final String VERIFIED_KEY_PREFIX = "email:verified:";

  private static final Pattern EMAIL_REGEX =
      Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

  // ===============================
  // 1) 이메일 인증번호 발송 (Redis)
  // ===============================
  public EmailResponseDto sendVerificationCode(String email) {

    if (email == null || !EMAIL_REGEX.matcher(email).matches()) {
      throw new CustomException(EmailErrorCode.INVALID_EMAIL_FORMAT);
    }

    String code = generateVerificationCode();
    sendHtmlEmail(email, SUBJECT, buildVerificationHtml(code));

    String key = EMAIL_KEY_PREFIX + email;

    redisTemplate.opsForValue().set(
        key,
        code,
        EXPIRE_TIME_SECONDS,
        TimeUnit.SECONDS
    );

    log.info("Redis 이메일 인증 저장: key={}, code={}", key, code);

    return EmailResponseDto.builder()
        .email(email)
        .message("인증번호가 발송되었습니다.")
        .expiresIn(EXPIRE_TIME_SECONDS)
        .build();
  }

  // ===============================
  // 2) 인증번호 검증 (Redis) + 인증 성공 기록
  // ===============================
  public VerifyCodeResponseDto verifyCode(VerifyCodeRequestDto dto) {

    String key = EMAIL_KEY_PREFIX + dto.getEmail();
    String redisCode = redisTemplate.opsForValue().get(key);

    if (redisCode == null) {
      throw new CustomException(EmailErrorCode.CODE_EXPIRED);
    }

    if (!redisCode.equals(dto.getCode())) {
      throw new CustomException(EmailErrorCode.CODE_MISMATCH);
    }

    String verifiedKey = VERIFIED_KEY_PREFIX + dto.getEmail();
    redisTemplate.opsForValue().set(verifiedKey, "true", 10, TimeUnit.MINUTES);

    redisTemplate.delete(key);

    log.info("이메일 인증 성공 (Redis): {}", dto.getEmail());

    return VerifyCodeResponseDto.builder()
        .email(dto.getEmail())
        .verified(true)
        .message("이메일 인증이 완료되었습니다.")
        .build();
  }

  // ===============================
  // 회원가입 전 인증 여부 확인
  // ===============================
  public boolean isVerifiedForSignUp(String email) {
    String key = VERIFIED_KEY_PREFIX + email;
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }

  // ===============================
  // 회원가입 성공 후 인증 기록 삭제
  // ===============================
  public void clearVerifiedEmail(String email) {
    String key = VERIFIED_KEY_PREFIX + email;
    redisTemplate.delete(key);
  }

  // ===============================
  // 임시 비밀번호 이메일 발송
  // ===============================
  public void sendTempPassword(String email, String tempPassword) {

    if (email == null || !EMAIL_REGEX.matcher(email).matches()) {
      throw new CustomException(EmailErrorCode.INVALID_EMAIL_FORMAT);
    }

    String subject = "[Opale] 임시 비밀번호 안내";
    String html = buildTempPasswordHtml(tempPassword);

    sendHtmlEmail(email, subject, html);
    log.info("임시 비밀번호 이메일 발송 완료: email={}", email);
  }

  // ===============================
  // 이메일 전송 공통
  // ===============================
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
      throw new CustomException(EmailErrorCode.SEND_FAILED);
    }
  }

  // ===============================
  // 이메일 인증 HTML (고급 버전)
  // ===============================
  private String buildVerificationHtml(String code) {
    return """
      <div style="font-family:Pretendard, sans-serif; background:#f6f7fb; padding:40px;">
        <div style="max-width:520px; margin:auto; background:white; border-radius:18px; padding:32px; box-shadow:0 4px 14px rgba(0,0,0,0.08);">
          
          <h2 style="text-align:center; color:#5C4B99; margin-bottom:8px;">
            Opale 이메일 인증
          </h2>

          <p style="font-size:15px; color:#444; text-align:center; line-height:1.6;">
            아래 인증번호를 입력하여 이메일 인증을 완료해주세요.
          </p>

          <div style="text-align:center; margin:28px 0;">
            <span style="
              display:inline-block;
              background:#5C4B99;
              color:white;
              font-size:30px;
              font-weight:bold;
              letter-spacing:6px;
              padding:14px 32px;
              border-radius:16px;">
              %s
            </span>
          </div>

          <p style="font-size:14px; color:#777; text-align:center;">
            인증번호 유효시간은 <b>5분</b>입니다.
          </p>

          <hr style="border:none; border-top:1px solid #eee; margin:30px 0;">

          <p style="font-size:12px; color:#aaa; text-align:center; line-height:1.6;">
            본 메일은 Opale 서비스 이용을 위한 인증 메일입니다.<br>
            본인이 요청하지 않았다면 해당 메일을 무시하셔도 됩니다.
          </p>

          <p style="font-size:12px; color:#bbb; text-align:center; margin-top:18px;">
            © 2025 Opale · 공연 정보 플랫폼
          </p>

        </div>
      </div>
    """.formatted(code);
  }

  // ===============================
  // 임시 비밀번호 HTML
  // ===============================
  private String buildTempPasswordHtml(String tempPassword) {
    return """
      <div style="font-family:Pretendard, sans-serif; background:#f6f7fb; padding:40px;">
        <div style="max-width:520px; margin:auto; background:white; border-radius:18px; padding:32px; box-shadow:0 4px 14px rgba(0,0,0,0.08);">

          <h2 style="text-align:center; color:#5C4B99;">
            Opale 임시 비밀번호 발급
          </h2>

          <p style="font-size:15px; color:#444; text-align:center; line-height:1.6;">
            요청하신 임시 비밀번호가 발급되었습니다.<br>
            로그인 후 반드시 새 비밀번호로 변경해주세요.
          </p>

          <div style="text-align:center; margin:28px 0;">
            <span style="
              display:inline-block;
              background:#5C4B99;
              color:white;
              font-size:22px;
              font-weight:bold;
              padding:14px 28px;
              border-radius:14px;">
              %s
            </span>
          </div>

          <p style="font-size:13px; color:#777; text-align:center;">
            보안을 위해 임시 비밀번호는 1회 사용 후 변경을 권장드립니다.
          </p>

          <hr style="border:none; border-top:1px solid #eee; margin:30px 0;">

          <p style="font-size:12px; color:#bbb; text-align:center; margin-top:18px;">
            © 2025 Opale · 공연 정보 플랫폼
          </p>

        </div>
      </div>
    """.formatted(tempPassword);
  }

  // ===============================
  // 인증번호 생성
  // ===============================
  private String generateVerificationCode() {
    int code = 100000 + new Random().nextInt(900000);
    return String.valueOf(code);
  }
}
