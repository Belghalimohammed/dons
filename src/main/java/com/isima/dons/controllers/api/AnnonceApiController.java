package com.isima.dons.controllers.api;

import com.isima.dons.configuration.UserPrincipale;
import com.isima.dons.entities.Annonce;
import com.isima.dons.entities.User;
import com.isima.dons.services.AnnonceService;
import com.isima.dons.services.RechercheService;
import com.isima.dons.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/annonces")
public class AnnonceApiController {

    private final AnnonceService annonceService;
    private final UserService userService;

    public AnnonceApiController(AnnonceService annonceService, UserService userService,
            RechercheService rechercheService) {
        this.annonceService = annonceService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllAnnonces(
            @RequestParam(value = "search", required = false) String key,
            @RequestParam(value = "zone", required = false) String zone,
            @RequestParam(value = "items", required = false) List<String> items,
            @RequestParam(value = "etatList", required = false) List<String> etatList,
            @RequestParam(value = "page", defaultValue = "0") int page) {

        // Fetch the filtered annonces
        Page<Annonce> annonces = annonceService.findFilteredAnnonces(key, zone, items, etatList, page);

        // Return success response with a message
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Annonces retrieved successfully");
        response.put("status", HttpStatus.OK.value());
        response.put("data", annonces);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAnnonce(@PathVariable Long id) {
        Annonce annonce = annonceService.getAnnonceById(id);

        if (annonce == null) {
            // Structured response for Not Found
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Annonce not found with ID: " + id);
            response.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Return success response with a message
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Annonce retrieved successfully");
        response.put("status", HttpStatus.OK.value());
        response.put("data", annonce);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/mes-annonces")
    public ResponseEntity<Map<String, Object>> getMyAnnonces(Authentication authentication) {
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userDetails.getId());
        List<Annonce> annonces = annonceService.getAnnoncesByUser(user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User's annonces retrieved successfully");
        response.put("status", HttpStatus.OK.value());
        response.put("data", annonces);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/mes-achats")
    public ResponseEntity<Map<String, Object>> getMyPurchases(Authentication authentication) {
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userDetails.getId());
        List<Annonce> annonces = annonceService.getAnnoncesByAcheteur(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User's purchases retrieved successfully");
        response.put("status", HttpStatus.OK.value());
        response.put("data", annonces);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Object> createAnnonce(@RequestBody Annonce annonce, Authentication authentication) {
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        annonce.setDatePublication(LocalDate.now());
        annonce.setVendeur(userService.getUserById(userDetails.getId())); // Ensure this does not return null
        annonceService.createAnnonce(annonce, userDetails.getId());

        URI location = URI.create("/api/annonces/" + annonce.getId());

        // Response with 201 Created status
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Annonce created successfully");
        response.put("status", HttpStatus.CREATED.value());
        response.put("data", annonce);
        response.put("location", location.toString());

        return ResponseEntity.created(location).body(response);
    }



    @PutMapping("/{id}")
    public ResponseEntity<Object> updateAnnonce(@PathVariable Long id, @RequestBody Annonce updatedAnnonce) {
        Annonce existingAnnonce = annonceService.getAnnonceById(id);
        if (existingAnnonce == null) {
            // Return Not Found if the annonce doesn't exist
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Annonce not found with ID: " + id);
            response.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        updatedAnnonce.setDatePublication(existingAnnonce.getDatePublication());
        updatedAnnonce.setVendeur(existingAnnonce.getVendeur());
        annonceService.updateAnnonce(id, updatedAnnonce);

        // Return success response for update
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Annonce updated successfully");
        response.put("status", HttpStatus.OK.value());
        response.put("data", updatedAnnonce);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAnnonce(@PathVariable Long id) {
        // Retrieve the annonce by ID
        Annonce annonce = annonceService.getAnnonceById(id);

        if (annonce == null) {
            // Return 404 Not Found with a custom message
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Annonce not found with ID: " + id);
            response.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Get the currently authenticated user (vendeur)
        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // Check if the logged-in user is the vendeur of the annonce
        if (!annonce.getVendeur().getUsername().equals(loggedInUsername)) {
            // Return 403 Forbidden with a custom message
            Map<String, Object> response = new HashMap<>();
            response.put("message", "You are not authorized to delete this annonce.");
            response.put("status", HttpStatus.FORBIDDEN.value());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        // Proceed to delete the annonce if the logged-in user is the vendeur
        annonceService.deleteAnnonce(id);

        // Return 204 No Content (no body) indicating successful deletion
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Annonce deleted successfully");
        response.put("status", HttpStatus.NO_CONTENT.value());
        return ResponseEntity.noContent().build(); // No content, just successful deletion
    }

    @PostMapping("/buy/{annonceId}")
    public ResponseEntity<Object> addAcheteur(@PathVariable Long annonceId, Authentication authentication) {
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userDetails.getId());
        annonceService.addAcheteurAndMarkAsPri(annonceId, user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Acheteur added successfully");
        response.put("status", HttpStatus.NO_CONTENT.value());

        return ResponseEntity.noContent().build(); // Return 204 No Content
    }

    @PostMapping("/addfavoris/{annonceId}")
    public ResponseEntity<Object> addFavoris(@PathVariable Long annonceId, Authentication authentication) {
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        userService.addFavorisToUserUser(userDetails.getId(), annonceId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Annonce added to favoris successfully");
        response.put("status", HttpStatus.NO_CONTENT.value());

        return ResponseEntity.noContent().build(); // Return 204 No Content
    }

    @PostMapping("/removefavoris/{annonceId}")
    public ResponseEntity<Object> removeFavoris(@PathVariable Long annonceId, Authentication authentication) {
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        userService.removeFavorisToUserUser(userDetails.getId(), annonceId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Annonce removed from favoris successfully");
        response.put("status", HttpStatus.NO_CONTENT.value());

        return ResponseEntity.noContent().build(); // Return 204 No Content
    }

    @GetMapping("/favoris")
    public ResponseEntity<Object> listFavoris(Authentication authentication) {
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userDetails.getId());
        List<Annonce> favoris = user.getFavoris();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Favoris retrieved successfully");
        response.put("status", HttpStatus.OK.value());
        response.put("data", favoris);

        return ResponseEntity.ok(response); // Return 200 OK with the list of favoris
    }
}
