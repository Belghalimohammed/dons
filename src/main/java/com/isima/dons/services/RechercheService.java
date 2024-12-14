package com.isima.dons.services;

import com.isima.dons.entities.Annonce;
import com.isima.dons.entities.Recherche;
import com.isima.dons.entities.User;
import com.isima.dons.repositories.RechercheRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public interface RechercheService {

    List<Recherche> getAllRecherches();

    Recherche getRechercheById(Long id);

    Recherche createRecherche(Recherche recherche);

    Recherche updateRecherche(Long id, Recherche updatedRecherche);

    boolean exists(Recherche recherche);

    List<Recherche> getByUserAndSearchTerm(User user, String searchTerm);

    void deleteRecherche(Long id);

    List<Recherche> getRechercheByAnnonce(Annonce annonce);
}
