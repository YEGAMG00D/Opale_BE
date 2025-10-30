package yegam.opale_be.domain.culture.performance.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "performance_relations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceRelation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "relation_id", nullable = false)
  private Long relationId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "performance_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_relation_performance"))
  private Performance performance;

  @Column(name = "site_name", length = 100)
  private String siteName;

  @Column(name = "site_url", length = 255)
  private String siteUrl;
}
