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

        List<Groupe> groupes = List.of(new Groupe());
        List<Annonce> annonces = List.of(new Annonce());

        Mockito.when(userService.getUserById(1L)).thenReturn(mockUser);
        Mockito.when(groupeService.getGroupeByAcheteurAndNotTaken(1L)).thenReturn(groupes);
        Mockito.when(groupeService.getAnnoncesFromGroupe(1L)).thenReturn(annonces);

        mockMvc.perform(get("/api/groupes").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupe").exists())
                .andExpect(jsonPath("$.annonces").isArray());
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

        List<Groupe> groupes = List.of(new Groupe());

        Mockito.when(userService.getUserById(1L)).thenReturn(mockUser);
        Mockito.when(groupeService.getGroupeByAcheteurAndTaken(1L)).thenReturn(groupes);

        mockMvc.perform(get("/api/groupes/valide").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void testCreateGroupe() throws Exception {
        User user = new User(1L, "username", "test@test.com", "password");
        UserPrincipale userPrincipale = new UserPrincipale(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipale, null);

        User mockUser = new User();
        mockUser.setId(1L);

        Mockito.when(userService.getUserById(1L)).thenReturn(mockUser);

        mockMvc.perform(post("/api/groupes")
                .param("annonceId", "1")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().string("Groupe created successfully"));
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
                .andExpect(status().isOk())
                .andExpect(content().string("Groupe validated successfully"));

        // Clear the SecurityContext after the test
        SecurityContextHolder.clearContext();
    }

    @Test
    void testRemoveAnnonceFromGroupe_Success() throws Exception {
        Mockito.when(groupeService.removeAnnonceFromGroupe(1L, 1L)).thenReturn(true);

        mockMvc.perform(delete("/api/groupes")
                .param("groupeId", "1")
                .param("annonceId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Annonce removed from groupe successfully"));
    }

    @Test
    void testRemoveAnnonceFromGroupe_Failure() throws Exception {
        Mockito.when(groupeService.removeAnnonceFromGroupe(1L, 1L)).thenReturn(false);

        mockMvc.perform(delete("/api/groupes")
                .param("groupeId", "1")
                .param("annonceId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Failed to remove annonce from groupe"));
    }
}
