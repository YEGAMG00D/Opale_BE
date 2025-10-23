package yegam.userservice.domain.user.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import yegam.userservice.domain.user.entity.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {

  private Long id;
  private String email;
  private String nickname;
  private String name;
  private LocalDate birth;
}
