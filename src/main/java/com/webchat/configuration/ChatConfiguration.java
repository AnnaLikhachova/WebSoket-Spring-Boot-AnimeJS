package com.webchat.configuration;

import java.io.IOException;

import com.webchat.event.ActiveUserEventListener;
import com.webchat.model.BannedUser;
import com.webchat.model.ChatProperties;
import com.webchat.repository.ChatMessagesRepository;
import com.webchat.repository.ChatPrivateMessagesRepository;
import com.webchat.repository.ChatRoomRepository;
import com.webchat.repository.UsersRepository;
import com.webchat.util.BannedUserFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
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

    @Bean
    public ChatPrivateMessagesRepository chatPrivateMessagesRepository() {
        return new ChatPrivateMessagesRepository();
    }

    @Bean
    public ChatRoomRepository chatRoomRepository() {
        return new ChatRoomRepository();
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

	/*
    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    /**
     * Create  Redis {@link RedisTemplate}.

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }

*/
}
