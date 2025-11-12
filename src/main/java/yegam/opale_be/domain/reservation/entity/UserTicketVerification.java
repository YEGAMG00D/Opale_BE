package yegam.opale_be.domain.reservation.entity;

import jakarta.persistence.*;
import lombok.*;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.place.entity.Place;
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
  private Long ticketId; // 티켓 인증 내역 고유의 id

  @Column(name = "ticket_number", length = 100)
  private String ticketNumber; // 예매 번호

  @Enumerated(EnumType.STRING)
  @Column(name = "source", length = 10)
  private Source source; // 인증 방식 - ENUM('OCR', 'MANUAL', 'ADMIN')

  @Column(name = "ticket_image_url", length = 255)
  private String ticketImageUrl; // 티켓 이미지 경로 url

  @Column(name = "is_verified")
  private Boolean isVerified; // 인증 성공 여부

  @Column(name = "requested_at")
  private LocalDateTime requestedAt; // 인증 요청 시각

  @Column(name = "updated_at")
  private LocalDateTime updatedAt; // 수정 시각

  @Column(name = "performance_date")
  private LocalDateTime performanceDate; // 공연 관람 날짜

  @Column(name = "seat_info", length = 50)
  private String seatInfo; // 좌석 정보

  @Column(name = "performance_name", length = 100)
  private String performanceName; // 공연 명

  @Column(name = "place_name", length = 100)
  private String placeName; // 공연장 명


  // 사용자 (항상 존재해야 함)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_ticket_user"))
  private User user;


  // 공연 (null 허용)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "performance_id", nullable = true,
      foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  private Performance performance;


  // 공연장 (null 허용)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "place_id", nullable = true,
      foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  private Place place;
}
