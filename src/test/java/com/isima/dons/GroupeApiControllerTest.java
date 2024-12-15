package com.isima.dons;

import com.isima.dons.configuration.UserPrincipale;
import com.isima.dons.entities.Annonce;
import com.isima.dons.entities.Groupe;
import com.isima.dons.entities.User;
import com.isima.dons.services.GroupeService;
import com.isima.dons.services.JwtService;
import com.isima.dons.services.UserService;
import com.isima.dons.services.implementations.Usersdetailservice;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class GroupeApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupeService groupeService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private Usersdetailservice usersdetailservice;

    @Test
    void testGetAllGroupes_NotValidated() throws Exception {
        // Mock authentication
        User user = new User(1L, "username", "test@test.com", "password");
        UserPrincipale userPrincipale = new UserPrincipale(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipale, null);

        User mockUser = new User();
        mockUser.setId(1L);

        List<Groupe> groupes = List.of(new Groupe()); // Mock list of groupes
        List<Annonce> annonces = List.of(new Annonce()); // Mock list of annonces

        Mockito.when(userService.getUserById(1L)).thenReturn(mockUser);
        Mockito.when(groupeService.getGroupeByAcheteurAndNotTaken(1L)).thenReturn(groupes);
        Mockito.when(groupeService.getAnnoncesFromGroupe(1L)).thenReturn(annonces);

        mockMvc.perform(get("/api/groupes").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success")) // Check for success status
                .andExpect(jsonPath("$.data").isArray()) // Check that 'data' is an array (groupes)
                .andExpect(jsonPath("$.data.length()").value(groupes.size())); // Check the number of groupes in 'data'
    }

    @Test
    void testGetAllGroupes_NoContent() throws Exception {
        User user = new User(1L, "username", "test@test.com", "password");
        UserPrincipale userPrincipale = new UserPrincipale(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipale, null);

        User mockUser = new User();
        mockUser.setId(1L);

        Mockito.when(userService.getUserById(1L)).thenReturn(mockUser);
        Mockito.when(groupeService.getGroupeByAcheteurAndNotTaken(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/groupes").principal(authentication))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetValidatedGroupes() throws Exception {
        User user = new User(1L, "username", "test@test.com", "password");
        UserPrincipale userPrincipale = new UserPrincipale(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipale, null);

        User mockUser = new User();
        mockUser.setId(1L);

        List<Groupe> groupes = List.of(new Groupe()); // Mock list of validated groupes

        Mockito.when(userService.getUserById(1L)).thenReturn(mockUser);
        Mockito.when(groupeService.getGroupeByAcheteurAndTaken(1L)).thenReturn(groupes);

        mockMvc.perform(get("/api/groupes/valide").principal(authentication))
                .andExpect(status().isOk()) // Check that the status is OK (200)
                .andExpect(jsonPath("$.status").value("success")) // Check for 'status' field with value 'success'
                .andExpect(jsonPath("$.data").isArray()) // Check that 'data' is an array
                .andExpect(jsonPath("$.data.length()").value(groupes.size())); // Check the length of 'data' matches the size of groupes
    }


    @Test
    void testCreateGroupe() throws Exception {
        User user = new User(1L, "username", "test@test.com", "password");
        UserPrincipale userPrincipale = new UserPrincipale(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipale, null);

        User mockUser = new User();
        mockUser.setId(1L);

        // Mocking service behavior
        Groupe mockGroupe = new Groupe(); // Mock the created Groupe
        mockGroupe.setId(1L); // Assuming the created group has an ID

        Mockito.when(userService.getUserById(1L)).thenReturn(mockUser);
        Mockito.when(groupeService.createGroupe(Mockito.anyLong(), Mockito.anyLong())).thenReturn(mockGroupe);

        mockMvc.perform(post("/api/groupes")
                        .param("annonceId", "1")
                        .principal(authentication))
                .andExpect(status().isCreated()) // Check for 201 Created status
                .andExpect(jsonPath("$.status").value("success")) // Check for 'status' field with value 'success'
                .andExpect(jsonPath("$.data.id").value(1L)) // Check that the created Groupe has the expected ID
                .andExpect(jsonPath("$.data").exists()); // Ensure 'data' is not null
    }


    @Test
    void testValidateGroupe() throws Exception {
        // Mock the authenticated user
        User mockUser = new User(1L, "username", "test@test.com", "password");
        UserPrincipale userPrincipale = new UserPrincipale(mockUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipale, null);

        // Set the Authentication object in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mock the userService behavior
        Mockito.when(userService.getUserById(1L)).thenReturn(mockUser);

        // Create a mock Groupe object
        Groupe mockGroupe = new Groupe();
        mockGroupe.setId(1L);

        // Mock the groupeService behavior
        Mockito.when(groupeService.validateGroupe(Mockito.anyLong(), Mockito.anyLong())).thenReturn(mockGroupe);

        // Perform the test
        mockMvc.perform(post("/api/groupes/validate")
                        .param("groupeId", "1")
                        .principal(authentication))
                .andExpect(status().isNoContent()) // Check for 204 No Content status
                .andExpect(jsonPath("$.status").value("success")) // Check for 'status' field with value 'success'
                .andExpect(jsonPath("$.message").value("Groupe validated successfully.")); // Check for 'message' field

        // Clear the SecurityContext after the test
        SecurityContextHolder.clearContext();
    }


    @Test
    void testRemoveAnnonceFromGroupe_Success() throws Exception {
        // Mocking the service call to return true (indicating success)
        Mockito.when(groupeService.removeAnnonceFromGroupe(1L, 1L)).thenReturn(true);

        // Perform the DELETE request and verify the response
        mockMvc.perform(delete("/api/groupes/{groupeId}/annonces/{annonceId}", 1L, 1L))
                .andExpect(status().isNoContent()) // Expecting 204 No Content status
                .andExpect(jsonPath("$.status").value("success")) // Check 'status' field
                .andExpect(jsonPath("$.message").value("Annonce removed successfully.")); // Check 'message' field
    }


    @Test
    void testRemoveAnnonceFromGroupe_Failure() throws Exception {
        // Mocking the service call to return false (indicating failure)
        Mockito.when(groupeService.removeAnnonceFromGroupe(1L, 1L)).thenReturn(false);

        // Perform the DELETE request and verify the response
        mockMvc.perform(delete("/api/groupes/{groupeId}/annonces/{annonceId}", 1L, 1L))
                .andExpect(status().isNotFound()) // Expecting 404 Not Found status
                .andExpect(jsonPath("$.status").value("fail")) // Check 'status' field
                .andExpect(jsonPath("$.message").value("Groupe or Annonce not found.")); // Check 'message' field
    }

}
