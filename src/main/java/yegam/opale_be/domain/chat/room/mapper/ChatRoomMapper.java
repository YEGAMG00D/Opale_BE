package yegam.opale_be.domain.chat.room.mapper;

import org.springframework.stereotype.Component;
import yegam.opale_be.domain.chat.room.dto.request.ChatRoomCreateRequestDto;
import yegam.opale_be.domain.chat.room.dto.response.ChatRoomListResponseDto;
import yegam.opale_be.domain.chat.room.dto.response.ChatRoomResponseDto;
import yegam.opale_be.domain.chat.room.dto.response.ChatRoomUpdateDto;
import yegam.opale_be.domain.chat.room.entity.ChatRoom;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChatRoomMapper {

  /** 생성 요청 DTO → 엔티티 변환 */
  public ChatRoom toEntity(ChatRoomCreateRequestDto dto, Performance performance, User creator) {
    return ChatRoom.builder()
        .title(dto.getTitle())
        .description(dto.getDescription())
        .roomType(dto.getRoomType())
        .performance(performance)
        .creator(creator)
        .thumbnailUrl(dto.getThumbnailUrl())
        .isPublic(dto.getIsPublic() != null ? dto.getIsPublic() : true)
        .password(dto.getPassword())
        .visitCount(0)
        .lastMessage(null)
        .lastMessageTime(null)
        .isActive(false)
        .build();
  }

  /** 엔티티 → 응답 DTO */
  public ChatRoomResponseDto toResponseDto(ChatRoom room) {
    return ChatRoomResponseDto.builder()
        .roomId(room.getRoomId())
        .title(room.getTitle())
        .description(room.getDescription())
        .performanceId(room.getPerformance() != null ? room.getPerformance().getPerformanceId() : null)
        .performanceTitle(room.getPerformance() != null ? room.getPerformance().getTitle() : null)
        .roomType(room.getRoomType().name())
        .isPublic(room.getIsPublic())
        .thumbnailUrl(room.getThumbnailUrl())
        .visitCount(room.getVisitCount())
        .lastMessage(room.getLastMessage())
        .lastMessageTime(room.getLastMessageTime())
        .isActive(room.getIsActive())
        .creatorNickname(room.getCreator() != null ? room.getCreator().getNickname() : null)
        .build();
  }

  /** 목록 변환 */
  public ChatRoomListResponseDto toListResponseDto(List<ChatRoom> rooms) {
    List<ChatRoomResponseDto> list = rooms.stream()
        .map(this::toResponseDto)
        .collect(Collectors.toList());

    return ChatRoomListResponseDto.builder()
        .totalCount(list.size())
        .rooms(list)
        .build();
  }

  /** 실시간 업데이트 DTO 변환 (방 입장 / 메시지 갱신 broadcast 용) */
  public ChatRoomUpdateDto toUpdateDto(ChatRoom room) {
    return ChatRoomUpdateDto.builder()
        .roomId(room.getRoomId())
        .title(room.getTitle())
        .visitCount(room.getVisitCount())
        .lastMessage(room.getLastMessage())
        .lastMessageTime(room.getLastMessageTime())
        .isActive(room.getIsActive())
        .build();
  }
}
