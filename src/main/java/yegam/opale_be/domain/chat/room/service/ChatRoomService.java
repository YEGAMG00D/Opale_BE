package yegam.opale_be.domain.chat.room.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.chat.room.dto.request.ChatRoomCreateRequestDto;
import yegam.opale_be.domain.chat.room.dto.request.ChatRoomJoinRequestDto;
import yegam.opale_be.domain.chat.room.dto.response.*;
import yegam.opale_be.domain.chat.room.entity.ChatRoom;
import yegam.opale_be.domain.chat.room.entity.RoomType;
import yegam.opale_be.domain.chat.room.exception.ChatRoomErrorCode;
import yegam.opale_be.domain.chat.room.mapper.ChatRoomMapper;
import yegam.opale_be.domain.chat.room.repository.ChatRoomRepository;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.culture.performance.repository.PerformanceRepository;
import yegam.opale_be.domain.user.entity.User;
import yegam.opale_be.domain.user.repository.UserRepository;
import yegam.opale_be.global.exception.CustomException;
import yegam.opale_be.global.exception.GlobalErrorCode;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;
  private final ChatRoomMapper chatRoomMapper;
  private final PerformanceRepository performanceRepository;
  private final UserRepository userRepository;
  private final SimpMessagingTemplate messagingTemplate;

  /* ============================================================
      1. 채팅방 생성
     ============================================================ */
  @Transactional
  public ChatRoomResponseDto createRoom(Long userId, ChatRoomCreateRequestDto dto) {
    Long creatorId = (dto.getCreatorId() != null && dto.getCreatorId() == -1) ? 2L : userId;

    User creator = userRepository.findById(creatorId)
        .orElseThrow(() -> new CustomException(ChatRoomErrorCode.CREATOR_NOT_FOUND));

    Performance performance = null;
    if (dto.getPerformanceId() != null) {
      performance = performanceRepository.findById(dto.getPerformanceId()).orElse(null);
    }

    ChatRoom room = chatRoomMapper.toEntity(dto, performance, creator);
    chatRoomRepository.save(room);

    return chatRoomMapper.toResponseDto(room);
  }

  /* ============================================================
      2. 채팅방 목록 조회
     ============================================================ */
  public ChatRoomListResponseDto getChatRooms(String roomTypeStr, String performanceId) {
    List<ChatRoom> rooms;

    if (roomTypeStr != null && !roomTypeStr.isBlank()) {
      RoomType roomType = RoomType.valueOf(roomTypeStr);

      if (performanceId != null && !performanceId.isBlank()) {
        rooms = chatRoomRepository.findByRoomTypeAndPerformance_PerformanceId(roomType, performanceId);
      } else {
        rooms = chatRoomRepository.findByRoomType(roomType);
      }
    } else {
      rooms = chatRoomRepository.findAll();
    }

    return chatRoomMapper.toListResponseDto(rooms);
  }

  /* ============================================================
      3. 단일 채팅방 조회
     ============================================================ */
  public ChatRoomResponseDto getChatRoom(Long roomId) {
    ChatRoom room = chatRoomRepository.findById(roomId)
        .orElseThrow(() -> new CustomException(ChatRoomErrorCode.ROOM_NOT_FOUND));
    return chatRoomMapper.toResponseDto(room);
  }

  /* ============================================================
      4. 채팅방 삭제 (개설자만 가능)
     ============================================================ */
  @Transactional
  public void deleteChatRoom(Long userId, Long roomId) {
    if (userId == null) throw new CustomException(GlobalErrorCode.UNAUTHORIZED);

    ChatRoom room = chatRoomRepository.findById(roomId)
        .orElseThrow(() -> new CustomException(ChatRoomErrorCode.ROOM_NOT_FOUND));

    if (!room.getCreator().getUserId().equals(userId)) {
      throw new CustomException(GlobalErrorCode.FORBIDDEN);
    }

    chatRoomRepository.delete(room);
    log.info("채팅방 삭제 완료 - roomId={}, deletedBy={}", roomId, userId);

    // broadcast (활성 false로 갱신)
    ChatRoomUpdateDto updateDto = chatRoomMapper.toUpdateDto(room);
    updateDto.setIsActive(false);
    messagingTemplate.convertAndSend("/topic/rooms", updateDto);
  }

  /* ============================================================
      5. 비공개방 입장 (비밀번호 검증 + 방문자 수 증가)
     ============================================================ */
  @Transactional
  public ChatRoomResponseDto joinRoom(Long userId, Long roomId, ChatRoomJoinRequestDto dto) {
    if (userId == null) throw new CustomException(GlobalErrorCode.UNAUTHORIZED);

    ChatRoom room = chatRoomRepository.findById(roomId)
        .orElseThrow(() -> new CustomException(ChatRoomErrorCode.ROOM_NOT_FOUND));

    if (!room.getIsPublic()) {
      String inputPassword = (dto != null && dto.getPassword() != null) ? dto.getPassword() : "";
      if (room.getPassword() == null || !room.getPassword().equals(inputPassword)) {
        throw new CustomException(ChatRoomErrorCode.INVALID_ROOM_PASSWORD);
      }
    }

    room.setVisitCount(room.getVisitCount() + 1);
    room.setIsActive(true);

    log.info("사용자 {} 채팅방 입장 완료 - roomId={}, totalVisits={}", userId, roomId, room.getVisitCount());

    ChatRoomUpdateDto updateDto = chatRoomMapper.toUpdateDto(room);
    messagingTemplate.convertAndSend("/topic/rooms", updateDto);

    return chatRoomMapper.toResponseDto(room);
  }
}
