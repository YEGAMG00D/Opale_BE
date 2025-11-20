package yegam.opale_be.domain.chat.room.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "ChatRoomExistenceResponse DTO", description = "공연별 오픈 채팅방 존재 여부 + 정보 응답 DTO")
public class ChatRoomExistenceResponseDto {

  @Schema(description = "오픈 채팅방 존재 여부", example = "true")
  private boolean exists;

  @Schema(description = "오픈 채팅방 정보 (없으면 null)")
  private ChatRoomResponseDto room;
}
