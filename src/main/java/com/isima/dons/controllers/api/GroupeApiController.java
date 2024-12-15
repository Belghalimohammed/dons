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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groupes")
public class GroupeApiController {

    @Autowired
    private GroupeService groupeService;

    @Autowired
    private UserService userService;

    // Get all non-valid groups for the authenticated user
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllNonValideGroupes(Authentication authentication) {
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipale.getId());

        List<Groupe> groupes = groupeService.getGroupeByAcheteurAndNotTaken(user.getId());

        Map<String, Object> response = new HashMap<>();
        if (!groupes.isEmpty()) {
            response.put("status", "success");
            response.put("data", groupes);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "fail");
            response.put("message", "No groupes found.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }
    }

    // Get all valid groups for the authenticated user
    @GetMapping("/valide")
    public ResponseEntity<Map<String, Object>> getAllValideGroupes(Authentication authentication) {
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipale.getId());

        List<Groupe> groupes = groupeService.getGroupeByAcheteurAndTaken(user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", groupes);
        return ResponseEntity.ok(response);
    }

    // Get all annonces in a specific groupe
    @GetMapping("/{groupeId}/annonces")
    public ResponseEntity<Map<String, Object>> getAnnoncesInGroupe(@PathVariable Long groupeId) {
        Groupe groupe = groupeService.getGroupeById(groupeId);

        Map<String, Object> response = new HashMap<>();
        if (groupe != null) {
            response.put("status", "success");
            response.put("data", groupe.getAnnonces());
        } else {
            response.put("status", "fail");
            response.put("message", "Groupe not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(response);
    }

    // Create a new groupe from an annonce
    @PostMapping
    public ResponseEntity<Map<String, Object>> createGroupe(@RequestParam("annonceId") Long annonceId,
            Authentication authentication) {
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipale.getId());

        Groupe groupe = groupeService.createGroupe(annonceId, user.getId());

        Map<String, Object> response = new HashMap<>();
        if (groupe != null) {
            response.put("status", "success");
            response.put("data", groupe);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            response.put("status", "fail");
            response.put("message", "Groupe creation failed.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Validate a groupe
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateGroupe(@RequestParam Long groupeId,
            Authentication authentication) {
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        Long achteur = userDetails.getId();

        Groupe isValid = groupeService.validateGroupe(groupeId, achteur);

        Map<String, Object> response = new HashMap<>();
        if (isValid != null) {
            response.put("status", "success");
            response.put("message", "Groupe validated successfully.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        } else {
            response.put("status", "fail");
            response.put("message", "Groupe not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // Remove an annonce from a groupe
    @DeleteMapping("/{groupeId}/annonces/{annonceId}")
    public ResponseEntity<Map<String, Object>> removeAnnonceFromGroupe(@PathVariable Long groupeId,
            @PathVariable Long annonceId) {
        boolean isRemoved = groupeService.removeAnnonceFromGroupe(groupeId, annonceId);

        Map<String, Object> response = new HashMap<>();
        if (isRemoved) {
            response.put("status", "success");
            response.put("message", "Annonce removed successfully.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        } else {
            response.put("status", "fail");
            response.put("message", "Groupe or Annonce not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // Delete a Groupe
    @DeleteMapping("/{groupeId}")
    public ResponseEntity<Map<String, Object>> deleteGroupe(@PathVariable Long groupeId,
            Authentication authentication) {
        Groupe groupe = groupeService.getGroupeById(groupeId);
        Map<String, Object> response = new HashMap<>();
        if (groupe == null) {
            response.put("status", "fail");
            response.put("message", "Groupe not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipale.getId());

        if (!groupe.getAcheteur().getId().equals(user.getId())) {
            response.put("status", "fail");
            response.put("message", "Unauthorized to delete the group.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        groupeService.deleteGroupe(groupeId);
        response.put("status", "success");
        response.put("message", "Groupe deleted successfully.");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    // Update a Groupe
    @PutMapping("/{groupeId}")
    public ResponseEntity<Map<String, Object>> updateGroupe(
            @PathVariable Long groupeId, @RequestBody Groupe updatedGroupe, Authentication authentication) {

        Groupe groupe = groupeService.getGroupeById(groupeId);
        Map<String, Object> response = new HashMap<>();

        if (groupe == null) {
            response.put("status", "fail");
            response.put("message", "Groupe not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipale.getId());

        if (!groupe.getAcheteur().getId().equals(user.getId())) {
            response.put("status", "fail");
            response.put("message", "Unauthorized to update the group.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        groupe.setPri(updatedGroupe.isPri());
        groupe.setCreationDate(updatedGroupe.getCreationDate());
        groupe.setValidationDate(updatedGroupe.getValidationDate());
        groupe.setAnnonces(updatedGroupe.getAnnonces());
        groupe.setAcheteur(updatedGroupe.getAcheteur());

        groupeService.updateGroupe(groupeId, groupe);

        response.put("status", "success");
        response.put("data", groupe);
        return ResponseEntity.ok(response);
    }
}
