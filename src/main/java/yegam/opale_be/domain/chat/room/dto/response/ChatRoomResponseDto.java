package yegam.opale_be.domain.chat.room.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import yegam.opale_be.domain.chat.room.entity.RoomType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "ChatRoomResponse DTO", description = "채팅방 상세 응답 DTO")
public class ChatRoomResponseDto {

  @Schema(description = "채팅방 ID", example = "1")
  private Long roomId;

  @Schema(description = "채팅방 이름", example = "뮤지컬 위키드 실시간 소감방")
  private String title;

  @Schema(description = "채팅방 설명", example = "위키드 관람객들과 공연 소감 나누기")
  private String description;

  @Schema(description = "공연 ID (공연 연관 방일 경우)", example = "1001")
  private String performanceId;

  @Schema(description = "공연명", example = "Wicked")
  private String performanceTitle;

  @Schema(description = "방 타입", example = "PERFORMANCE_PUBLIC")
  private RoomType roomType;

  @Schema(description = "공개 여부", example = "true")
  private Boolean isPublic;

  @Schema(description = "썸네일 URL", example = "https://example.com/poster.jpg")
  private String thumbnailUrl;

  @Schema(description = "누적 방문자 수", example = "153")
  private Long visitCount;

  @Schema(description = "최근 메시지", example = "오늘 공연 정말 멋졌어요!")
  private String lastMessage;

  @Schema(description = "최근 메시지 시각")
  private LocalDateTime lastMessageTime;

  @Schema(description = "활성 여부", example = "true")
  private Boolean isActive;

  @Schema(description = "개설자 닉네임", example = "opale_user01")
  private String creatorNickname;
}
