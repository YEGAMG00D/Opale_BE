package yegam.opale_be.domain.chat.message.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "ChatMessageResponse DTO", description = "채팅 메시지 전송 응답 DTO")
public class ChatMessageResponseDto {

  @Schema(description = "채팅방 ID", example = "1")
  private Long roomId;

  @Schema(description = "보낸 사람 ID", example = "5")
  private Long userId;

  @Schema(description = "보낸 사람 닉네임", example = "emerald-owl")
  private String nickname;

  @Schema(description = "메시지 내용", example = "뮤지컬 위키드 최고였어요!")
  private String message;

  @Schema(description = "메시지 전송 시각", example = "2025-11-10T21:30:00")
  private LocalDateTime sentAt;
}
