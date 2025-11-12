package yegam.opale_be.global.websocket;

import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.stereotype.Component;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Component
public class WebSocketUserArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(AuthenticationPrincipal.class)
        && parameter.getParameterType().equals(Long.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, Message<?> message) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
    if (accessor.getUser() == null) return null;
    return Long.valueOf(accessor.getUser().getName());
  }
}
