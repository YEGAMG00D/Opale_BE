package yegam.opale_be.global.websocket;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;


@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final StompHandler stompHandler;

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // 구독 prefix
    registry.enableSimpleBroker("/topic");
    // 송신 prefix
    registry.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
        .setAllowedOriginPatterns("*") // 프론트 도메인 허용
        .withSockJS(); // SockJS fallback
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    // 메시지 수신 전 JWT 인증 인터셉터
    registration.interceptors(stompHandler);
  }


  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new WebSocketUserArgumentResolver());
  }




}