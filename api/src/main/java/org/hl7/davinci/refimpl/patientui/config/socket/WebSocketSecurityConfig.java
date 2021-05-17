package org.hl7.davinci.refimpl.patientui.config.socket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

/**
 * The security configuration for the WebSockets. We disable the same-origin policy, disallow sending messages for the
 * clients and require authentication for any other requests.
 *
 * @author Taras Vuyiv
 */
@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

  @Override
  protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
    messages.simpTypeMatchers(SimpMessageType.MESSAGE)
        .denyAll()
        .anyMessage()
        .authenticated();
  }

  @Override
  protected boolean sameOriginDisabled() {
    return true;
  }
}
