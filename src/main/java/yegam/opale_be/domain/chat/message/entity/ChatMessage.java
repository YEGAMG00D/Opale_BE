package yegam.opale_be.domain.chat.message.entity;


import jakarta.persistence.*;
import lombok.*;
import yegam.opale_be.domain.chat.room.entity.ChatRoom;
import yegam.opale_be.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

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
  @Column(name = "contents", columnDefinition = "TEXT")
  private String contents;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "is_deleted")
  private Boolean isDeleted;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;
}

