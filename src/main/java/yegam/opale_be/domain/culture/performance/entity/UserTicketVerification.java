package yegam.opale_be.domain.culture.performance.entity;



import jakarta.persistence.*;
import lombok.*;
import yegam.opale_be.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_ticket_verifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTicketVerification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ticket_id", nullable = false)
  private Long ticketId;

  @Column(name = "ticket_number", length = 100)
  private String ticketNumber;

  @Enumerated(EnumType.STRING)
  @Column(name = "source", length = 10)
  private Source source;

  @Column(name = "ticket_image_url", length = 255)
  private String ticketImageUrl;

  @Column(name = "is_verified")
  private Boolean isVerified;

  @Column(name = "requested_at")
  private LocalDateTime requestedAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "performance_date")
  private LocalDateTime performanceDate;

  @Column(name = "seat_info", length = 50)
  private String seatInfo;

  @Column(name = "performance_name", length = 50)
  private String performanceName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_ticket_user"))
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "performance_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_ticket_performance"))
  private Performance performance;

  public enum Source {
    OCR, MANUAL, ADMIN
  }
}

