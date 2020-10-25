package com.webchat.controller;

import com.webchat.event.UserLoggedInEvent;
import com.webchat.exception.BannedUsersException;
import com.webchat.model.ChatMessage;
import com.webchat.model.BannedUser;
import com.webchat.repository.ChatMessagesRepository;
import com.webchat.repository.UsersRepository;
import com.webchat.util.BannedUserFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import java.security.Principal;
import java.util.Collection;

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
    private SimpMessagingTemplate simpMessagingTemplate;

    @SubscribeMapping("/chat.participants")
    public Collection<UserLoggedInEvent> retrieveParticipants() {
        return usersRepository.getActiveSessions().values();
    }

    @MessageMapping("/chat.message")
    @SendTo("/topic/messages")
    public ChatMessage filterMessage(@Payload ChatMessage message, Principal principal) {
        checkToBan(message);
        return new ChatMessage(principal.getName(), message.getMessage());
    }

    @MessageMapping("/chat/private/{username}")
    public void filterPrivateMessage(@Payload ChatMessage message, @DestinationVariable("username") String username, Principal principal) {
        checkToBan(message);
        message.setUsername(principal.getName());
        simpMessagingTemplate.convertAndSendToUser(username, "/queue/reply", message);
    }

    private void checkToBan(ChatMessage message) {
        long bannedLevel = bannedUserFilter.getMessageToBan(message.getMessage());
        bannedUser.increment(bannedLevel);
        message.setMessage(bannedUserFilter.filter(message.getMessage()));
    }

    @MessageExceptionHandler
    @SendToUser(value = "/queue/reply/errors", broadcast = false)
    public String handleProfanity(BannedUsersException e) {
        return e.getMessage();
    }
}
