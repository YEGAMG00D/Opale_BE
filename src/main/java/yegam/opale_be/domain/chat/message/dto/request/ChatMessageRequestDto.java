package yegam.opale_be.domain.chat.message.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import yegam.opale_be.domain.user.entity.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "ChatMessageRequest DTO", description = "채팅 메시지 전송 요청 DTO")
public class ChatMessageRequestDto {

  @Schema(description = "채팅방 ID", example = "1")
  private Long roomId;

  @Schema(description = "보낸 사람 (User 엔티티)")
  private User user;

  @Schema(description = "메시지 내용", example = "오늘 공연 너무 좋았어요!")
  private String message;


}
