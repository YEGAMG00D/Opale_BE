package yegam.opale_be.domain.chat.message.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

// 내가 보낸 채팅 메시지 조회용 //

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "ChatMessageWithRoomResponse DTO", description = "내가 작성한 채팅 메시지 + 채팅방 정보 응답 DTO")
public class ChatMessageWithRoomResponseDto {

  @Schema(description = "메시지 ID", example = "501")
  private Long messageId;

  @Schema(description = "채팅 내용", example = "오늘 공연 너무 좋았어요!")
  private String message;

  @Schema(description = "메시지 전송 시각", example = "2025-11-10T21:30:00")
  private LocalDateTime sentAt;

  @Schema(description = "채팅방 ID", example = "12")
  private Long roomId;

  @Schema(description = "채팅방 이름", example = "뮤지컬 위키드 실시간 소감방")
  private String roomTitle;

  @Schema(description = "채팅방 타입", example = "PERFORMANCE_PUBLIC")
  private String roomType;

  @Schema(description = "공연명 (공연방일 경우)", example = "Wicked")
  private String performanceTitle;
}
