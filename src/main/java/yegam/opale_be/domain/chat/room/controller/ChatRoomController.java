package yegam.opale_be.domain.chat.room.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import yegam.opale_be.domain.chat.room.dto.request.ChatRoomCreateRequestDto;
import yegam.opale_be.domain.chat.room.dto.request.ChatRoomJoinRequestDto;
import yegam.opale_be.domain.chat.room.dto.response.ChatRoomListResponseDto;
import yegam.opale_be.domain.chat.room.dto.response.ChatRoomResponseDto;
import yegam.opale_be.domain.chat.room.dto.response.ChatRoomUpdateDto;
import yegam.opale_be.domain.chat.room.service.ChatRoomService;
import yegam.opale_be.global.response.BaseResponse;
import yegam.opale_be.global.exception.CustomException;
import yegam.opale_be.global.exception.GlobalErrorCode;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
@Tag(name = "ChatRoom", description = "채팅방 관련 API")
public class ChatRoomController {

  private final ChatRoomService chatRoomService;
  private final SimpMessagingTemplate messagingTemplate;

  /**  채팅방 생성 (로그인 필요) */
  @Operation(
      summary = "채팅방 생성",
      description = "로그인한 사용자가 새로운 채팅방을 생성합니다. (공연방 / 단체방 / 개인방 포함)"
  )
  @PostMapping
  public ResponseEntity<BaseResponse<ChatRoomResponseDto>> createRoom(
      @AuthenticationPrincipal Long userId,
      @RequestBody @Valid ChatRoomCreateRequestDto dto
  ) {
    // 인증되지 않은 사용자 차단
    if (userId == null) {
      throw new CustomException(GlobalErrorCode.UNAUTHORIZED);
    }

    ChatRoomResponseDto response = chatRoomService.createRoom(userId, dto);
    return ResponseEntity.ok(BaseResponse.success("채팅방 생성 성공", response));
  }

  /** 채팅방 목록 조회 (roomType / performanceId 기반 필터링 가능) */
  @Operation(
      summary = "채팅방 목록 조회",
      description = "roomType, performanceId를 기준으로 채팅방 목록을 조회합니다."
  )
  @GetMapping
  public ResponseEntity<BaseResponse<ChatRoomListResponseDto>> getChatRooms(
      @RequestParam(required = false) String roomType,
      @RequestParam(required = false) String performanceId
  ) {
    ChatRoomListResponseDto response = chatRoomService.getChatRooms(roomType, performanceId);
    return ResponseEntity.ok(BaseResponse.success("채팅방 목록 조회 성공", response));
  }

  /** 단일 채팅방 조회 */
  @Operation(summary = "채팅방 상세 조회", description = "roomId를 통해 채팅방 상세 정보를 조회합니다.")
  @GetMapping("/{roomId}")
  public ResponseEntity<BaseResponse<ChatRoomResponseDto>> getChatRoom(
      @PathVariable Long roomId
  ) {
    ChatRoomResponseDto response = chatRoomService.getChatRoom(roomId);
    return ResponseEntity.ok(BaseResponse.success("채팅방 상세 조회 성공", response));
  }

  /** 채팅방 삭제 (로그인 필요) */
  @Operation(summary = "채팅방 삭제", description = "로그인 사용자가 자신이 개설한 채팅방을 삭제합니다.")
  @DeleteMapping("/{roomId}")
  public ResponseEntity<BaseResponse<Void>> deleteChatRoom(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long roomId
  ) {
    if (userId == null) {
      throw new CustomException(GlobalErrorCode.UNAUTHORIZED);
    }

    chatRoomService.deleteChatRoom(userId, roomId);
    return ResponseEntity.ok(BaseResponse.success("채팅방 삭제 성공", null));
  }


  /** 비공개방 입장 (비밀번호 검증 포함) */
  @Operation(summary = "비공개방 입장", description = "비밀번호를 검증하고 입장 성공 시 방 정보를 반환합니다.")
  @PostMapping("/{roomId}/join")
  public ResponseEntity<BaseResponse<ChatRoomResponseDto>> joinPrivateRoom(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long roomId,
      @RequestBody(required = false) ChatRoomJoinRequestDto dto
  ) {
    if (userId == null) throw new CustomException(GlobalErrorCode.UNAUTHORIZED);

    ChatRoomResponseDto response = chatRoomService.joinRoom(userId, roomId, dto);

    // 🟢 실시간으로 입장자 수 갱신 브로드캐스트
    messagingTemplate.convertAndSend("/topic/rooms", ChatRoomUpdateDto.from(response));

    return ResponseEntity.ok(BaseResponse.success("채팅방 입장 성공", response));
  }



}
