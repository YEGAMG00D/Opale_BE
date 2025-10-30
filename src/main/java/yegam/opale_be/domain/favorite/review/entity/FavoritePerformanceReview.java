package yegam.opale_be.domain.favorite.review.entity;



import jakarta.persistence.*;
import lombok.*;
import yegam.opale_be.domain.review.performance.entity.PerformanceReview;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.global.common.BaseTimeEntity;

@Entity
@Table(name = "favorite_performance_reviews")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoritePerformanceReview extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "performance_review_like_id", nullable = false)
  private Long performanceReviewLikeId;

  @Column(name = "is_liked", nullable = false)
  private Boolean isLiked = true;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_fav_perf_review_user"))
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "performance_review_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_fav_perf_review_review"))
  private PerformanceReview performanceReview;
}
