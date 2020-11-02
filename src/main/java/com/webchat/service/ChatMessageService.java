package com.webchat.service;

import com.webchat.model.ChatMessage;
import com.webchat.model.MessageStatus;
import com.webchat.repository.ChatPrivateMessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Anna Likhachova
 */

@Service
public class ChatMessageService {

    @Autowired
    private ChatPrivateMessagesRepository repository;

    @Autowired
    private ChatRoomService chatRoomService;

    public ChatMessage save(String sessionId, ChatMessage chatMessage) {
        chatMessage.setStatus(MessageStatus.RECEIVED);
        chatMessage.setChatId(sessionId);
        repository.add(sessionId,chatMessage);
        return chatMessage;
    }

    public long countNewMessages(String senderId, String recipientId) {
        return repository.countBySenderIdAndRecipientIdAndStatus(
                senderId, recipientId, MessageStatus.RECEIVED);
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        Optional<String> chatId = chatRoomService.getChatId(senderId, recipientId, false);
        List<ChatMessage> messagesNew = repository.getMessage(chatId.get());
        if(messagesNew.size() > 0) {
            updateStatuses(chatId.get(), MessageStatus.DELIVERED);
        }
        return messagesNew;
    }


    private void updateStatuses(String chatId, MessageStatus status) {
        repository.getMessages().entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(c -> c.iterator().next().getChatId().equals(chatId))
                .peek(c -> c.iterator().next().setStatus(status))
                .collect(Collectors.toList());
    }
}
