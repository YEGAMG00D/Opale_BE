package yegam.opale_be.domain.review.performance.entity;


import jakarta.persistence.*;
import lombok.*;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.reservation.entity.UserTicketVerification;
import yegam.opale_be.domain.review.common.ReviewType;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.global.common.BaseTimeEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "performance_reviews")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceReview extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "performance_review_id", nullable = false)
  private Long performanceReviewId;

  @Column(name = "review_type", length = 20, nullable = false)
  @Enumerated(EnumType.STRING)
  private ReviewType reviewType;

  @Column(name = "title", length = 255)
  private String title;

  @Lob
  @Column(name = "contents", columnDefinition = "TEXT")
  private String contents;

  @Column(name = "rating")
  private Float rating;

  @Builder.Default
  @Column(name = "is_deleted")
  private Boolean isDeleted = false;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_perf_review_user"))
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "performance_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_perf_review_performance"))
  private Performance performance;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ticket_id", nullable = true,
      foreignKey = @ForeignKey(name = "fk_perf_review_ticket"))
  private UserTicketVerification ticket;



}

