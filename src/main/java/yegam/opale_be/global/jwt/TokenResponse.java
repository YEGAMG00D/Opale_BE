package yegam.opale_be.global.jwt;

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
