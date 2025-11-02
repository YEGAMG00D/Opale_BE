package yegam.opale_be.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "UserUpdateRequest DTO", description = "본인 정보 변경을 위한 데이터 전송")
public class UserUpdateRequestDto {

  @Schema(description = "연락처", example = "01012345678")
  private String phone;

  @Schema(description = "주소 1", example = "서울특별시 강남구 테헤란로 123")
  private String address1;

  @Schema(description = "주소 2", example = "아파트 101동 1001호")
  private String address2;

  @Schema(description = "닉네임", example = "문화덕후")
  private String nickname;
}
