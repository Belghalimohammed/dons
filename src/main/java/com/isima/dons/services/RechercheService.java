package com.isima.dons.services;

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

    List<Recherche> getByUserAndSearchTermAndKeywordsListInAndEtatObjetListInAndZone(User user, String searchTerm,
            List<String> keywordsList, List<String> etatObjetList, String zone);

    List<Recherche> getByUserAndSearchTerm(User user, String searchTerm);

    void deleteRecherche(Long id);
}
