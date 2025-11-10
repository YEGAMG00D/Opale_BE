package yegam.opale_be.domain.chat.room.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "ChatRoomJoinRequest DTO", description = "비공개방 입장 요청 DTO")
public class ChatRoomJoinRequestDto {

  @Schema(description = "비밀번호 (공개방일 경우 생략 가능)", example = "1234")
  private String password;
}
