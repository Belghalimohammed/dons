package com.isima.dons.controllers.api;

import com.isima.dons.configuration.UserPrincipale;
import com.isima.dons.entities.Message;
import com.isima.dons.entities.User;
import com.isima.dons.services.MessageService;
import com.isima.dons.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageApiController {

    private final MessageService messageService;
    private final UserService userService;

    @Autowired
    public MessageApiController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    /**
     * Get all conversations for the authenticated user.
     *
     * @param authentication the current authentication
     * @return List of users involved in conversations
     */
    @GetMapping
    public ResponseEntity<List<User>> getConversations(Authentication authentication) {
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        List<User> conversations = messageService.getConversationsByUserId(userDetails.getId());

        if (conversations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(conversations);
    }

    /**
     * Get the conversation between the authenticated user and a specific receiver.
     *
     * @param receiverId     the ID of the receiver
     * @param authentication the current authentication
     * @return List of messages in the conversation
     */
    @GetMapping("/{receiverId}")
    public ResponseEntity<List<Message>> getConversation(
            @PathVariable Long receiverId,
            Authentication authentication) {
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        Long senderId = userDetails.getId();

        List<Message> conversation = messageService.getConversation(senderId, receiverId);

        if (conversation.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(conversation);
    }

    /**
     * Send a message to a specific receiver.
     *
     * @param receiverId     the ID of the receiver
     * @param msg            the message content
     * @param authentication the current authentication
     * @return Response indicating the success of the operation
     */
    @PostMapping("/{receiverId}")
    public ResponseEntity<String> createMessage(
            @PathVariable Long receiverId,
            @RequestParam String msg,
            Authentication authentication) {
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        User receiver = userService.getUserById(receiverId);

        if (receiver == null) {
            return ResponseEntity.badRequest().body("Receiver not found");
        }

        Message message = new Message();
        message.setReciever(receiver);
        message.setSender(userService.getUserById(userDetails.getId()));
        message.setMessage(msg);
        message.setSentDate(LocalDateTime.now());

        messageService.createMessage(message);

        return ResponseEntity.ok("Message sent successfully");
    }
}
