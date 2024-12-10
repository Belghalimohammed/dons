package com.isima.dons.services.implementations;

import com.isima.dons.entities.Recherche;
import com.isima.dons.entities.User;
import com.isima.dons.repositories.RechercheRepository;
import com.isima.dons.services.RechercheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class RechercheServiceImpl implements RechercheService {

    @Autowired
    private RechercheRepository rechercheRepository;

    @Override
    public List<Recherche> getAllRecherches() {
        return rechercheRepository.findAll();
    }

    @Override
    public Recherche getRechercheById(Long id) {
        return rechercheRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recherche not found"));
    }

    @Override
    public Recherche createRecherche(Recherche recherche) {
        return rechercheRepository.save(recherche);
    }

    @Override
    public List<Recherche> getByUserAndSearchTermAndKeywordsListInAndEtatObjetListInAndZone(User user,
            String searchTerm, List<String> keywordsList, List<String> etatObjetList, String zone) {

        return rechercheRepository.findByUserAndSearchTermAndKeywordsListInAndEtatObjetListInAndZone(user, searchTerm,
                keywordsList, etatObjetList, zone);
    }

    @Override
    public List<Recherche> getByUserAndSearchTerm(User user, String searchTerm) {

        return rechercheRepository.findByUserAndSearchTerm(user, searchTerm);
    }

    @Override
    public Recherche updateRecherche(Long id, Recherche updatedRecherche) {
        Optional<Recherche> rechercheOptional = rechercheRepository.findById(id);

        if (rechercheOptional.isPresent()) {
            Recherche existingRecherche = rechercheOptional.get();
            existingRecherche.setUser(updatedRecherche.getUser());
            return rechercheRepository.save(existingRecherche);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recherche not found");
        }
    }

    @Override
    public void deleteRecherche(Long id) {
        if (rechercheRepository.existsById(id)) {
            rechercheRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recherche not found");
        }
    }
}