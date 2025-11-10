package yegam.opale_be.domain.chat.message.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.chat.message.dto.request.ChatMessageRequestDto;
import yegam.opale_be.domain.chat.message.dto.response.*;
import yegam.opale_be.domain.chat.message.entity.ChatMessage;
import yegam.opale_be.domain.chat.message.exception.ChatMessageErrorCode;
import yegam.opale_be.domain.chat.message.mapper.ChatMessageMapper;
import yegam.opale_be.domain.chat.message.repository.ChatMessageRepository;
import yegam.opale_be.domain.chat.room.entity.ChatRoom;
import yegam.opale_be.domain.chat.room.repository.ChatRoomRepository;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.domain.user.repository.UserRepository;
import yegam.opale_be.global.exception.CustomException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {

  private final ChatMessageRepository chatMessageRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final UserRepository userRepository;
  private final ChatMessageMapper chatMessageMapper;

  /* ============================================================
      메시지 저장
   ============================================================ */
  @Transactional
  public ChatMessageResponseDto saveMessage(Long userId, ChatMessageRequestDto dto) {
    if (userId == null) throw new CustomException(ChatMessageErrorCode.UNAUTHORIZED);

    ChatRoom room = chatRoomRepository.findById(dto.getRoomId())
        .orElseThrow(() -> new CustomException(ChatMessageErrorCode.CHAT_ROOM_NOT_FOUND));

    User sender = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ChatMessageErrorCode.USER_NOT_FOUND));

    ChatMessage message = ChatMessage.builder()
        .chatRoom(room)
        .user(sender)
        .contents(dto.getMessage())
        .sentAt(LocalDateTime.now())
        .isDeleted(false)
        .build();

    chatMessageRepository.save(message);

    // 방 최신 상태 갱신
    room.setLastMessage(dto.getMessage());
    room.setLastMessageTime(LocalDateTime.now());
    room.setIsActive(true);

    return chatMessageMapper.toResponseDto(message);
  }

  /* ============================================================
      채팅방별 과거 메시지 조회 (정렬 ASC)
   ============================================================ */
  public ChatMessageListResponseDto getMessages(Long roomId, int page, int size) {
    ChatRoom room = chatRoomRepository.findById(roomId)
        .orElseThrow(() -> new CustomException(ChatMessageErrorCode.CHAT_ROOM_NOT_FOUND));

    Pageable pageable = PageRequest.of(
        Math.max(page - 1, 0),
        size,
        Sort.by(Sort.Direction.ASC, "sentAt")
    );

    Page<ChatMessage> messagePage = chatMessageRepository.findByChatRoom_RoomId(roomId, pageable);
    return chatMessageMapper.toPagedResponseDto(messagePage);
  }

  /* ============================================================
      내가 작성한 메시지 목록 조회 (채팅방 정보 포함)
   ============================================================ */
  public ChatMessageListWithRoomResponseDto getMyMessages(Long userId, int page, int size) {
    if (userId == null) throw new CustomException(ChatMessageErrorCode.UNAUTHORIZED);

    Pageable pageable = PageRequest.of(
        Math.max(page - 1, 0),
        size,
        Sort.by(Sort.Direction.DESC, "sentAt")
    );

    Page<ChatMessage> pageResult = chatMessageRepository.findByUser_UserId(userId, pageable);
    List<ChatMessageWithRoomResponseDto> messages = pageResult.getContent().stream()
        .map(chatMessageMapper::toWithRoomDto)
        .collect(Collectors.toList());

    return ChatMessageListWithRoomResponseDto.builder()
        .totalCount(pageResult.getTotalElements())
        .currentPage(pageResult.getNumber() + 1)
        .pageSize(pageResult.getSize())
        .totalPages(pageResult.getTotalPages())
        .hasNext(pageResult.hasNext())
        .hasPrev(pageResult.hasPrevious())
        .messages(messages)
        .build();
  }
}
