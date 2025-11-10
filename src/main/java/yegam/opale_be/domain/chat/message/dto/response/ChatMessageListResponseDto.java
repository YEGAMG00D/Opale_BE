package yegam.opale_be.domain.chat.message.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.*;
import java.time.LocalDateTime;


// 채팅방의 과거 메시지 보여주기용 - 무한 스크롤 용 //


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "ChatMessageListResponse DTO", description = "채팅방 메시지 목록 조회 응답 DTO")
public class ChatMessageListResponseDto {

  @Schema(description = "총 메시지 개수", example = "123")
  private long totalCount;

  @Schema(description = "현재 페이지", example = "1")
  private int currentPage;

  @Schema(description = "페이지당 항목 수", example = "50")
  private int pageSize;

  @Schema(description = "총 페이지 수", example = "5")
  private int totalPages;

  @Schema(description = "다음 페이지 존재 여부", example = "true")
  private boolean hasNext;

  @Schema(description = "이전 페이지 존재 여부", example = "false")
  private boolean hasPrev;

  @Schema(description = "메시지 목록 데이터")
  private List<ChatMessageResponseDto> messages;
}

