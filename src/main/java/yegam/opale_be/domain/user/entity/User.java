package yegam.opale_be.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import yegam.opale_be.global.common.BaseTimeEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_email", columnNames = "email")
    },
    indexes = {
        @Index(name = "idx_nickname", columnList = "nickname")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long userId;


  @Column(nullable = false, unique = true, length = 255)
  private String email;

  @Column(nullable = false, length = 255)
  private String password;

  @Column(length = 50)
  private String name;

  private LocalDate birth;

  @Column(length = 10)
  private String gender;

  @Column(length = 20)
  private String phone;

  @Column(length = 255)
  private String address1;

  @Column(length = 255)
  private String address2;

  @Column(length = 50)
  private String nickname;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  @Column(nullable = false, length = 10)
  private Role role = Role.USER;

  @Builder.Default
  @Column(nullable = false)
  private Boolean isDeleted = false;

  private LocalDateTime deletedAt;

  @PrePersist
  public void prePersist() {
    if (role == null) role = Role.USER;
    if (isDeleted == null) isDeleted = false;
  }

  public enum Role {
    USER,
    ADMIN
  }
}
