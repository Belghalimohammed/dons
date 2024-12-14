package com.isima.dons.repositories;

import java.util.List;

import com.isima.dons.entities.Annonce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

        @Query("SELECT r FROM Recherche r WHERE r.searchTerm = :searchTerm " +
                "OR EXISTS (SELECT 1 FROM r.keywordsList k WHERE k IN :keywords) " +
                "OR r.zone = :zone")
        List<Recherche> findByAnnonce(@Param("searchTerm") String searchTerm,
                                      @Param("keywords") List<String> keywords,
                                      @Param("zone") String zone);




}