package com.isima.dons.controllers.api;

import com.isima.dons.configuration.SessionUtils;
import com.isima.dons.entities.Annonce;
import com.isima.dons.entities.Recherche;
import com.isima.dons.services.AnnonceService;
import com.isima.dons.services.RechercheService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
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
    public ResponseEntity<List<Recherche>> getAllRecherches() {
        List<Recherche> recherches = rechercheService.getAllRecherches();
        return new ResponseEntity<>(recherches, HttpStatus.OK);
    }

    // Use search parameters to set session values for filtering
    @GetMapping("/use/{id}")
    public ResponseEntity<List<Annonce>> saveRecherche(@PathVariable("id") Long id) {
        Recherche recherche = rechercheService.getRechercheById(id);
        List<Annonce> recherches = annonceService.findAllFilteredAnnonces(recherche.getSearchTerm(),
                recherche.getZone(), recherche.getKeywordsList(), recherche.getEtatObjetList());
        return new ResponseEntity<>(recherches, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRechercheById(@PathVariable("id") Long id) {
        try {
            Recherche recherche = rechercheService.getRechercheById(id); // This will throw if not found
            return new ResponseEntity<>(recherche, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            // Use getStatusCode() instead of getStatus()
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new ResponseEntity<>("Recherche not found with id " + id, HttpStatus.NOT_FOUND);
            }
            throw e; // Propagate other exceptions
        }
    }

    // 2. Update Recherche
    @PutMapping("/{id}")
    public ResponseEntity<Recherche> updateRecherche(@PathVariable("id") Long id,
            @RequestBody Recherche updatedRecherche) {
        Recherche updated = rechercheService.updateRecherche(id, updatedRecherche);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // 3. Delete Recherche
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecherche(@PathVariable("id") Long id) {
        rechercheService.deleteRecherche(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
