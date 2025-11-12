package yegam.opale_be.domain.chat.room.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "ChatRoomListResponse DTO", description = "채팅방 목록 조회 응답 DTO")
public class ChatRoomListResponseDto {

  @Schema(description = "총 채팅방 개수", example = "42")
  private long totalCount;

  @Schema(description = "채팅방 목록 데이터")
  private List<ChatRoomResponseDto> rooms;
}
