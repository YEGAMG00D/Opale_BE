package yegam.opale_be.domain.favorite.review.entity;



import jakarta.persistence.*;
import lombok.*;
import yegam.opale_be.domain.review.place.entity.PlaceReview;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.global.common.BaseTimeEntity;

@Entity
@Table(name = "favorite_place_reviews")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoritePlaceReview extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "place_review_like_id", nullable = false)
  private Long placeReviewLikeId;

  @Column(name = "is_liked", nullable = false)
  private Boolean isLiked = true;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_favorite_place_review_user"))
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "place_review_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_favorite_place_review_place_review"))
  private PlaceReview placeReview;
}
