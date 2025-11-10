package yegam.opale_be.domain.chat.room.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import yegam.opale_be.domain.chat.message.dto.response.ChatMessageResponseDto;

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

  @Schema(description = "최근 메시지", example = "오늘 공연 최고였어요!")
  private String lastMessage;

  @Schema(description = "최근 메시지 시간")
  private LocalDateTime lastMessageTime;

  @Schema(description = "활성 상태", example = "true")
  private Boolean isActive;

  @Schema(description = "누적 방문자 수", example = "128")
  private Integer visitCount;

  // ChatRoomResponseDto 변환 (기존 유지)
  public static ChatRoomUpdateDto from(ChatRoomResponseDto response) {
    if (response == null) return null;

    return ChatRoomUpdateDto.builder()
        .roomId(response.getRoomId())
        .title(response.getTitle())
        .lastMessage(response.getLastMessage())
        .lastMessageTime(response.getLastMessageTime())
        .isActive(response.getIsActive())
        .build();
  }

  // ChatMessageResponseDto 변환도 허용
  public static ChatRoomUpdateDto from(ChatMessageResponseDto messageResponse) {
    if (messageResponse == null) return null;

    return ChatRoomUpdateDto.builder()
        .roomId(messageResponse.getRoomId())
        .title(null)
        .lastMessage(messageResponse.getMessage())
        .lastMessageTime(messageResponse.getSentAt())
        .isActive(true)
        .build();
  }
}
