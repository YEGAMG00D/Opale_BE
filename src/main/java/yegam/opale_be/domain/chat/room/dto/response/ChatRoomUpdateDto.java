package yegam.opale_be.domain.chat.room.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import yegam.opale_be.domain.chat.room.entity.RoomType;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "ChatRoomUpdate DTO", description = "채팅방 목록 실시간 갱신용 DTO")
public class ChatRoomUpdateDto {

  @Schema(description = "채팅방 ID", example = "1")
  private Long roomId;

  @Schema(description = "채팅방 제목", example = "위키드 실시간 톡방")
  private String title;

  @Schema(description = "방 타입", example = "PERFORMANCE_PUBLIC")
  private RoomType roomType;

  @Schema(description = "최근 메시지", example = "오늘 공연 최고였어요!")
  private String lastMessage;

  @Schema(description = "최근 메시지 시간")
  private LocalDateTime lastMessageTime;

  @Schema(description = "활성 상태", example = "true")
  private Boolean isActive;

  @Schema(description = "누적 방문자 수", example = "128")
  private Long visitCount;
}
