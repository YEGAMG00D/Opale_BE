package yegam.opale_be.domain.culture.entity;

import jakarta.persistence.*;
import lombok.*;
import yegam.opale_be.domain.place.entity.Place;

@Entity
@Table(name = "performances")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Performance {

  @Id
  @Column(name = "performance_id", length = 20, nullable = false)
  private String performanceId;

  @Column(name = "title", length = 100)
  private String title;

  @Column(name = "start_date")
  private java.sql.Date startDate;

  @Column(name = "end_date")
  private java.sql.Date endDate;

  @Column(name = "place_name", length = 255)
  private String placeName;

  @Column(name = "prfcast", length = 255)
  private String prfcast;

  @Column(name = "prfcrew", length = 255)
  private String prfcrew;

  @Column(name = "prfruntime", length = 255)
  private String prfruntime;

  @Column(name = "prfage", length = 50)
  private String prfage;

  @Column(name = "entrpsnm", length = 255)
  private String entrpsnm;

  @Column(name = "entrpsnm_p", length = 255)
  private String entrpsnmP;

  @Column(name = "entrpsnm_a", length = 255)
  private String entrpsnmA;

  @Column(name = "entrpsnm_h", length = 255)
  private String entrpsnmH;

  @Column(name = "entrpsnm_s", length = 255)
  private String entrpsnmS;

  @Column(name = "price", length = 255)
  private String price;

  @Column(name = "poster", length = 255)
  private String poster;

  @Lob
  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "area", length = 20)
  private String area;

  @Column(name = "genrenm", length = 20)
  private String genrenm;

  @Column(name = "openrun")
  private Boolean openrun;

  @Column(name = "visit")
  private Boolean visit;

  @Column(name = "child")
  private Boolean child;

  @Column(name = "daehakro")
  private Boolean daehakro;

  @Column(name = "festival")
  private Boolean festival;

  @Column(name = "musicallicense")
  private Boolean musicallicense;

  @Column(name = "musicalcreate")
  private Boolean musicalcreate;

  @Column(name = "updatedate")
  private java.time.LocalDateTime updatedate;

  @Column(name = "prfstate", length = 10)
  private String prfstate;

  @Column(name = "time", length = 255)
  private String time;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "place_id",
      foreignKey = @ForeignKey(name = "fk_performance_place"))
  private Place place;

  @Lob
  @Column(name = "ai_summary", columnDefinition = "TEXT")
  private String aiSummary;

  @Lob
  @Column(name = "ai_keywords", columnDefinition = "TEXT")
  private String aiKeywords;

  @Lob
  @Column(name = "embedding_vector", columnDefinition = "MEDIUMTEXT")
  private String embeddingVector;
}
