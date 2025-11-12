package yegam.opale_be.global.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import yegam.opale_be.global.jwt.JwtProvider;

/**
 * ✅ StompHandler (2025-11-11 수정 완료)
 * - 로그인 사용자: JWT 검증 후 userId 세션에 저장
 * - 비로그인 게스트: 인증 없이 CONNECT 허용 (읽기 전용)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

  private final JwtProvider jwtProvider;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

    // ✅ WebSocket CONNECT 시점에만 JWT 검사 수행
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String token = accessor.getFirstNativeHeader("Authorization");

      // ✅ 로그인 안 된 사용자 (게스트)
      if (token == null || token.isBlank()) {
        log.info("👤 비로그인 사용자 WebSocket CONNECT - 게스트 모드");
        accessor.getSessionAttributes().put("guest", true);
        return message; // ❗ 예외 던지지 않고 통과시킴
      }

      // ✅ JWT 토큰 앞의 'Bearer ' 제거
      if (token.startsWith("Bearer ")) {
        token = token.substring(7).trim();
      }

      try {
        // ✅ 유효성 검증 (유효하지 않으면 CustomException 발생)
        jwtProvider.validateTokenOrThrow(token);

        // ✅ 사용자 ID 추출 후 세션 저장
        Long userId = jwtProvider.extractUserIdAsLong(token);
        accessor.getSessionAttributes().put("userId", userId);
        log.info("✅ WebSocket CONNECT 인증 성공 - userId={}", userId);

      } catch (Exception e) {
        // ✅ 비정상 토큰은 게스트로 간주
        log.warn("⚠️ WebSocket JWT 검증 실패: {}", e.getMessage());
        accessor.getSessionAttributes().put("guest", true);
      }
    }

    return message;
  }
}
