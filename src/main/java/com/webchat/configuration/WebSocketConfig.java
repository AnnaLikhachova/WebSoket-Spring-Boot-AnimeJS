package com.webchat.configuration;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration for broker and application destinations. Setting of the endpoint for Stomp Client.
 *
 * @author Anna Likhachova
 */

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Set the {@link MessageBrokerRegistry} to configure path for
     * application and broker.
     */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic", "/queue");
		config.setApplicationDestinationPrefixes("/app", "/user");
        config.setUserDestinationPrefix("/user");
	}

    /**
     * Set the {@link StompEndpointRegistry} to configure endpoint for
     * Stomp client.
     */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/gs-guide-websocket").withSockJS();
	}

    /**
     * Create a new {@link TomcatServletWebServerFactory} instance.
     */
	@Bean
	ServletWebServerFactory servletWebServerFactory(){
		return new TomcatServletWebServerFactory();
	}

}
