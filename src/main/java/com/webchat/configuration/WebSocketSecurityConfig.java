package com.webchat.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

/**
 * Security configuration for broker and app destinations.
 *
 * @author Anna Likhachova
 *
 * @since 1.0
 * @see AbstractSecurityWebSocketMessageBrokerConfigurer
 */

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    /**
     * Set the {@link MessageSecurityMetadataSourceRegistry} to deny access
     * for not authenticated users.
     */
	@Override
	protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
		messages
		 		.simpMessageDestMatchers("/topic/chat.login", "/topic/chat.logout", "/topic/chat.message").denyAll()
				.anyMessage().authenticated();
	}


    /**
     * Disable CSRF for webSockets.
     */
	@Override
	protected boolean sameOriginDisabled() {
		return true;
	}
}