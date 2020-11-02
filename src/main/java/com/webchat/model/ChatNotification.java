package com.webchat.model;

/**
 *
 * @author Anna Likhachova
 */

public class ChatNotification {
    private String id;
    private String senderId;
    private String senderName;

    public ChatNotification(String chatId, String senderId, String username) {
        this.id = chatId;
        this.senderId = senderId;
        this.senderName = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
