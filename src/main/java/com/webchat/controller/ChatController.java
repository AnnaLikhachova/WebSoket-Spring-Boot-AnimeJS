package com.webchat.controller;

import com.webchat.event.UserLoggedInEvent;
import com.webchat.exception.BannedUsersException;
import com.webchat.exception.NoSessionIdException;
import com.webchat.model.ChatNotification;
import com.webchat.model.JqueryResponseBody;
import com.webchat.model.ChatMessage;
import com.webchat.model.BannedUser;
import com.webchat.repository.ChatMessagesRepository;
import com.webchat.repository.ChatPrivateMessagesRepository;
import com.webchat.repository.UsersRepository;
import com.webchat.service.ChatMessageService;
import com.webchat.service.ChatRoomService;
import com.webchat.util.BannedUserFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @MessageMapping("/chat.message")
    public void filterMessage(@Payload ChatMessage message, Principal principal, @Header("simpSessionId") String sessionId) {
        checkToBan(message);
        message.setUsername(principal.getName());
        message.setSenderId(sessionId);
        message.setTime();
        chatMessagesRepository.add(sessionId, message);
        simpMessagingTemplate.convertAndSend("/topic/messages", chatMessagesRepository.getMessages());
    }

    @MessageMapping("/chat/private/{username}")
    public void filterPrivateMessage(@Payload ChatMessage message, @DestinationVariable("username") String username, Principal principal, @Header("simpSessionId") String sessionId) {
        checkToBan(message);
        message.setUsername(principal.getName());
        message.setSenderId(sessionId);
        message.setRecipientName(username);
        message.setRecipientId(returnUserId(username));
        message.setTime();

        String chatId = chatRoomService
                .getChatId(message.getSenderId(), message.getRecipientId(), true).get();
        message.setChatId(chatId);
        chatMessageService.save(chatId,message);

        simpMessagingTemplate.convertAndSendToUser(username,"/queue/notification",
                new ChatNotification(
                        message.getChatId(),
                        message.getSenderId(),
                        message.getUsername()));
        simpMessagingTemplate.convertAndSendToUser(principal.getName(), "/queue/reply", chatMessageService.addMessageToList(chatMessageService.findChatMessages(returnUserId(principal.getName()), returnUserId(username)),chatMessageService.findChatMessages(returnUserId(username), returnUserId(principal.getName()))));

        simpMessagingTemplate.convertAndSendToUser(username, "/queue/reply", chatMessageService.addMessageToList(chatMessageService.findChatMessages(returnUserId(principal.getName()), returnUserId(username)),chatMessageService.findChatMessages(returnUserId(username), returnUserId(principal.getName()))));
    }

    @SubscribeMapping("/chat.private.messages/{username}")
    @SendToUser("/queue/reply")
    public Collection<ChatMessage> findChatMessages (@DestinationVariable("username") String username, Principal principal) {
        return chatMessageService.findChatMessages(returnUserId(principal.getName()), returnUserId(username));
    }

    private void checkToBan(ChatMessage message) {
        long bannedLevel = bannedUserFilter.getMessageToBan(message.getMessage());
        bannedUser.increment(bannedLevel);
        message.setMessage(bannedUserFilter.filter(message.getMessage()));
    }

    private String returnUserId(String name) {
       Optional<String> object = usersRepository.getSessionIdByName(name);
       if(object.isPresent()) return object.get();
       throw new NoSessionIdException("No user with name "+name+" in the repository");
    }

    @MessageExceptionHandler
    @SendToUser(value = "/queue/reply/errors", broadcast = false)
    public String handleProfanity(BannedUsersException e) {
        return e.getMessage();
    }

    @PostMapping(value = "/name-unique")
    public ResponseEntity findName (@RequestBody String username, Errors errors) {
        JqueryResponseBody result = new JqueryResponseBody();
        if (errors.hasErrors()) {
            result.setMsg(errors.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage())
                    .collect(Collectors.joining(",")));

            return ResponseEntity.badRequest().body(result);

        }

        String exists = String.valueOf(usersRepository.isNameUniqe(username));
        result.setResult(exists);
        return  ResponseEntity.ok(result);
    }

}
