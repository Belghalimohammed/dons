package com.isima.dons.controllers.api;

import com.isima.dons.entities.Annonce;
import com.isima.dons.entities.User;
import com.isima.dons.services.AnnonceService;
import com.isima.dons.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.isima.dons.configuration.UserPrincipale;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/annonces")
public class AnnonceApiController {

    private final AnnonceService annonceService;
    private final UserService userService;

    @Autowired
    public AnnonceApiController(AnnonceService annonceService, UserService userService) {
        this.annonceService = annonceService;
        this.userService = userService;
    }

    // Fetch all annonces
    @GetMapping
    public ResponseEntity<List<Annonce>> getAllAnnonces() {
        List<Annonce> annonces = annonceService.getAllAnnonces();
        return ResponseEntity.ok(annonces);
    }

    // Fetch annonces by current user
    @GetMapping("/mes-annonces")
    public ResponseEntity<List<Annonce>> getUserAnnonces(Authentication authentication) {
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        List<Annonce> annonces = annonceService.getAnnoncesByUser(userPrincipale.getId());
        return ResponseEntity.ok(annonces);
    }

    // Fetch annonce details by ID
    @GetMapping("/{id}")
    public ResponseEntity<Annonce> getAnnonceById(@PathVariable Long id) {
        Annonce annonce = annonceService.getAnnonceById(id);
        return ResponseEntity.ok(annonce);
    }

    // Fetch annonces by vendeur ID
    @GetMapping("/vendeur/{id}")
    public ResponseEntity<List<Annonce>> getAnnoncesByVendeur(@PathVariable Long id) {
        List<Annonce> annonces = annonceService.getAnnoncesByVendeurId(id);
        return ResponseEntity.ok(annonces);
    }

    // Create a new annonce
    @PostMapping
    public ResponseEntity<Annonce> createAnnonce(@RequestBody Annonce annonce) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();

        annonce.setDatePublication(LocalDate.now());
        annonce.setVendeur(userService.getUserById(userDetails.getId()));
        annonce.setKeywords(Arrays.asList(annonce.getKeywords().get(0).split(" ")));

        Annonce createdAnnonce = annonceService.createAnnonce(annonce);
        return ResponseEntity.ok(createdAnnonce);
    }

    // Update an existing annonce
    @PutMapping("/{id}")
    public ResponseEntity<Annonce> updateAnnonce(@PathVariable Long id, @RequestBody Annonce updatedAnnonce) {
        Annonce existingAnnonce = annonceService.getAnnonceById(id);

        updatedAnnonce.setDatePublication(existingAnnonce.getDatePublication());
        updatedAnnonce.setVendeur(existingAnnonce.getVendeur());
        updatedAnnonce.setKeywords(
                Arrays.asList(updatedAnnonce.getKeywords().get(0)
                        .replaceAll("\\s+", " ")
                        .trim()
                        .split(" ")));

        Annonce savedAnnonce = annonceService.updateAnnonce(id, updatedAnnonce);
        return ResponseEntity.ok(savedAnnonce);
    }

    // Delete an annonce
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnonce(@PathVariable Long id) {
        annonceService.deleteAnnonce(id);
        return ResponseEntity.noContent().build();
    }
}
