package com.isima.dons.controllers.api;

import com.isima.dons.entities.Annonce;
import com.isima.dons.entities.Recherche;
import com.isima.dons.services.AnnonceService;
import com.isima.dons.services.RechercheService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recherches")
public class RechercheApiController {

    @Autowired
    private RechercheService rechercheService;

    @Autowired
    private AnnonceService annonceService;

    // Fetch all recherches
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRecherches() {
        List<Recherche> recherches = rechercheService.getAllRecherches();
        Map<String, Object> response = new HashMap<>();
        if (recherches.isEmpty()) {
            response.put("status", "fail");
            response.put("message", "No recherches found.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response); // No recherches found
        } else {
            response.put("status", "success");
            response.put("data", recherches);
            return ResponseEntity.ok(response); // Return recherches list
        }
    }

    // Use search parameters to set session values for filtering
    @GetMapping("/use/{id}")
    public ResponseEntity<Map<String, Object>> saveRecherche(@PathVariable("id") Long id) {
        Recherche recherche = rechercheService.getRechercheById(id);
        if (recherche == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "fail");
            response.put("message", "Recherche not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // Recherche not found
        }

        List<Annonce> annonces = annonceService.findAllFilteredAnnonces(
                recherche.getSearchTerm(), recherche.getZone(),
                recherche.getKeywordsList(), recherche.getEtatObjetList());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", annonces);
        return ResponseEntity.ok(response); // Return filtered annonces
    }

    // Get Recherche by id
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRechercheById(@PathVariable("id") Long id) {
        try {
            Recherche recherche = rechercheService.getRechercheById(id); // This will throw if not found
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", recherche);
            return ResponseEntity.ok(response); // Return the recherche
        } catch (ResponseStatusException e) {
            Map<String, Object> response = new HashMap<>();
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                response.put("status", "fail");
                response.put("message", "Recherche not found with id " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // Recherche not found
            }
            throw e; // Propagate other exceptions
        }
    }

    // Update Recherche
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRecherche(@PathVariable("id") Long id,
            @RequestBody Recherche updatedRecherche) {
        try {
            Recherche updated = rechercheService.updateRecherche(id, updatedRecherche);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", updated);
            return ResponseEntity.ok(response); // Return updated recherche
        } catch (ResponseStatusException e) {
            Map<String, Object> response = new HashMap<>();
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                response.put("status", "fail");
                response.put("message", "Recherche not found with id " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // Recherche not found
            }
            throw e; // Propagate other exceptions
        }
    }

    // Delete Recherche
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRecherche(@PathVariable("id") Long id) {
        try {
            rechercheService.deleteRecherche(id);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Recherche deleted successfully.");
            return ResponseEntity.noContent().build(); // No Content for successful deletion
        } catch (ResponseStatusException e) {
            Map<String, Object> response = new HashMap<>();
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                response.put("status", "fail");
                response.put("message", "Recherche not found with id " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // Recherche not found
            }
            throw e; // Propagate other exceptions
        }
    }
}
