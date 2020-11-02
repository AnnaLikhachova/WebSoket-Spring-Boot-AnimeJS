package com.webchat.event;

import java.util.Optional;

import com.webchat.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * Listener for {@link SessionConnectEvent} and {@link SessionDisconnectEvent}.
 * 
 * @author Anna Likhachova
 */

public class ActiveUserEventListener {

    @Autowired
	private UsersRepository usersRepository;

    @Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	private String loginDestination;
	
	private String logoutDestination;

	/**
	 * Create new {@link UserLoggedInEvent} and
	 * add  new user to the repository.
	 */
	@EventListener
	private void handleSessionConnected(SessionConnectEvent event) {
		SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
		String username = headers.getUser().getName();
		UserLoggedInEvent userLoggedInEvent = new UserLoggedInEvent(username);
		messagingTemplate.convertAndSend(loginDestination, userLoggedInEvent);
		usersRepository.add(headers.getSessionId(), userLoggedInEvent);
	}

	/**
	 * Delete user from the repository.
	 */
	@EventListener
	private void handleSessionDisconnect(SessionDisconnectEvent event) {
		
		Optional.ofNullable(usersRepository.getParticipant(event.getSessionId()))
				.ifPresent(login -> {
					messagingTemplate.convertAndSend(logoutDestination, new UserLoggedOutEvent(login.getUsername()));
					usersRepository.removeParticipant(event.getSessionId());
				});
	}

	public void setLoginDestination(String loginDestination) {
		this.loginDestination = loginDestination;
	}

	public void setLogoutDestination(String logoutDestination) {
		this.logoutDestination = logoutDestination;
	}
}
