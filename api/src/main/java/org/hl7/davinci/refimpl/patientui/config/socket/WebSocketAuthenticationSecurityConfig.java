package org.hl7.davinci.refimpl.patientui.config.socket;

import lombok.RequiredArgsConstructor;
import org.hl7.davinci.refimpl.patientui.security.jwt.JwtAuthenticationProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Provides the {@link org.springframework.security.authentication.UsernamePasswordAuthenticationToken} for every
 * message containing the Authorization native header. This interceptor must have precedence over Spring Security
 * filters, so we declare it with the highest order.
 *
 * @author Taras Vuyiv
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
public class WebSocketAuthenticationSecurityConfig implements WebSocketMessageBrokerConfigurer {

  private final JwtAuthenticationProvider authenticationProvider;

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(new ChannelInterceptor() {
      @Override
      public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
          String authorizationHeader = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
          if (authorizationHeader != null) {
            accessor.setUser(authenticationProvider.getAuthentication(authorizationHeader));
          }
        }
        return message;
      }
    });
  }
}
