package yegam.userservice.domain.user.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDeleteRequestDto {
  private String reason; // 탈퇴 사유 (선택 입력)
}
