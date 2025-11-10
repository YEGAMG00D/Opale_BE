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

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

  private final JwtProvider jwtProvider;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

    // WebSocket CONNECT 요청일 때 JWT 인증
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String token = accessor.getFirstNativeHeader("Authorization");

      if (token == null || !token.startsWith("Bearer ")) {
        log.warn("WebSocket CONNECT 요청에 JWT가 없습니다.");
        throw new IllegalArgumentException("Authorization 헤더가 필요합니다.");
      }

      token = token.substring(7).trim(); // 'Bearer ' 제거
      jwtProvider.validateTokenOrThrow(token);
      Long userId = jwtProvider.extractUserIdAsLong(token);

      // 세션에 userId 저장
      accessor.getSessionAttributes().put("userId", userId);
      log.info("WebSocket CONNECT 인증 성공 - userId={}", userId);
    }

    return message;
  }
}
