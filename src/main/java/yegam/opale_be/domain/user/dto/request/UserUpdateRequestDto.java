package yegam.opale_be.domain.user.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequestDto {

  @Size(max = 20, message = "닉네임은 20자 이하로 입력해주세요.")
  private String nickname;

  @Pattern(regexp = "^[0-9]{10,11}$", message = "연락처 형식이 올바르지 않습니다.")
  private String phone;

  private String address;
}
