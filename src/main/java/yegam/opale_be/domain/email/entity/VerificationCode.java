package yegam.opale_be.domain.email.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import yegam.opale_be.global.common.BaseTimeEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "email_verification_codes")
public class VerificationCode extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String code;

  @Column(nullable = false)
  private LocalDateTime expiresAt;

  /** ✅ 인증 완료 여부 */
  @Column(nullable = false)
  private Boolean verified = false;

  /** ✅ 만료 여부 확인 */
  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiresAt);
  }
}
