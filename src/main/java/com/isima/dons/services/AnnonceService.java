package com.isima.dons.services;

import com.isima.dons.entities.Annonce;
import com.isima.dons.entities.Annonce.EtatObjet;
import com.isima.dons.entities.FilterCriteria;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AnnonceService {

    List<Annonce> getAllAnnonces();

    Annonce getAnnonceById(Long id);

    List<Annonce> getAnnoncesByUser(Long idUser);

    List<Annonce> getAnnoncesByVendeurId(Long vendeurId);

    Annonce createAnnonce(Annonce annonce);

    Annonce updateAnnonce(Long id, Annonce updatedAnnonce);

    void deleteAnnonce(Long id);

    Page<Annonce> findFilteredAnnonces(String key, String zone, List<String> keywordsList, List<String> etatList,
            int page);

    Long findFilteredAnnoncesCount(String key, String zone, List<String> keywordsList, List<String> etatList);
}
