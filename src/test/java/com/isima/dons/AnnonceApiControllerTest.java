package com.isima.dons;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isima.dons.configuration.UserPrincipale;
import com.isima.dons.entities.Annonce;
import com.isima.dons.entities.User;
import com.isima.dons.services.AnnonceService;
import com.isima.dons.services.JwtService;
import com.isima.dons.services.UserService;
import com.isima.dons.services.implementations.Usersdetailservice;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AnnonceApiControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AnnonceService annonceService;

        @MockBean
        private UserService userService;

        @MockBean // Add this line to mock JwtService
        private JwtService jwtService;

        @MockBean // Mock Usersdetailservice here
        private Usersdetailservice usersdetailservice;

        @Test
        @WithMockUser(username = "test", roles = { "USER" })
        void testGetAllAnnonces() throws Exception {
                // Mock data
                Annonce annonce1 = new Annonce();
                annonce1.setId(1L);
                annonce1.setTitre("Annonce 1");

                Annonce annonce2 = new Annonce();
                annonce2.setId(2L);
                annonce2.setTitre("Annonce 2");

                List<Annonce> annonces = Arrays.asList(annonce1, annonce2);

                // Mock service call
                when(annonceService.getAllAnnonces()).thenReturn(annonces);

                // Perform GET request and verify response
                mockMvc.perform(get("/api/annonces")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(2)) // Check the length of the list
                                .andExpect(jsonPath("[0].titre").value("Annonce 1")) // First item in the list
                                .andExpect(jsonPath("[1].titre").value("Annonce 2")); // Second item in the list
        }

        @Test
        @WithMockUser(username = "test", roles = { "USER" })
        void testGetAnnonceById() throws Exception {
                // Mock data
                Annonce annonce = new Annonce();
                annonce.setId(1L);
                annonce.setTitre("Annonce 1");

                // Mock service call
                when(annonceService.getAnnonceById(1L)).thenReturn(annonce);

                // Perform GET request and verify response
                mockMvc.perform(get("/api/annonces/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.titre").value("Annonce 1"));
        }

        @BeforeEach
        public void setup() {
                // Mock de SecurityContextHolder pour l'authentification
                User user = new User(1L, "username", "test@test.com", "password");
                UserPrincipale userPrincipale = new UserPrincipale(user);
                Authentication authentication = Mockito.mock(Authentication.class);
                SecurityContext securityContext = Mockito.mock(SecurityContext.class);

                Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
                Mockito.when(authentication.getPrincipal()).thenReturn(userPrincipale);

                SecurityContextHolder.setContext(securityContext);
        }

        @Test
        void testCreateAnnonce() throws Exception {
                // Mocking a user
                User user = new User(1L, "username", "test@test.com", "password");
                UserPrincipale userPrincipale = new UserPrincipale(user);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipale, null);

                // Mocking dependencies
                Annonce annonce = new Annonce();
                annonce.setTitre("Titre de test");
                annonce.setDescription("Description de test");
                annonce.setEtatObjet(Annonce.EtatObjet.Neuf);
                annonce.setKeywords(Arrays.asList("test", "annonce"));

                // Mock the service call
                Mockito.when(annonceService.createAnnonce(Mockito.any(Annonce.class), Mockito.eq(user.getId())))
                                .thenReturn(annonce);

                // Perform the test
                mockMvc.perform(post("/api/annonces")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(annonce)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.titre").value("Titre de test"))
                                .andExpect(jsonPath("$.description").value("Description de test"))
                                .andExpect(jsonPath("$.etatObjet").value("Neuf"))
                                .andExpect(jsonPath("$.keywords[0]").value("test"))
                                .andExpect(jsonPath("$.keywords[1]").value("annonce"));
        }

        @Test
        void updateAnnonce_shouldReturnUpdatedAnnonce() throws Exception {
                Long annonceId = 1L;

                // Existing Annonce mock
                Annonce existingAnnonce = new Annonce();
                existingAnnonce.setId(annonceId);
                existingAnnonce.setDatePublication(LocalDate.now());
                existingAnnonce.setVendeur(new User());
                existingAnnonce.setKeywords(List.of("existing", "keywords"));

                // Updated Annonce mock
                Annonce updatedAnnonce = new Annonce();
                updatedAnnonce.setTitre("Updated Title");
                updatedAnnonce.setDescription("Updated Description");
                updatedAnnonce.setKeywords(List.of("updated", "keywords"));

                // Mocking service behavior
                Mockito.when(annonceService.getAnnonceById(annonceId)).thenReturn(existingAnnonce);
                Mockito.when(annonceService.updateAnnonce(Mockito.eq(annonceId), Mockito.any(Annonce.class)))
                                .thenReturn(updatedAnnonce);

                // Request body for the update
                String updatedAnnonceJson = "{" +
                                "\"titre\": \"Updated Title\"," +
                                "\"description\": \"Updated Description\"," +
                                "\"keywords\": [\"updated keywords\"]" +
                                "}";

                mockMvc.perform(MockMvcRequestBuilders.put("/api/annonces/{id}", annonceId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatedAnnonceJson))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.titre").value("Updated Title"))
                                .andExpect(jsonPath("$.description").value("Updated Description"))
                                .andExpect(jsonPath("$.keywords[0]").value("updated"))
                                .andExpect(jsonPath("$.keywords[1]").value("keywords"));

                // Verify service calls
                Mockito.verify(annonceService).getAnnonceById(annonceId);
                Mockito.verify(annonceService).updateAnnonce(Mockito.eq(annonceId), Mockito.any(Annonce.class));
        }

}