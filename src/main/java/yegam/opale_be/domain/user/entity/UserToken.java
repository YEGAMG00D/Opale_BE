package yegam.opale_be.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_tokens")
public class UserToken {

  @Id
  private Long userId;

  @Column(nullable = false, length = 512)
  private String refreshToken;

  private LocalDateTime issuedAt;
  private LocalDateTime expiresAt;
}

