package com.isima.dons.services;

import com.isima.dons.entities.Annonce;
import com.isima.dons.entities.User;

import org.springframework.data.domain.Page;

import java.util.List;

public interface AnnonceService {

    List<Annonce> getAllAnnonces();

    Annonce getAnnonceById(Long id);

    List<Annonce> getAnnoncesByUser(Long idUser);

    List<Annonce> getAnnoncesByVendeurId(Long vendeurId);

    List<String> findDistinctZones();

    void addAcheteurAndMarkAsPri(Long annonceId, User currentUser);

    List<Annonce> getAnnoncesByAcheteur(User acheteur);

    Annonce createAnnonce(Annonce annonce, Long userId);

    Annonce updateAnnonce(Long id, Annonce updatedAnnonce);

    void deleteAnnonce(Long id);

    Page<Annonce> findFilteredAnnonces(String key, String zone, List<String> keywordsList, List<String> etatList,
            int page);

    Long findFilteredAnnoncesCount(String key, String zone, List<String> keywordsList, List<String> etatList);

    List<Annonce> findAllFilteredAnnonces(String key, String zone, List<String> keywordsList, List<String> etatList);
}
