package yegam.opale_be.domain.analytics.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.global.common.BaseTimeEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_event_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEventLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "log_id")
  private Long logId;

  @Enumerated(EnumType.STRING)
  @Column(name = "event_type", nullable = false, length = 20)
  private EventType eventType;

  @Enumerated(EnumType.STRING)
  @Column(name = "target_type", length = 20)
  private TargetType targetType;

  @Column(name = "target_id", length = 20)
  private String targetId;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_user_event_log_user"))
  private User user;

  public enum EventType {
    VIEW, LIKE, FAVORITE, REVIEW_WRITE, BOOKED
  }

  public enum TargetType {
    PERFORMANCE, PLACE, REVIEW
  }
}
