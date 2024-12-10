package com.isima.dons.controllers.web;

import com.isima.dons.configuration.UserPrincipale;
import com.isima.dons.entities.Message;
import com.isima.dons.entities.User;
import com.isima.dons.services.MessageService;
import com.isima.dons.services.UserService;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/messages")
public class MessageWebController {

    @Autowired
    private MessageService messageService;

    private final UserService userService;

    public MessageWebController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @GetMapping
    public String showConversations(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();

        List<User> conversations = messageService.getConversationsByUserId(userDetails.getId());
        for (User user : conversations) {
            System.out.println("---" + user.getUsername());
        }
        model.addAttribute("conversations", conversations);
        model.addAttribute("content", "pages/messages/conversations");
        return "home";
    }

    @GetMapping("/{receiverId}")
    public String showCreateForm(@PathVariable Long receiverId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        Long senderId = userDetails.getId();

        // Get the conversation between the sender and receiver
        List<Message> conversation = messageService.getConversation(senderId, receiverId);
        User receiver = userService.getUserById(receiverId);
        model.addAttribute("message", new Message());
        model.addAttribute("receiver", receiver); // Pass the receiver ID to the view
        model.addAttribute("conversation", conversation); // Pass the conversation to the view
        model.addAttribute("content", "pages/messages/create");
        return "home";
    }

    @PostMapping("/{receiverId}")
    public String createMessage(@PathVariable Long receiverId, @RequestParam String msg) {
        System.out.println("Receiver ID: " + receiverId);

        // Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();

        // Fetch the receiver user
        User receiver = userService.getUserById(receiverId);

        // Create and save the message
        Message message = new Message();
        message.setReciever(receiver);
        message.setSender(userService.getUserById(userDetails.getId()));
        message.setMessage(msg);
        message.setSentDate(LocalDateTime.now());
        messageService.createMessage(message);

        // Redirect to the message creation page for the same receiverId
        return "redirect:/messages/" + receiverId;
    }

}
