package com.webchat.repository;

import com.webchat.event.UserLoggedInEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author Anna Likhachova
 */
public class UsersRepository {

	private Map<String, UserLoggedInEvent> activeSessions = new ConcurrentHashMap<>();

	public void add(String sessionId, UserLoggedInEvent event) {
		activeSessions.put(sessionId, event);
	}

	public UserLoggedInEvent getParticipant(String sessionId) {
		return activeSessions.get(sessionId);
	}

	public void removeParticipant(String sessionId) {
		activeSessions.remove(sessionId);
	}

	public Map<String, UserLoggedInEvent> getActiveSessions() {
		return activeSessions;
	}

	public void setActiveSessions(Map<String, UserLoggedInEvent> activeSessions) {
		this.activeSessions = activeSessions;
	}
}
