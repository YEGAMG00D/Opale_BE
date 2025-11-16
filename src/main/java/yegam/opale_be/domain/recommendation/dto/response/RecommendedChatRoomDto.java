package yegam.opale_be.domain.recommendation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "RecommendedChatRoom DTO", description = "추천된 채팅방 데이터 DTO")
public class RecommendedChatRoomDto {

  @Schema(description = "채팅방 ID", example = "42")
  private Long roomId;

  @Schema(description = "채팅방 제목", example = "레미제라블 관람후기방")
  private String title;

  @Schema(description = "최근 메시지 내용", example = "오늘 공연 너무 좋았어요!")
  private String lastMessage;

  @Schema(description = "최근 메시지 시간")
  private LocalDateTime lastMessageTime;

  @Schema(description = "채팅방 방문수", example = "3412")
  private Long visitCount;
}
