package com.isima.dons.controllers.api;

import com.isima.dons.entities.Message;
import com.isima.dons.entities.User;
import com.isima.dons.services.MessageService;
import com.isima.dons.services.UserService;
import com.isima.dons.configuration.UserPrincipale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageApiController {

    @Autowired
    private MessageService messageService;

    private final UserService userService;

    public MessageApiController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    // Get the list of conversations for the authenticated user
    @GetMapping
    public ResponseEntity<List<User>> getConversations(Authentication authentication) {
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        List<User> conversations = messageService.getConversationsByUserId(userPrincipale.getId());

        if (!conversations.isEmpty()) {
            return ResponseEntity.ok(conversations);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // No conversations found
        }
    }

    // Get the conversation between the authenticated user and the receiver
    @GetMapping("/{receiverId}")
    public ResponseEntity<List<Message>> getConversation(
            @PathVariable Long receiverId, Authentication authentication) {

        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        Long senderId = userDetails.getId();

        // Fetch the conversation between sender and receiver
        List<Message> conversation = messageService.getConversation(senderId, receiverId);

        if (!conversation.isEmpty()) {
            return ResponseEntity.ok(conversation);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // No conversation found
        }
    }

    // Create a new message
    @PostMapping("/{receiverId}")
    public ResponseEntity<Message> createMessage(
            @PathVariable Long receiverId, @RequestBody String msg, Authentication authentication) {

        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        User sender = userService.getUserById(userDetails.getId());
        User receiver = userService.getUserById(receiverId);

        // Create and save the message
        Message message = new Message();
        message.setSender(sender);
        message.setReciever(receiver);
        message.setMessage(msg);
        message.setSentDate(LocalDateTime.now());
        messageService.createMessage(message);

        return ResponseEntity.status(HttpStatus.CREATED).body(message); // Return the created message
    }

    // Delete a message
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId, Authentication authentication) {
        // Check if the message exists
        Message message = messageService.getMessageById(messageId);
        if (message == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Message not found
        }

        // Only allow the sender or receiver to delete the message
        Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipale userDetails = (UserPrincipale) authentication1.getPrincipal();
        Long userId = userDetails.getId();

        if (!message.getSender().getId().equals(userId) && !message.getReciever().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // User is not authorized to delete
        }

        // Delete the message
        messageService.deleteMessage(messageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Message deleted successfully
    }

    // Update a message
    @PutMapping("/{messageId}")
    public ResponseEntity<Message> updateMessage(
            @PathVariable Long messageId, @RequestBody String newMessageContent, Authentication authentication) {

        // Check if the message exists
        Message message = messageService.getMessageById(messageId);
        if (message == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Message not found
        }

        // Only allow the sender to update the message
        Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipale userDetails = (UserPrincipale) authentication1.getPrincipal();
        Long userId = userDetails.getId();

        if (!message.getSender().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // User is not authorized to update
        }

        // Update the message content
        message.setMessage(newMessageContent);
        message.setSentDate(LocalDateTime.now()); // Optionally update the sent date

        // Save the updated message
        messageService.createMessage(message);

        return ResponseEntity.ok(message); // Return the updated message
    }
}
