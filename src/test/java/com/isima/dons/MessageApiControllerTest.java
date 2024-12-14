package com.isima.dons;

import com.isima.dons.configuration.UserPrincipale;
import com.isima.dons.entities.Annonce;
import com.isima.dons.entities.Groupe;
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
        User user = new User(1L, "username", "test@test.com","password");
        UserPrincipale userPrincipale = new UserPrincipale(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipale, null);

        User user2 = new User(2L, "user2", "user2@test.com", "password");
        List<User> conversations = Arrays.asList(user, user2);
        User mockUser = new User();
        mockUser.setId(1L);

        List<Groupe> groupes = List.of(new Groupe());
        List<Annonce> annonces = List.of(new Annonce());

        Mockito.when(userService.getUserById(1L)).thenReturn(mockUser);

        when(messageService.getConversationsByUserId(1L)).thenReturn(conversations);

        mockMvc.perform(get("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON).principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))  // Check the length of the list
                .andExpect(jsonPath("[0].username").value("username"))  // First user in the list
                .andExpect(jsonPath("[1].username").value("user2"));

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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))  // Check the length of the conversation
                .andExpect(jsonPath("[0].message").value("Hello!"))
                .andExpect(jsonPath("[1].message").value("Hi!"));
    }



    @Test
    @WithMockUser(username = "test", roles = {"USER"})
    void testCreateMessage() throws Exception {
        User user = new User(1L, "username", "test@test.com","password");
        UserPrincipale userPrincipale = new UserPrincipale(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipale, null);
        // Mock data
        // Mock data
        Message message = new Message();
        message.setMessage("Test message");
        message.setSender(new User(1L, "user1", "user1@test.com", "password"));
        message.setReciever(new User(2L, "user2", "user2@test.com", "password"));
        message.setSentDate(LocalDateTime.now());

        // Mock service call
        when(userService.getUserById(2L)).thenReturn(new User(2L, "user2", "user2@test.com", "password"));
        when(messageService.createMessage(Mockito.any(Message.class))).thenReturn(message);

        // Perform POST request to create a message
        mockMvc.perform(post("/api/messages/{receiverId}", 2L)
                        .param("msg", "Test message").principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Message sent successfully"));
    }

    @Test
    @WithMockUser(username = "test", roles = {"USER"})
    void testCreateMessage_receiverNotFound() throws Exception {
        User user = new User(1L, "username", "test@test.com","password");
        UserPrincipale userPrincipale = new UserPrincipale(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipale, null);
        // Mock data
        // Mocking receiver not found scenario
        when(userService.getUserById(2L)).thenReturn(null);

        // Perform POST request to create a message
        mockMvc.perform(post("/api/messages/{receiverId}", 2L)
                        .param("msg", "Test message").principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Receiver not found"));
    }
}
