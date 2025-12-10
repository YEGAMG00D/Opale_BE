package yegam.opale_be.domain.favorite.performance.entity;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.global.common.BaseTimeEntity;

@Entity
@Table(name = "favorite_performance")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoritePerformance extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "performance_like_id", nullable = false)
  private Long performanceLikeId;

  @Builder.Default
  @Column(name = "is_liked", nullable = false)
  private Boolean isLiked = true;

  @Builder.Default
  @Column(name = "is_deleted")
  private Boolean isDeleted = false;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_fav_performance_user"))
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "performance_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_fav_performance_performance"))
  private Performance performance;
}

