package yegam.opale_be.global.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
public class WebSocketEventListener {

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    log.info("WebSocket connected: {}", headerAccessor.getSessionId());
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    log.info("WebSocket disconnected: {}", headerAccessor.getSessionId());
  }
}
