package yegam.opale_be.domain.place.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "places")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Place {

  @Id
  @Column(name = "place_id", length = 20, nullable = false)
  private String placeId;

  @Column(name = "name", length = 255)
  private String name;

  @Column(name = "stage_count")
  private Integer stageCount;

  @Column(name = "fcltychartr", length = 50)
  private String fcltychartr;

  @Column(name = "opende")
  private Integer opende;

  @Column(name = "seatscale")
  private Integer seatscale;

  @Column(name = "telno", length = 20)
  private String telno;

  @Column(name = "relateurl", length = 255)
  private String relateurl;

  @Column(name = "address", length = 255)
  private String address;

  @Column(name = "la", precision = 10, scale = 6)
  private BigDecimal la;

  @Column(name = "lo", precision = 11, scale = 8)
  private BigDecimal lo;

  @Column(name = "restaurant")
  private Boolean restaurant;

  @Column(name = "cafe")
  private Boolean cafe;

  @Column(name = "store")
  private Boolean store;

  @Column(name = "nolitbang")
  private Boolean nolibang;

  @Column(name = "suyu")
  private Boolean suyu;

  @Column(name = "parkbarrier")
  private Boolean parkbarrier;

  @Column(name = "restbarrier")
  private Boolean restbarrier;

  @Column(name = "runwbarrier")
  private Boolean runwbarrier;

  @Column(name = "elevbarrier")
  private Boolean elevbarrier;

  @Column(name = "parkinglot")
  private Boolean parkinglot;
}
