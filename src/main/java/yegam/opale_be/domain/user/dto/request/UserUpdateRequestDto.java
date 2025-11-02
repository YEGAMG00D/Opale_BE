package yegam.opale_be.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "UserUpdateRequest DTO", description = "본인 정보 변경을 위한 데이터 전송")
public class UserUpdateRequestDto {

  @Schema(description = "휴대폰 번호(숫자만 입력)", example = "01012345678")
  @Pattern(regexp = "^[0-9]{10,11}$", message = "연락처 형식이 올바르지 않습니다.")
  private String phone;

  @Schema(description = "주소", example = "서울특별시 강남구 테헤란로 123")
  private String address1;

  @Schema(description = "주소", example = "큰길타워 301호")
  private String address2;

  @Schema(description = "닉네임", example = "집이대학로")
  @NotBlank(message = "닉네임은 필수 입력값입니다.")
  @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해야 합니다.")
  private String nickname;



}
