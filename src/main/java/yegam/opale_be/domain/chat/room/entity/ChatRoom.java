package yegam.opale_be.domain.chat.room.entity;

import jakarta.persistence.*;
import lombok.*;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.global.common.BaseTimeEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "room_id", nullable = false)
  private Long roomId;

  @Column(name = "title", length = 100, nullable = false)
  private String title;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "performance_id",
      foreignKey = @ForeignKey(name = "fk_chatroom_performance"))
  private Performance performance;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_id",
      foreignKey = @ForeignKey(name = "fk_chatroom_creator"))
  private User creator;

  @Enumerated(EnumType.STRING)
  @Column(name = "room_type", length = 30, nullable = false)
  private RoomType roomType;

  @Column(name = "is_public", nullable = false)
  private Boolean isPublic = true;

  @Column(name = "password", length = 100)
  private String password;

  /** 채팅방 조회수 */
  @Column(name = "visit_count", nullable = false)
  private Long visitCount = 0L;

  @Column(name = "last_message", length = 255)
  private String lastMessage;

  @Column(name = "last_message_time")
  private LocalDateTime lastMessageTime;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive = false;

  @Column(name = "thumbnail_url", length = 255)
  private String thumbnailUrl;

  @Column(name = "description", length = 255)
  private String description;
}
