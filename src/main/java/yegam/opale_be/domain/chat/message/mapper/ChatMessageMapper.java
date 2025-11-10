package yegam.opale_be.domain.chat.message.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import yegam.opale_be.domain.chat.message.dto.response.*;
import yegam.opale_be.domain.chat.message.entity.ChatMessage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChatMessageMapper {

  /** 단일 메시지 변환 */
  public ChatMessageResponseDto toResponseDto(ChatMessage m) {
    return ChatMessageResponseDto.builder()
        .roomId(m.getChatRoom().getRoomId())
        .userId(m.getUser().getUserId())
        .nickname(m.getUser().getNickname())
        .message(m.getContents())
        .sentAt(m.getSentAt())
        .build();
  }

  /** 채팅방 + 메시지 함께 변환 */
  public ChatMessageWithRoomResponseDto toWithRoomDto(ChatMessage m) {
    return ChatMessageWithRoomResponseDto.builder()
        .messageId(m.getMessageId())
        .message(m.getContents())
        .sentAt(m.getSentAt())
        .roomId(m.getChatRoom().getRoomId())
        .roomTitle(m.getChatRoom().getTitle())
        .roomType(m.getChatRoom().getRoomType().name())
        .performanceTitle(m.getChatRoom().getPerformance() != null
            ? m.getChatRoom().getPerformance().getTitle() : null)
        .build();
  }

  /** 페이지 변환 (DESC → ASC로 뒤집기) */
  public ChatMessageListResponseDto toPagedResponseDto(Page<ChatMessage> page) {
    List<ChatMessageResponseDto> list = page.getContent().stream()
        .map(this::toResponseDto)
        .collect(Collectors.toList());

    Collections.reverse(list);
    return ChatMessageListResponseDto.builder()
        .totalCount(page.getTotalElements())
        .currentPage(page.getNumber() + 1)
        .pageSize(page.getSize())
        .totalPages(page.getTotalPages())
        .hasNext(page.hasNext())
        .hasPrev(page.hasPrevious())
        .messages(list)
        .build();
  }
}
