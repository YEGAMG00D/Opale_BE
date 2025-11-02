package yegam.opale_be.domain.email.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yegam.opale_be.domain.email.dto.request.SendEmailRequestDto;
import yegam.opale_be.domain.email.dto.request.VerifyCodeRequestDto;
import yegam.opale_be.domain.email.dto.response.EmailResponseDto;
import yegam.opale_be.domain.email.dto.response.VerifyCodeResponseDto;
import yegam.opale_be.domain.email.service.EmailService;
import yegam.opale_be.global.response.BaseResponse;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Tag(name = "Email", description = "이메일 인증 관련 API")
public class EmailController {

  private final EmailService emailService;

  /** ✅ 인증번호 발송 */
  @Operation(
      summary = "이메일 인증번호 발송",
      description = "입력한 이메일 주소로 인증번호를 전송합니다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "인증번호 발송 성공",
              content = @Content(schema = @Schema(implementation = EmailResponseDto.class)))
      }
  )
  @PostMapping("/send")
  public ResponseEntity<BaseResponse<EmailResponseDto>> sendVerificationCode(
      @RequestBody SendEmailRequestDto requestDto
  ) {
    EmailResponseDto response = emailService.sendVerificationCode(requestDto.getEmail());
    return ResponseEntity.ok(BaseResponse.success("인증번호 발송 성공", response));
  }

  /** ✅ 인증번호 검증 */
  @Operation(
      summary = "이메일 인증번호 검증",
      description = "입력한 이메일과 인증번호가 일치하는지 확인합니다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "인증번호 확인 성공",
              content = @Content(schema = @Schema(implementation = VerifyCodeResponseDto.class)))
      }
  )
  @PostMapping("/verify")
  public ResponseEntity<BaseResponse<VerifyCodeResponseDto>> verifyCode(
      @RequestBody VerifyCodeRequestDto requestDto
  ) {
    VerifyCodeResponseDto response = emailService.verifyCode(requestDto);
    return ResponseEntity.ok(BaseResponse.success("인증번호 검증 성공", response));
  }
}
