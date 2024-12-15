package com.isima.dons;

import com.isima.dons.configuration.UserPrincipale;
import com.isima.dons.entities.Message;
import com.isima.dons.entities.User;
import com.isima.dons.services.MessageService;
import com.isima.dons.services.NotificationService;
import com.isima.dons.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class MessageApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @MockBean
    private UserService userService;

    @MockBean
    private NotificationService notificationService;

    @BeforeEach
    public void setup() {
        // Mocking SecurityContextHolder for authentication
        User user = new User(1L, "username", "test@test.com", "password");
        UserPrincipale userPrincipale = new UserPrincipale(user);

        // Mocking the Authentication object
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipale, null);

        // Mocking the SecurityContext and setting it
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetConversations() throws Exception {
        // Mock authentication
        User user = new User(1L, "username", "test@test.com", "password");
        UserPrincipale userPrincipale = new UserPrincipale(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipale, null);

        // Mock the list of conversations
        User user2 = new User(2L, "user2", "user2@test.com", "password");
        List<User> conversations = Arrays.asList(user, user2);

        // Mock the userService behavior
        User mockUser = new User();
        mockUser.setId(1L);
        Mockito.when(userService.getUserById(1L)).thenReturn(mockUser);

        // Mock the messageService behavior
        when(messageService.getConversationsByUserId(1L)).thenReturn(conversations);

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication))
                .andExpect(status().isOk()) // Check if the status is OK (200)
                .andExpect(jsonPath("$.status").value("success")) // Check 'status' field
                .andExpect(jsonPath("$.data.length()").value(2)) // Check the length of the 'data' array
                .andExpect(jsonPath("$.data[0].username").value("username")) // First user in the list
                .andExpect(jsonPath("$.data[1].username").value("user2")); // Second user in the list
    }


    @Test
    void testGetConversation() throws Exception {
        // Mock authentication
        User user = new User(1L, "username", "test@test.com", "password");
        UserPrincipale userPrincipale = new UserPrincipale(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipale, null);

        // Mock data
        Message message1 = new Message();
        message1.setMessage("Hello!");
        message1.setSender(user);
        message1.setReciever(new User(2L, "user2", "user2@test.com", "password"));
        message1.setSentDate(LocalDateTime.now());

        Message message2 = new Message();
        message2.setMessage("Hi!");
        message2.setSender(new User(2L, "user2", "user2@test.com", "password"));
        message2.setReciever(user);
        message2.setSentDate(LocalDateTime.now());

        List<Message> conversation = Arrays.asList(message1, message2);

        // Mock service call
        when(messageService.getConversation(1L, 2L)).thenReturn(conversation);

        // Perform GET request and verify response
        mockMvc.perform(get("/api/messages/{receiverId}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication))
                .andExpect(status().isOk()) // Check if status is OK (200)
                .andExpect(jsonPath("$.status").value("success")) // Check 'status' field
                .andExpect(jsonPath("$.data.length()").value(2)) // Check the length of the 'data' array
                .andExpect(jsonPath("$.data[0].message").value("Hello!")) // Check the first message
                .andExpect(jsonPath("$.data[1].message").value("Hi!")); // Check the second message
    }


    @Test
    @WithMockUser(username = "test", roles = { "USER" })
    void testCreateMessage() throws Exception {
        // Mock the authenticated user
        User user = new User(1L, "username", "test@test.com", "password");
        UserPrincipale userPrincipale = new UserPrincipale(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipale, null);

        // Mock data
        User sender = new User(1L, "user1", "user1@test.com", "password");
        User receiver = new User(2L, "user2", "user2@test.com", "password");

        Message message = new Message();
        message.setMessage("Test message");
        message.setSender(sender);
        message.setReciever(receiver);
        message.setSentDate(LocalDateTime.now());

        // Mock service calls
        when(userService.getUserById(2L)).thenReturn(receiver);
        when(userService.getUserById(1L)).thenReturn(sender);  // Mock sender retrieval
        when(messageService.createMessage(Mockito.any(Message.class))).thenReturn(message);

        // Perform POST request to create a message
        mockMvc.perform(post("/api/messages/{receiverId}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"Test message\"") // Send the message content as a plain string
                        .principal(authentication))
                .andExpect(status().isCreated()) // Expect status 201 (Created)
                .andExpect(jsonPath("$.status").value("success")) // Check 'status' field
                .andExpect(jsonPath("$.data.message").value("\"Test message\"")) // Expect the message with quotes
                .andExpect(jsonPath("$.data.sender.username").value("user1")) // Check sender username
                .andExpect(jsonPath("$.data.reciever.username").value("user2")); // Check receiver username
    }


    @Test
    @WithMockUser(username = "test", roles = { "USER" })
    void testCreateMessage_receiverNotFound() throws Exception {
        User user = new User(1L, "username", "test@test.com", "password");
        UserPrincipale userPrincipale = new UserPrincipale(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipale, null);

        // Mocking receiver not found scenario
        when(userService.getUserById(2L)).thenReturn(null);

        // Perform POST request to create a message
        mockMvc.perform(post("/api/messages/{receiverId}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"msg\": \"Test message\"}") // Send message content as JSON
                        .principal(authentication))
                .andExpect(status().isBadRequest()) // Expect 400 (Bad Request)
                .andExpect(jsonPath("$.status").value("fail")) // Check 'status' field
                .andExpect(jsonPath("$.message").value("Receiver not found")); // Check error message
    }

}
