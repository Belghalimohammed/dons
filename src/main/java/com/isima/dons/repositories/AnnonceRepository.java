package com.isima.dons.repositories;

import com.isima.dons.entities.Annonce;
import com.isima.dons.entities.User;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnonceRepository extends JpaRepository<Annonce, Long>, JpaSpecificationExecutor<Annonce> {

    @Query("select a from Annonce a where a.pri = false")
    List<Annonce> getAnnoncesDisponible();

    @Query("select a from Annonce a where a.vendeur.id = ?1")
    List<Annonce> getAnnoncesByUser(@Param("user") Long user);

    List<Annonce> findByVendeurIdAndPriFalse(Long vendeurId);

    @Query("SELECT DISTINCT a.zone FROM Annonce a")
    List<String> findDistinctZones();

    List<Annonce> findByAcheteur(User acheteur);

    List<Annonce> findAll(Specification<Annonce> spec);

}
