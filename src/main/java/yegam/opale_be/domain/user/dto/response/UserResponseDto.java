package yegam.opale_be.domain.user.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import yegam.opale_be.domain.user.entity.User;

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
