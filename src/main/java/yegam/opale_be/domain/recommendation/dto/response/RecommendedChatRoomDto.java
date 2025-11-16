package yegam.opale_be.domain.recommendation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "RecommendedChatRoom DTO", description = "추천된 채팅방 데이터 DTO (기본 채팅방 DTO와 구조 통일)")
public class RecommendedChatRoomDto {

  @Schema(description = "채팅방 ID")
  private Long roomId;

  @Schema(description = "채팅방 제목")
  private String title;

  @Schema(description = "썸네일 URL")
  private String thumbnailUrl;

  @Schema(description = "최근 메시지")
  private String lastMessage;

  @Schema(description = "최근 메시지 시간")
  private LocalDateTime lastMessageTime;

  @Schema(description = "채팅방 방문수 (null → 0)")
  private Long visitCount;

  @Schema(description = "활성 여부")
  private Boolean isActive;

  @Schema(description = "공연명")
  private String performanceTitle;

  @Schema(description = "공연 ID")
  private String performanceId;
}
