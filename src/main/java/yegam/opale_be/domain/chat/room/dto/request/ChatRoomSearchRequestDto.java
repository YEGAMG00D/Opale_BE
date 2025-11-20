package yegam.opale_be.domain.chat.room.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "ChatRoomSearchRequest DTO", description = "채팅방 검색 요청 DTO")
public class ChatRoomSearchRequestDto {

  @Schema(description = "채팅방 타입 (PERFORMANCE_PUBLIC, PERFORMANCE_GROUP, PRIVATE_DM 등)", example = "PERFORMANCE_PUBLIC")
  private String roomType;

  @Schema(description = "공연 ID", example = "PF123456")
  private String performanceId;

  @Schema(description = "검색어 (채팅방 제목 검색)", example = "위키드")
  private String keyword;
}
