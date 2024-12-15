package com.isima.dons.controllers.api;

import com.isima.dons.configuration.UserPrincipale;
import com.isima.dons.entities.Annonce;
import com.isima.dons.entities.Recherche;
import com.isima.dons.entities.User;
import com.isima.dons.services.AnnonceService;
import com.isima.dons.services.RechercheService;
import com.isima.dons.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/annonces")
public class AnnonceApiController {

    private final AnnonceService annonceService;
    private final UserService userService;
    private final RechercheService rechercheService;

    @Autowired
    public AnnonceApiController(AnnonceService annonceService, UserService userService,
            RechercheService rechercheService) {
        this.annonceService = annonceService;
        this.userService = userService;
        this.rechercheService = rechercheService;
    }

    @GetMapping
    public ResponseEntity<Page<Annonce>> getAllAnnonces(
            @RequestParam(value = "search", required = false) String key, // key: search term
            @RequestParam(value = "zone", required = false) String zone, // zone: location filter
            @RequestParam(value = "items", required = false) List<String> items, // items: list of keywords
            @RequestParam(value = "etatList", required = false) List<String> etatList, // etatList: list of conditions
                                                                                       // (e.g., "neuf", "commeNeuf")
            @RequestParam(value = "page", defaultValue = "0") int page) { // size: number of items per page

        // Fetch the filtered annonces based on provided parameters
        Page<Annonce> annonces = annonceService.findFilteredAnnonces(key, zone, items, etatList, page);

        return ResponseEntity.ok(annonces); // Return the result as JSON in ResponseEntity
    }

    @GetMapping("/{id}")
    public ResponseEntity<Annonce> getAnnonce(@PathVariable Long id) {
        Annonce annonce = annonceService.getAnnonceById(id);
        if (annonce == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(annonce);
    }

    @GetMapping("/mes-annonces")
    public ResponseEntity<List<Annonce>> getMyAnnonces(Authentication authentication) {
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userDetails.getId());
        List<Annonce> annonces = annonceService.getAnnoncesByUser(user.getId());
        return ResponseEntity.ok(annonces);
    }

    @GetMapping("/mes-achats")
    public ResponseEntity<List<Annonce>> getMyPurchases(Authentication authentication) {
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userDetails.getId());
        List<Annonce> annonces = annonceService.getAnnoncesByAcheteur(user);
        return ResponseEntity.ok(annonces);
    }

    @PostMapping
    public ResponseEntity<Annonce> createAnnonce(@RequestBody Annonce annonce, Authentication authentication) {
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        annonce.setDatePublication(LocalDate.now());
        annonce.setVendeur(userService.getUserById(userDetails.getId()));
        annonceService.createAnnonce(annonce, userDetails.getId());

        URI location = URI.create("/api/annonces/" + annonce.getId());
        return ResponseEntity.created(location).body(annonce);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Annonce> updateAnnonce(@PathVariable Long id, @RequestBody Annonce updatedAnnonce) {
        Annonce existingAnnonce = annonceService.getAnnonceById(id);
        if (existingAnnonce == null) {
            return ResponseEntity.notFound().build();
        }

        updatedAnnonce.setDatePublication(existingAnnonce.getDatePublication());
        updatedAnnonce.setVendeur(existingAnnonce.getVendeur());
        annonceService.updateAnnonce(id, updatedAnnonce);

        return ResponseEntity.ok(updatedAnnonce);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnonce(@PathVariable Long id) {
        Annonce annonce = annonceService.getAnnonceById(id);
        if (annonce == null) {
            return ResponseEntity.notFound().build();
        }

        annonceService.deleteAnnonce(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/buy/{annonceId}")
    public ResponseEntity<Void> addAcheteur(@PathVariable Long annonceId, Authentication authentication) {
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userDetails.getId());
        annonceService.addAcheteurAndMarkAsPri(annonceId, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/addfavoris/{annonceId}")
    public ResponseEntity<Void> addFavoris(@PathVariable Long annonceId, Authentication authentication) {
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        userService.addFavorisToUserUser(userDetails.getId(), annonceId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/removefavoris/{annonceId}")
    public ResponseEntity<Void> removeFavoris(@PathVariable Long annonceId, Authentication authentication) {
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        userService.removeFavorisToUserUser(userDetails.getId(), annonceId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/favoris")
    public ResponseEntity<List<Annonce>> listFavoris(Authentication authentication) {
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userDetails.getId());
        List<Annonce> favoris = user.getFavoris();
        return ResponseEntity.ok(favoris);
    }
}
