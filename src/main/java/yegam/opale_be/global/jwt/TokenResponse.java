package yegam.userservice.global.jwt;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponse {
  private String accessToken;
  private String refreshToken;
}
