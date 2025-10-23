package yegam.opale_be.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignUpRequestDto {

  @Schema(description = "이메일 주소", example = "user@example.com")
  @Email(message = "이메일 형식이 올바르지 않습니다.")
  @NotBlank(message = "이메일은 필수 입력값입니다.")
  private String email;

  @Schema(description = "비밀번호", example = "qqqq1234!")
  @NotBlank(message = "비밀번호는 필수 입력값입니다.")
  @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
  private String password;

  @Schema(description = "성명", example = "김유저")
  @NotBlank(message = "이름은 필수 입력값입니다.")
  private String name;

  @Schema(description = "생년월일 (yyyy-MM-dd 형식)", example = "1998-07-15")
  private LocalDate birth; // LocalDate로 직접 받기

  @Schema(description = "성별 (남/여 중 하나)", example = "여")
  private String gender;

  @Schema(description = "휴대폰 번호(숫자만 입력)", example = "01012345678")
  @Pattern(regexp = "^[0-9]{10,11}$", message = "연락처 형식이 올바르지 않습니다.")
  private String phone;

  @Schema(description = "주소", example = "서울특별시 강남구 테헤란로 123")
  private String address;

  @Schema(description = "닉네임", example = "집이대학로")
  @NotBlank(message = "닉네임은 필수 입력값입니다.")
  private String nickname;



}
