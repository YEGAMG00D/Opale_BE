package yegam.opale_be.domain.preference.entity;


import jakarta.persistence.*;
import lombok.*;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.global.common.BaseTimeEntity;

@Entity
@Table(name = "user_preference_vectors")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceVector extends BaseTimeEntity {

  @Id
  @Column(name = "user_preference_vector_id", length = 20)
  private Long userPreferenceVectorId;

  @Lob
  @Column(name = "embedding_vector", columnDefinition = "MEDIUMTEXT")
  private String embeddingVector;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "user_id",
      foreignKey = @ForeignKey(name = "fk_user_vector_user"))
  private User user;
}

