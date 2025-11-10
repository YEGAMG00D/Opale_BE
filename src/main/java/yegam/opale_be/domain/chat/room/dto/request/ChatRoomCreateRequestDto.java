package yegam.opale_be.domain.chat.room.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import yegam.opale_be.domain.chat.room.entity.RoomType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "ChatRoomCreateRequest DTO", description = "채팅방 생성 요청 DTO")
public class ChatRoomCreateRequestDto {

  @Schema(description = "채팅방 이름", example = "뮤지컬 위키드 실시간 소감방")
  @NotBlank
  private String title;

  @Schema(description = "채팅방 설명", example = "위키드 관람객들과 공연 소감 나누기")
  private String description;

  @Schema(description = "방 타입 (PERFORMANCE_PUBLIC / PERFORMANCE_GROUP / PRIVATE_DM)", example = "PERFORMANCE_PUBLIC")
  @NotNull
  private RoomType roomType;

  @Schema(description = "공연 ID (공연 연관 채팅방일 경우 필수)", example = "1001")
  private String performanceId;

  @Schema(description = "썸네일 이미지 URL", example = "https://example.com/poster.jpg")
  private String thumbnailUrl;

  @Schema(description = "비공개 여부", example = "false")
  private Boolean isPublic;

  @Schema(description = "비공개 비밀번호 (공개방이면 null)", example = "1234")
  private String password;

  @Schema(description = "개설자 ID", example = "1")
  @NotNull
  private Long creatorId;
}
