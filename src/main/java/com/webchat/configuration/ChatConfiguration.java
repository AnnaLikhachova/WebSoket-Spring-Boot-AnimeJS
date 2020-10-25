package com.webchat.configuration;

import java.io.IOException;

import com.webchat.event.ActiveUserEventListener;
import com.webchat.model.BannedUser;
import com.webchat.model.ChatProperties;
import com.webchat.repository.ChatMessagesRepository;
import com.webchat.repository.UsersRepository;
import com.webchat.util.BannedUserFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import redis.embedded.RedisServer;

/**
 * Configuration for {@link ChatProperties}
 *
 * @author Anna Likhachova
 */

@Configuration
@EnableConfigurationProperties(ChatProperties.class)
public class ChatConfiguration {

	@Autowired
	private ChatProperties chatProperties;


    /**
     * Set the {@link ActiveUserEventListener} for login and logout events.
     */
	@Bean
	public ActiveUserEventListener presenceEventListener(SimpMessagingTemplate messagingTemplate) {
		ActiveUserEventListener presence = new ActiveUserEventListener(messagingTemplate, participantRepository());
		presence.setLoginDestination(chatProperties.getDestinations().getLogin());
		presence.setLogoutDestination(chatProperties.getDestinations().getLogout());
		return presence;
	}

	@Bean
	public UsersRepository participantRepository() {
		return new UsersRepository();
	}

	@Bean
	public ChatMessagesRepository chatMessagesRepository() {
		return new ChatMessagesRepository();
	}

    /**
     * Create {@link BannedUser} with maximal allowed level for disallowed words.
     */
	@Bean
	@Scope(value = "websocket", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public BannedUser sessionProfanity() {
		return new BannedUser(chatProperties.getMaxProfanityLevel());
	}

    /**
     * Create {@link BannedUserFilter} to filter disallowed words.
     */
	@Bean
	public BannedUserFilter bannedFilter() {
		BannedUserFilter checker = new BannedUserFilter();
		checker.setBannedUser(chatProperties.getDisallowedWords());
		return checker;
	}

    /**
     * Create embedded Redis {@link RedisServer} used by Spring Session.
     */
	@Bean(initMethod = "start", destroyMethod = "stop")
	public RedisServer redisServer(@Value("${redis.embedded.port}") int port)  throws IOException {
		return new RedisServer(port);
	}
}
