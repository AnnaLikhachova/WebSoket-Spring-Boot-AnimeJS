package com.webchat.service;

import com.webchat.model.ChatMessage;
import com.webchat.model.MessageStatus;
import com.webchat.repository.ChatMessagesRepository;
import com.webchat.repository.ChatPrivateMessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatMessageService {

    @Autowired
    private ChatPrivateMessagesRepository repository;

    @Autowired
    private ChatRoomService chatRoomService;

    public ChatMessage save(String sessionId, ChatMessage chatMessage) {
        chatMessage.setStatus(MessageStatus.RECEIVED);
        repository.add(sessionId,chatMessage);
        return chatMessage;
    }

    public long countNewMessages(String senderId, String recipientId) {
        return repository.countBySenderIdAndRecipientIdAndStatus(
                senderId, recipientId, MessageStatus.RECEIVED);
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        Optional<String> chatId = chatRoomService.getChatId(senderId, recipientId, false);

        List<ChatMessage> messagesnew = new ArrayList<>();
        messagesnew.stream().map(sId -> repository.getMessage(chatId.get()));

        if(messagesnew.size() > 0) {
            updateStatuses(chatId.get(), MessageStatus.DELIVERED);
        }

        return messagesnew;
    }


    public void updateStatuses(String chatId, MessageStatus status) {
        repository.getMessages().entrySet().stream()
                .filter(c -> c.getValue().getChatId().equals(chatId))
                .peek(c -> c.getValue().setStatus(status))
                .collect(Collectors.toList());
    }
}
