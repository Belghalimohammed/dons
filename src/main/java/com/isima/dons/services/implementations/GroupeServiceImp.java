package com.isima.dons.services.implementations;

import com.isima.dons.entities.Annonce;
import com.isima.dons.entities.Groupe;
import com.isima.dons.entities.User;
import com.isima.dons.repositories.AnnonceRepository;
import com.isima.dons.repositories.GroupeRepository;
import com.isima.dons.services.AnnonceService;
import com.isima.dons.services.GroupeService;
import com.isima.dons.services.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class GroupeServiceImp implements GroupeService {

    @Autowired
    private GroupeRepository groupeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AnnonceService annonceService;

    @Autowired
    private AnnonceRepository annonceRepository;

    @Override
    public List<Groupe> getAllGroupes() {
        return groupeRepository.findAll();
    }

    @Override
    public Groupe getGroupeById(Long id) {
        return groupeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Groupe not found"));
    }

    @Override
    public List<Groupe> getGroupeByAcheteurAndNotTaken(Long userId) {
        return groupeRepository.getGroupeByAcheteurAndNotTaken(userId);
    }

    @Override
    public List<Groupe> getGroupeByAcheteurAndTaken(Long userId) {
        return groupeRepository.getGroupeByAcheteurAndTaken(userId);
    }

    @Override
    public List<Annonce> getAnnoncesFromGroupe(Long userId) {
        List<Groupe> groupes = getGroupeByAcheteurAndNotTaken(userId);
        if (!groupes.isEmpty()) {
            return groupes.get(0).getAnnonces(); // Assuming the first group is the one you're interested in
        }
        return new ArrayList<>();
    }

    @Override
    public Groupe createGroupe(Long annonceId, Long idUser) {
        // Fetch User and Annonce entities
        User user = userService.getUserById(idUser);
        Annonce annonce = annonceService.getAnnonceById(annonceId);

        // Get any group that the user is part of and is not taken (pri == false)
        List<Groupe> existingGroups = groupeRepository.getGroupeByAcheteurAndNotTaken(idUser);

        // If no group exists, create a new group
        if (existingGroups.isEmpty()) {
            Groupe newGroupe = new Groupe();
            newGroupe.setCreationDate(new Date());
            List<Annonce> annonces = new ArrayList<>();
            annonces.add(annonce);
            newGroupe.setAnnonces(annonces);
            newGroupe.setAcheteur(user);
            newGroupe.setPri(false); // Ensure pri is set correctly
            return groupeRepository.save(newGroupe);
        } else {
            // Add the annonce to the first group found (assuming one group per user)
            Groupe existingGroupe = existingGroups.get(0);
            if (existingGroupe.getAnnonces().isEmpty()) {
                existingGroupe.setCreationDate(new Date());
            }
            List<Annonce> annoncesInGroup = existingGroupe.getAnnonces();

            // Check if the annonce already exists in the group
            if (!annoncesInGroup.contains(annonce)) {
                annoncesInGroup.add(annonce);
                existingGroupe.setAnnonces(annoncesInGroup);
                return groupeRepository.save(existingGroupe);
            } else {
                // If the annonce is already in the group, return the existing group
                return existingGroupe;
            }
        }
    }

    @Override
    public Groupe updateGroupe(Long id, Groupe updatedGroupe) {
        Optional<Groupe> groupeOptional = groupeRepository.findById(id);

        if (groupeOptional.isPresent()) {
            Groupe existingGroupe = groupeOptional.get();
            existingGroupe.setAnnonces(updatedGroupe.getAnnonces());
            existingGroupe.setAcheteur(updatedGroupe.getAcheteur());
            return groupeRepository.save(existingGroupe);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Groupe not found");
        }
    }

    public void deleteAllGroups() {
        groupeRepository.deleteAllGroups();
    }

    @PostConstruct
    public void resetGroupeTableOnStartup() {
        groupeRepository.deleteAll();
    }

    @Override
    public void deleteGroupe(Long id) {
        if (groupeRepository.existsById(id)) {
            groupeRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Groupe not found");
        }
    }

    public boolean removeAnnonceFromGroupe(Long groupeId, Long annonceId) {
        // Find the Groupe by ID
        Optional<Groupe> groupeOptional = groupeRepository.findById(groupeId);

        if (groupeOptional.isPresent()) {
            Groupe groupe = groupeOptional.get();

            // Find and remove the Annonce by ID from the list of Annonces
            List<Annonce> annonces = groupe.getAnnonces();
            boolean isRemoved = annonces.removeIf(annonce -> annonce.getId().equals(annonceId));

            if (isRemoved) {
                // Save the updated Groupe
                groupeRepository.save(groupe);
            }
            return isRemoved;
        }

        return false;
    }

    @Override
    public Groupe validateGroupe(Long groupeId) {
        Groupe groupe = getGroupeById(groupeId);

        groupe.setPri(true);
        groupe.setValidationDate(new Date());
        groupeRepository.save(groupe);
        for (Annonce annonce : groupe.getAnnonces()) {
            annonce.setPri(true);
            annonceRepository.save(annonce);
        }
        return groupe;
    }
}
