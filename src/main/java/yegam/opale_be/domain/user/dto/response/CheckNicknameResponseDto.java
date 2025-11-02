package yegam.opale_be.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "닉네임 중복 확인 응답 DTO", description = "닉네임 사용 가능 여부를 반환합니다.")
public class CheckNicknameResponseDto {

  @Schema(description = "닉네임", example = "문화덕후")
  private String nickname;

  @Schema(description = "사용 가능 여부 (true = 사용 가능, false = 중복됨)", example = "true")
  private boolean available;
}
