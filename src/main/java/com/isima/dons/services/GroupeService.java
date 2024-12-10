package com.isima.dons.services;

import com.isima.dons.entities.Annonce;
import com.isima.dons.entities.Groupe;

import java.util.List;

public interface GroupeService {

    List<Groupe> getAllGroupes();

    Groupe getGroupeById(Long id);

    Groupe createGroupe(Long annonceId, Long userId);

    void deleteAllGroups();

    Groupe updateGroupe(Long id, Groupe updatedGroupe);

    void deleteGroupe(Long id);

    boolean removeAnnonceFromGroupe(Long groupeId, Long annonceId);

    Groupe validateGroupe(Long groupeId);

    List<Groupe> getGroupeByAcheteurAndNotTaken(Long userId);

    List<Annonce> getAnnoncesFromGroupe(Long userId);

    List<Groupe> getGroupeByAcheteurAndTaken(Long userId);
}
