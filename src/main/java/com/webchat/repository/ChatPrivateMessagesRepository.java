package com.webchat.repository;

import com.webchat.model.ChatMessage;
import com.webchat.model.MessageStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 
 * @author Anna Likhachova
 */

public class ChatPrivateMessagesRepository {

	private Map<String, List<ChatMessage>> messages = new ConcurrentHashMap<>();

	public void add(String sessionId, ChatMessage event) {
		if(messages.containsKey(sessionId)){
			messages.entrySet().stream().filter(p -> p.getKey() == sessionId)
					.map(p -> p.getValue().add(event))
					.collect(Collectors.toList());

		} else {
			List<ChatMessage> messagesNew = new ArrayList<>();
			messagesNew.add(event);
			messages.put(sessionId, messagesNew );
		}

	}

	public List<ChatMessage> getMessage(String sessionId) {
		return messages.entrySet().stream().filter(p -> p.getKey() == sessionId).flatMap(p -> p.getValue().stream())
				.collect(Collectors.toList());
	}

	public void removeMessage(String sessionId) {
		messages.remove(sessionId);
	}

	public Map<String, List<ChatMessage>> getMessages() {
		return messages;
	}

	public void setMessages(Map<String, List<ChatMessage>> messages) {
		this.messages = messages;
	}

	public long countBySenderIdAndRecipientIdAndStatus(String senderId, String recipientId, MessageStatus status){
		long count = messages.entrySet().stream()
				.filter(p -> p.getKey() == String.format("%s_%s", senderId, recipientId))
				.map(Map.Entry::getValue)
				.filter(c -> c.iterator().next().getStatus().equals(status))
				.count();
		return count;
	}
}
