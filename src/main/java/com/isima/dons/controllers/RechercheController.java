package com.isima.dons.controllers;

import com.isima.dons.entities.Recherche;
import com.isima.dons.services.RechercheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recherches")
public class RechercheController {

    @Autowired
    private RechercheService rechercheService;

    @GetMapping
    public ResponseEntity<List<Recherche>> getAllRecherches() {
        return new ResponseEntity<>(rechercheService.getAllRecherches(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recherche> getRechercheById(@PathVariable Long id) {
        return new ResponseEntity<>(rechercheService.getRechercheById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Recherche> createRecherche(@RequestBody Recherche recherche) {
        return new ResponseEntity<>(rechercheService.createRecherche(recherche), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recherche> updateRecherche(@PathVariable Long id, @RequestBody Recherche updatedRecherche) {
        return new ResponseEntity<>(rechercheService.updateRecherche(id, updatedRecherche), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecherche(@PathVariable Long id) {
        rechercheService.deleteRecherche(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
