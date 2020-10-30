package com.webchat.repository;

import com.webchat.model.ChatMessage;
import com.webchat.model.MessageStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author Anna Likhachova
 */

public class ChatPrivateMessagesRepository {

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

	public long countBySenderIdAndRecipientIdAndStatus(String senderId, String recipientId, MessageStatus status){
       return messages.entrySet().stream().filter(c -> c.getValue().getStatus().equals(status)).map(c -> c.getValue().getChatId().equals(
                String.format("%s_%s", senderId, recipientId))).count();
    }
}
