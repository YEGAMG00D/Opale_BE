package yegam.opale_be.domain.chat.message.entity;

import jakarta.persistence.*;
import lombok.*;
import yegam.opale_be.domain.chat.room.entity.ChatRoom;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.global.common.BaseTimeEntity;
import java.time.LocalDateTime;

/**
 * ChatMessage
 * - 채팅방 내 개별 메시지
 * - 삭제 플래그 포함
 */
@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "message_id", nullable = false)
  private Long messageId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "room_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_chat_message_room"))
  private ChatRoom chatRoom;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_chat_message_user"))
  private User user;

  @Lob
  @Column(name = "contents", nullable = false, columnDefinition = "TEXT")
  private String contents;

  @Column(name = "sent_at", nullable = false)
  private LocalDateTime sentAt;

  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted = false;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;
}
