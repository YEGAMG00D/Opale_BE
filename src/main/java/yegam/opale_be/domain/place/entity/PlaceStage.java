package yegam.opale_be.domain.place.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "place_stages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceStage {

  @Id
  @Column(name = "stage_id", length = 20, nullable = false)
  private String stageId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "place_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_place_stage_place"))
  private Place place;

  @Column(name = "name", length = 255)
  private String name;

  @Column(name = "seatscale")
  private Integer seatscale;

  @Column(name = "stageorchat")
  private Boolean stageorchat;

  @Column(name = "stagepracat")
  private Boolean stagepracat;

  @Column(name = "stagedresat")
  private Boolean stagedresat;

  @Column(name = "stageoutdrat")
  private Boolean stageoutdrat;

  @Column(name = "disabledseatscale")
  private Integer disabledseatscale;

  @Column(name = "stagearea", length = 100)
  private String stagearea;
}
