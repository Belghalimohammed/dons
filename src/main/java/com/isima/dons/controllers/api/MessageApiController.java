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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> getConversations(Authentication authentication) {
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        List<User> conversations = messageService.getConversationsByUserId(userPrincipale.getId());

        Map<String, Object> response = new HashMap<>();
        if (!conversations.isEmpty()) {
            response.put("status", "success");
            response.put("data", conversations);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "fail");
            response.put("message", "No conversations found.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }
    }

    // Get the conversation between the authenticated user and the receiver
    @GetMapping("/{receiverId}")
    public ResponseEntity<Map<String, Object>> getConversation(
            @PathVariable Long receiverId, Authentication authentication) {

        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        Long senderId = userDetails.getId();

        // Fetch the conversation between sender and receiver
        List<Message> conversation = messageService.getConversation(senderId, receiverId);

        Map<String, Object> response = new HashMap<>();
        if (!conversation.isEmpty()) {
            response.put("status", "success");
            response.put("data", conversation);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "fail");
            response.put("message", "No conversation found.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }
    }

    // Create a new message
    @PostMapping("/{receiverId}")
    public ResponseEntity<Map<String, Object>> createMessage(
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

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", message);
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // Return the created message
    }

    // Delete a message
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Map<String, Object>> deleteMessage(@PathVariable Long messageId,
            Authentication authentication) {
        // Check if the message exists
        Message message = messageService.getMessageById(messageId);
        Map<String, Object> response = new HashMap<>();

        if (message == null) {
            response.put("status", "fail");
            response.put("message", "Message not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Only allow the sender or receiver to delete the message
        Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipale userDetails = (UserPrincipale) authentication1.getPrincipal();
        Long userId = userDetails.getId();

        if (!message.getSender().getId().equals(userId) && !message.getReciever().getId().equals(userId)) {
            response.put("status", "fail");
            response.put("message", "User is not authorized to delete this message.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); // User is not authorized
        }

        // Delete the message
        messageService.deleteMessage(messageId);
        response.put("status", "success");
        response.put("message", "Message deleted successfully.");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response); // Message deleted successfully
    }

    // Update a message
    @PutMapping("/{messageId}")
    public ResponseEntity<Map<String, Object>> updateMessage(
            @PathVariable Long messageId, @RequestBody String newMessageContent, Authentication authentication) {

        // Check if the message exists
        Message message = messageService.getMessageById(messageId);
        Map<String, Object> response = new HashMap<>();

        if (message == null) {
            response.put("status", "fail");
            response.put("message", "Message not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // Message not found
        }

        // Only allow the sender to update the message
        Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipale userDetails = (UserPrincipale) authentication1.getPrincipal();
        Long userId = userDetails.getId();

        if (!message.getSender().getId().equals(userId)) {
            response.put("status", "fail");
            response.put("message", "User is not authorized to update this message.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); // User is not authorized
        }

        // Update the message content
        message.setMessage(newMessageContent);
        message.setSentDate(LocalDateTime.now()); // Optionally update the sent date

        // Save the updated message
        messageService.createMessage(message);

        response.put("status", "success");
        response.put("data", message);
        return ResponseEntity.ok(response); // Return the updated message
    }
}
