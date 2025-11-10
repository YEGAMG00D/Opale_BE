package yegam.opale_be.domain.chat.room.entity;

import jakarta.persistence.*;
import lombok.*;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.global.common.BaseTimeEntity;

import java.time.LocalDateTime;

/**
 * 🎭 ChatRoom
 * - 공연별/단체/개인 대화방 통합 관리
 */
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

  /** 방 이름 (예: "뮤지컬 위키드 실시간 소감방" / "userA ↔ userB") */
  @Column(name = "title", length = 100, nullable = false)
  private String title;

  /** 공연 연관 정보 (공연 채팅방일 경우에만 존재) */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "performance_id", foreignKey = @ForeignKey(name = "fk_chatroom_performance"))
  private Performance performance;

  /** 방 개설자 */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_id", foreignKey = @ForeignKey(name = "fk_chatroom_creator"))
  private User creator;

  /** 방 유형 (공연 오픈 / 공연 단체 / 개인 DM 등) */
  @Enumerated(EnumType.STRING)
  @Column(name = "room_type", length = 30, nullable = false)
  private RoomType roomType;

  /** 공개 여부 */
  @Column(name = "is_public", nullable = false)
  private Boolean isPublic = true;

  /** 비공개방 비밀번호 (공개방이면 null) */
  @Column(name = "password", length = 100)
  private String password;

  /** 누적 방문자 수 */
  @Column(name = "visit_count", nullable = false)
  private int visitCount = 0;

  /** 최근 메시지 미리보기 */
  @Column(name = "last_message", length = 255)
  private String lastMessage;

  /** 최근 메시지 시각 */
  @Column(name = "last_message_time")
  private LocalDateTime lastMessageTime;

  /** 활성 상태 (최근 메시지 여부 등) */
  @Column(name = "is_active", nullable = false)
  private Boolean isActive = false;

  /** 방 썸네일 (공연 포스터나 사용자 프로필 등) */
  @Column(name = "thumbnail_url", length = 255)
  private String thumbnailUrl;

  /** 방 설명 */
  @Column(name = "description", length = 255)
  private String description;
}
