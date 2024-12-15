package com.isima.dons.controllers.api;

import com.isima.dons.configuration.UserPrincipale;
import com.isima.dons.entities.Annonce;
import com.isima.dons.entities.Groupe;
import com.isima.dons.entities.User;
import com.isima.dons.services.GroupeService;
import com.isima.dons.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groupes")
public class GroupeApiController {

    @Autowired
    private GroupeService groupeService;

    @Autowired
    private UserService userService;

    // Get all non-valid groups for the authenticated user
    @GetMapping
    public ResponseEntity<List<Groupe>> getAllNonValideGroupes(Authentication authentication) {
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipale.getId());

        List<Groupe> groupes = groupeService.getGroupeByAcheteurAndNotTaken(user.getId());

        if (!groupes.isEmpty()) {
            return ResponseEntity.ok(groupes);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // No groups found
        }
    }

    // Get all valid groups for the authenticated user
    @GetMapping("/valide")
    public ResponseEntity<List<Groupe>> getAllValideGroupes(Authentication authentication) {
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipale.getId());

        List<Groupe> groupes = groupeService.getGroupeByAcheteurAndTaken(user.getId());

        return ResponseEntity.ok(groupes);
    }

    // Get all annonces in a specific groupe
    @GetMapping("/{groupeId}/annonces")
    public ResponseEntity<List<Annonce>> getAnnoncesInGroupe(@PathVariable Long groupeId) {
        Groupe groupe = groupeService.getGroupeById(groupeId);

        if (groupe != null) {
            return ResponseEntity.ok(groupe.getAnnonces());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Groupe not found
        }
    }

    // Create a new groupe from an annonce
    @PostMapping
    public ResponseEntity<Groupe> createGroupe(@RequestParam("annonceId") Long annonceId,
            Authentication authentication) {
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipale.getId());

        Groupe groupe = groupeService.createGroupe(annonceId, user.getId());

        if (groupe != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(groupe);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // If creation fails
        }
    }

    // Validate a groupe
    @PostMapping("/validate")
    public ResponseEntity<Void> validateGroupe(@RequestParam Long groupeId, Authentication authentication) {
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        Long achteur = userDetails.getId();

        Groupe isValid = groupeService.validateGroupe(groupeId, achteur);

        if (isValid != null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Successfully validated
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Groupe not found
        }
    }

    // Remove an annonce from a groupe
    @DeleteMapping("/{groupeId}/annonces/{annonceId}")
    public ResponseEntity<Void> removeAnnonceFromGroupe(@PathVariable Long groupeId, @PathVariable Long annonceId) {
        boolean isRemoved = groupeService.removeAnnonceFromGroupe(groupeId, annonceId);

        if (isRemoved) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Successfully removed
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Groupe or Annonce not found
        }
    }

    // Delete a Groupe
    @DeleteMapping("/{groupeId}")
    public ResponseEntity<Void> deleteGroupe(@PathVariable Long groupeId, Authentication authentication) {
        // Check if the groupe exists
        Groupe groupe = groupeService.getGroupeById(groupeId);
        if (groupe == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Groupe not found
        }

        // Additional check: Only allow the creator (or admin) to delete the group
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipale.getId());

        if (!groupe.getAcheteur().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Unauthorized to delete
        }

        // Delete the groupe
        groupeService.deleteGroupe(groupeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Groupe deleted successfully
    }

    // Update a Groupe
    @PutMapping("/{groupeId}")
    public ResponseEntity<Groupe> updateGroupe(
            @PathVariable Long groupeId, @RequestBody Groupe updatedGroupe, Authentication authentication) {

        // Check if the groupe exists
        Groupe groupe = groupeService.getGroupeById(groupeId);
        if (groupe == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Groupe not found
        }

        // Additional check: Only allow the creator (or admin) to update the group
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipale.getId());

        if (!groupe.getAcheteur().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Unauthorized to update
        }

        // Update the groupe
        groupe.setPri(updatedGroupe.isPri());
        groupe.setCreationDate(updatedGroupe.getCreationDate());
        groupe.setValidationDate(updatedGroupe.getValidationDate());
        groupe.setAnnonces(updatedGroupe.getAnnonces());
        groupe.setAcheteur(updatedGroupe.getAcheteur());

        groupeService.updateGroupe(groupeId, groupe);

        return ResponseEntity.ok(groupe); // Return the updated groupe
    }
}
