package com.webchat.service;

import com.webchat.model.ChatRoom;
import com.webchat.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatRoomService {

    @Autowired
    @Qualifier("chatRoomRepository")
    private ChatRoomRepository chatRoomRepository;

    public Optional<String> getChatId(
            String senderId, String recipientId, boolean createIfNotExist) {

        String chatId = String.format("%s_%s", senderId, recipientId);
       return chatRoomRepository
                .getChatRoom(chatId)
                .map(ChatRoom::getChatId)
                .map(Optional::of)
                .orElseGet(() ->
                        {
                    if(!createIfNotExist) {
                        return  Optional.empty();
                    }


                    ChatRoom senderRecipient = ChatRoom
                            .builder()
                            .chatId(chatId)
                            .senderId(senderId)
                            .recipientId(recipientId)
                            .build();

                    ChatRoom recipientSender = ChatRoom
                            .builder()
                            .chatId(chatId)
                            .senderId(recipientId)
                            .recipientId(senderId)
                            .build();
                    chatRoomRepository.add(chatId, senderRecipient);
                    chatRoomRepository.add(chatId, recipientSender);

                    return Optional.of(chatId);
                });
    }
}
