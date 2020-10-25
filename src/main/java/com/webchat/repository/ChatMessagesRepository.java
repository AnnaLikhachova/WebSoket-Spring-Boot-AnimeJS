package com.webchat.repository;

import com.webchat.model.ChatMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author Anna Likhachova
 */
public class ChatMessagesRepository {

	private Map<String, ChatMessage> messages = new ConcurrentHashMap<>();

	public void add(String sessionId, ChatMessage event) {
		messages.put(sessionId, event);
	}

	public ChatMessage getMessage(String sessionId) {
		return messages.get(sessionId);
	}

	public void removeMessage(String sessionId) {
		messages.remove(sessionId);
	}

	public Map<String, ChatMessage> getMessages() {
		return messages;
	}

	public void setMessages(Map<String, ChatMessage> messages) {
		this.messages = messages;
	}
}
