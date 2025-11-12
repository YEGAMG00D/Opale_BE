package yegam.opale_be.domain.chat.message.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "ChatMessageListWithRoomResponse DTO", description = "내가 작성한 메시지 + 채팅방 정보 목록 조회 응답 DTO (페이지네이션 포함)")
public class ChatMessageListWithRoomResponseDto {

  @Schema(description = "총 메시지 개수", example = "123")
  private long totalCount;

  @Schema(description = "현재 페이지", example = "1")
  private int currentPage;

  @Schema(description = "페이지당 항목 수", example = "30")
  private int pageSize;

  @Schema(description = "총 페이지 수", example = "5")
  private int totalPages;

  @Schema(description = "다음 페이지 존재 여부", example = "true")
  private boolean hasNext;

  @Schema(description = "이전 페이지 존재 여부", example = "false")
  private boolean hasPrev;

  @Schema(description = "메시지 목록 (채팅방 정보 포함)")
  private List<ChatMessageWithRoomResponseDto> messages;
}
