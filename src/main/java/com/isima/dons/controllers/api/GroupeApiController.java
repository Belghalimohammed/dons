package com.isima.dons.controllers.api;

import com.isima.dons.configuration.UserPrincipale;
import com.isima.dons.entities.Annonce;
import com.isima.dons.entities.Groupe;
import com.isima.dons.entities.User;
import com.isima.dons.services.GroupeService;
import com.isima.dons.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groupes")
public class GroupeApiController {

    private final GroupeService groupeService;
    private final UserService userService;

    @Autowired
    public GroupeApiController(GroupeService groupeService, UserService userService) {
        this.groupeService = groupeService;
        this.userService = userService;
    }

    // Fetch groupes not yet validated for the authenticated user
    @GetMapping
    public ResponseEntity<?> getAll(Authentication authentication) {
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipale.getId());

        List<Groupe> groupes = groupeService.getGroupeByAcheteurAndNotTaken(user.getId());
        if (groupes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<Annonce> annonces = groupeService.getAnnoncesFromGroupe(user.getId());
        return ResponseEntity.ok(new GroupeResponse(groupes.get(0), annonces));
    }

    // Fetch validated groupes for the authenticated user
    @GetMapping("/valide")
    public ResponseEntity<List<Groupe>> getValidatedGroupes(Authentication authentication) {
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipale.getId());

        List<Groupe> groupes = groupeService.getGroupeByAcheteurAndTaken(user.getId());
        return ResponseEntity.ok(groupes);
    }

    // Create a new groupe
    @PostMapping
    public ResponseEntity<String> createGroupe(
            @RequestParam("annonceId") Long annonceId,
            Authentication authentication) {
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipale.getId());

        groupeService.createGroupe(annonceId, user.getId());
        return ResponseEntity.ok("Groupe created successfully");
    }

    // Validate a groupe
    @PostMapping("/validate")
    public ResponseEntity<String> validateGroupe(@RequestParam("groupeId") Long groupeId) {
        groupeService.validateGroupe(groupeId);
        return ResponseEntity.ok("Groupe validated successfully");
    }

    // Remove an annonce from a groupe
    @DeleteMapping
    public ResponseEntity<String> removeAnnonceFromGroupe(
            @RequestParam("groupeId") Long groupeId,
            @RequestParam("annonceId") Long annonceId) {
        boolean isRemoved = groupeService.removeAnnonceFromGroupe(groupeId, annonceId);
        if (isRemoved) {
            return ResponseEntity.ok("Annonce removed from groupe successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to remove annonce from groupe");
        }
    }

    // Response Wrapper for Groupe and Annonces
    private static class GroupeResponse {
        private final Groupe groupe;
        private final List<Annonce> annonces;

        public GroupeResponse(Groupe groupe, List<Annonce> annonces) {
            this.groupe = groupe;
            this.annonces = annonces;
        }

        public Groupe getGroupe() {
            return groupe;
        }

        public List<Annonce> getAnnonces() {
            return annonces;
        }
    }
}
