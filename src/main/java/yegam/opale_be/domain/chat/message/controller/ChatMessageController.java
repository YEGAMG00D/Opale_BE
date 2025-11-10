package yegam.opale_be.domain.chat.message.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import yegam.opale_be.domain.chat.message.dto.request.ChatMessageRequestDto;
import yegam.opale_be.domain.chat.message.dto.response.*;
import yegam.opale_be.domain.chat.message.service.ChatMessageService;
import yegam.opale_be.domain.chat.room.dto.response.ChatRoomUpdateDto;
import yegam.opale_be.global.exception.CustomException;
import yegam.opale_be.global.exception.GlobalErrorCode;
import yegam.opale_be.global.response.BaseResponse;

@RestController
@RequestMapping("/api/chat/messages")
@RequiredArgsConstructor
@Tag(name = "ChatMessage", description = "채팅 메시지 관련 API")
public class ChatMessageController {

  private final ChatMessageService chatMessageService;
  private final SimpMessagingTemplate messagingTemplate;

  /** 과거 메시지 조회 */
  @Operation(summary = "채팅방 과거 메시지 조회", description = "특정 채팅방의 과거 메시지를 페이지 단위로 조회합니다.")
  @GetMapping("/{roomId}")
  public ResponseEntity<BaseResponse<ChatMessageListResponseDto>> getMessages(
      @PathVariable Long roomId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "50") int size
  ) {
    ChatMessageListResponseDto response = chatMessageService.getMessages(roomId, page, size);
    return ResponseEntity.ok(BaseResponse.success("메시지 목록 조회 성공", response));
  }

  /** 내가 작성한 메시지 목록 */
  @Operation(summary = "내 메시지 목록 조회", description = "로그인 사용자가 작성한 메시지들을 채팅방 정보와 함께 조회합니다.")
  @GetMapping("/my")
  public ResponseEntity<BaseResponse<ChatMessageListWithRoomResponseDto>> getMyMessages(
      @AuthenticationPrincipal Long userId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "30") int size
  ) {
    ChatMessageListWithRoomResponseDto response = chatMessageService.getMyMessages(userId, page, size);
    return ResponseEntity.ok(BaseResponse.success("내 메시지 목록 조회 성공", response));
  }

  /** REST 테스트용 메시지 전송 */
  @Operation(summary = "채팅 메시지 전송 (REST)", description = "테스트용 메시지 전송 API")
  @PostMapping
  public ResponseEntity<BaseResponse<ChatMessageResponseDto>> sendMessage(
      @AuthenticationPrincipal Long userId,
      @RequestBody ChatMessageRequestDto dto
  ) {
    ChatMessageResponseDto response = chatMessageService.saveMessage(userId, dto);
    return ResponseEntity.ok(BaseResponse.success("메시지 전송 성공", response));
  }

  /** WebSocket 메시지 전송 */
  @MessageMapping("/chat/send")
  public void sendMessageWebSocket(ChatMessageRequestDto request, SimpMessageHeaderAccessor accessor) {
    Long userId = (Long) accessor.getSessionAttributes().get("userId");
    if (userId == null) {
      throw new CustomException(GlobalErrorCode.UNAUTHORIZED);
    }

    ChatMessageResponseDto response = chatMessageService.saveMessage(userId, request);
    messagingTemplate.convertAndSend("/topic/rooms/" + response.getRoomId(), response);
    messagingTemplate.convertAndSend("/topic/rooms", ChatRoomUpdateDto.from(response));
  }
}
