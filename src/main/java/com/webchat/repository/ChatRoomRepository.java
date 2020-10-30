package com.webchat.repository;

import com.webchat.model.ChatRoom;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Anna Likhachova
 */

public class ChatRoomRepository {

    private Map<String, ChatRoom> chatRoom = new ConcurrentHashMap<>();

    public void add(String sessionId, ChatRoom event) {
        chatRoom.put(sessionId, event);
    }

    public Optional<ChatRoom> getChatRoom(String sessionId) {
        return Optional.ofNullable(chatRoom.get(sessionId));
    }

    public void removeChatRoom(String sessionId) {
        chatRoom.remove(sessionId);
    }

    public Map<String, ChatRoom> getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(Map<String, ChatRoom> chatRoom) {
        this.chatRoom = chatRoom;
    }


}
