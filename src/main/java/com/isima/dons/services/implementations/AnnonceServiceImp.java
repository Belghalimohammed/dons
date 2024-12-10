package com.isima.dons.services.implementations;

import com.isima.dons.entities.Annonce;
import com.isima.dons.entities.Annonce.EtatObjet;
import com.isima.dons.entities.FilterCriteria;
import com.isima.dons.repositories.AnnonceRepository;
import com.isima.dons.repositories.AnnonceSpecification;
import com.isima.dons.services.AnnonceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.stream.Collectors;

@Service
public class AnnonceServiceImp implements AnnonceService {

    @Autowired
    private AnnonceRepository annonceRepository;

    public List<Annonce> getAllAnnonces() {
        String key = "";
        String zone = "";
        List<String> keywordsList = new ArrayList<>();
        List<String> etatList = new ArrayList<>();

        Specification<Annonce> spec = AnnonceSpecification.searchAnnonce(key, keywordsList, etatList, zone);
        return annonceRepository.findAll(spec);
    }

    public Annonce getAnnonceById(Long id) {
        return annonceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce not found"));
    }

    public Annonce createAnnonce(Annonce annonce) {
        return annonceRepository.save(annonce);
    }

    @Override
    public List<Annonce> getAnnoncesByVendeurId(Long vendeurId) {
        return annonceRepository.findByVendeurId(vendeurId);
    }

    @Override
    public List<Annonce> getAnnoncesByUser(Long idUser) {
        return annonceRepository.getAnnoncesByUser(idUser);
    }

    public Annonce updateAnnonce(Long id, Annonce updatedAnnonce) {
        Optional<Annonce> annonceOptional = annonceRepository.findById(id);

        if (annonceOptional.isPresent()) {
            Annonce existingAnnonce = annonceOptional.get();
            existingAnnonce.setTitre(updatedAnnonce.getTitre());
            existingAnnonce.setDescription(updatedAnnonce.getDescription());
            existingAnnonce.setEtatObjet(updatedAnnonce.getEtatObjet());
            existingAnnonce.setDatePublication(updatedAnnonce.getDatePublication());
            // existingAnnonce.setLatitude(updatedAnnonce.getLatitude());
            // existingAnnonce.setLongitude(updatedAnnonce.getLongitude());
            existingAnnonce.setTypeDon(updatedAnnonce.getTypeDon());
            existingAnnonce.setKeywords(updatedAnnonce.getKeywords());
            return annonceRepository.save(existingAnnonce);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce not found");
        }
    }

    public void deleteAnnonce(Long id) {
        if (annonceRepository.existsById(id)) {
            annonceRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Annonce not found");
        }
    }

    @Override
    public Page<Annonce> findFilteredAnnonces(String key, String zone, List<String> keywordsList, List<String> etatList,
            int page) {

        Pageable pageable = PageRequest.of(page, 20);

        Specification<Annonce> spec = AnnonceSpecification.searchAnnonce(key, keywordsList, etatList, zone);
        return annonceRepository.findAll(spec, pageable);
    }

    @Override
    public Long findFilteredAnnoncesCount(String key, String zone, List<String> keywordsList, List<String> etatList) {

        Specification<Annonce> spec = AnnonceSpecification.searchAnnonce(key, keywordsList, etatList, zone);
        return annonceRepository.count(spec);
    }
}
