package com.webchat.controller;

import com.webchat.event.UserLoggedInEvent;
import com.webchat.exception.BannedUsersException;
import com.webchat.model.ChatMessage;
import com.webchat.model.BannedUser;
import com.webchat.model.ChatNotification;
import com.webchat.repository.ChatMessagesRepository;
import com.webchat.repository.ChatPrivateMessagesRepository;
import com.webchat.repository.UsersRepository;
import com.webchat.service.ChatMessageService;
import com.webchat.service.ChatRoomService;
import com.webchat.util.BannedUserFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.Collection;
import java.util.Optional;

/**
 * Chat Controller
 *
 * @author Anna Likhachova
 */

@Controller
public class ChatController {

    @Autowired
    private BannedUserFilter bannedUserFilter;

    @Autowired
    private BannedUser bannedUser;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ChatMessagesRepository chatMessagesRepository;

    @Autowired
    private ChatPrivateMessagesRepository chatPrivateMessagesRepository;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @SubscribeMapping("/chat.participants")
    @SendTo("/topic/chat.participants")
    public Collection<UserLoggedInEvent> retrieveParticipants() {
        return usersRepository.getActiveSessions().values();
    }

    @SubscribeMapping("/chat.messages")
    @SendTo("/topic/messages")
    public Collection<ChatMessage> retrieveGroupMessages() {
        Collection<ChatMessage> retrieveGroupMessages =chatMessagesRepository.getMessages().values();
        if(retrieveGroupMessages.isEmpty()) return null;
        return retrieveGroupMessages;
    }


    @MessageMapping("/chat.message")
    @SendTo("/topic/messages")
    public ChatMessage filterMessage(@Payload ChatMessage message, Principal principal, @Header("simpSessionId") String sessionId) {
        checkToBan(message);
        chatMessagesRepository.add(sessionId, message);
        return new ChatMessage(principal.getName(), message.getMessage());
    }

    @MessageMapping("/chat/private/{username}")
    public void filterPrivateMessage(@Payload ChatMessage message, @DestinationVariable("username") String username, Principal principal) {
        checkToBan(message);

        Optional<String> chatId = chatRoomService
                .getChatId(message.getSenderId(), message.getRecipientId(), true);
        message.setUsername(principal.getName());
        chatMessageService.save(chatId.get(),message);
       /* simpMessagingTemplate.convertAndSendToUser(
                username,"/queue/reply",
                new ChatNotification(
                        saved.getChatId(),
                        saved.getSenderId(),
                        saved.getUsername()));
*/
        simpMessagingTemplate.convertAndSendToUser(username, "/queue/reply", message);
    }

    private void checkToBan(ChatMessage message) {
        long bannedLevel = bannedUserFilter.getMessageToBan(message.getMessage());
        bannedUser.increment(bannedLevel);
        message.setMessage(bannedUserFilter.filter(message.getMessage()));
    }


    private String returnUserId(String name) {
        return String.valueOf(usersRepository.getSessionIdByName(name));
    }

    @MessageExceptionHandler
    @SendToUser(value = "/queue/reply/errors", broadcast = false)
    public String handleProfanity(BannedUsersException e) {
        return e.getMessage();
    }

    @MessageMapping("/private/{username}/{recipientName}")
    @SendTo("/queue/private")
    public ResponseEntity<?> findChatMessages (@DestinationVariable("username") String username, Principal principal) {
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(returnUserId(principal.getName()), returnUserId(username)));
    }

    @GetMapping("/reply/{username}/{recipientName}/count")
    public ResponseEntity<Long> countNewMessages(
            @PathVariable String username,
            @PathVariable String recipientName) {

        return ResponseEntity
                .ok(chatMessageService.countNewMessages(returnUserId(username), returnUserId(recipientName)));
    }

    @GetMapping("/reply/{id}")
    public ResponseEntity<?> findMessage ( @PathVariable String id) {
        return ResponseEntity
                .ok(chatMessagesRepository.getMessage(id));
    }
}
