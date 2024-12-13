package com.isima.dons.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.isima.dons.entities.Recherche;
import com.isima.dons.entities.User;

@Repository
public interface RechercheRepository extends JpaRepository<Recherche, Long> {

        boolean existsByUserAndSearchTermAndZoneAndKeywordsListContainsAndEtatObjetListContains(
                        User user, String searchTerm, String zone, List<String> keywordsList,
                        List<String> etatObjetList);

        List<Recherche> findByUserAndSearchTermAndZone(User user, String key, String zone);

        List<Recherche> findByUserAndSearchTerm(
                        User user,
                        String searchTerm);

}