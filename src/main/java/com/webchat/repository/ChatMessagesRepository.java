package com.webchat.repository;

import com.webchat.model.ChatMessage;
import java.util.*;

/**
 * 
 * @author Anna Likhachova
 */

public class ChatMessagesRepository {

    private List<ChatMessage> messages = Collections.synchronizedList(new ArrayList<ChatMessage>());

    public void add(String sessionId, ChatMessage event) {
        messages.add(event);
    }

    public List<ChatMessage>  getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage>  messages) {
        this.messages = messages;
    }

}
