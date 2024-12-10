package com.isima.dons.repositories;

import org.springframework.data.jpa.domain.Specification;
import com.isima.dons.entities.Annonce;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AnnonceSpecification {

    // Method to search for annonces based on keyword, keywords list, etatObjet
    // list, and zone
    public static Specification<Annonce> searchAnnonce(String keyword, List<String> keywordslist,
            List<String> listEtatObjet, String zone) {
        return new Specification<Annonce>() {
            @Override
            public Predicate toPredicate(Root<Annonce> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                // Check for the main keyword in title or description
                if (StringUtils.isNotBlank(keyword)) {
                    Predicate titlePredicate = cb.like(cb.lower(root.get("titre")), "%" + keyword.toLowerCase() + "%");
                    Predicate descriptionPredicate = cb.like(cb.lower(root.get("description")),
                            "%" + keyword.toLowerCase() + "%");
                    predicates.add(cb.or(titlePredicate, descriptionPredicate));
                }

                // Check for keywords from the keywords list
                if (keywordslist != null && !keywordslist.isEmpty()) {
                    List<Predicate> keywordPredicates = new ArrayList<>();
                    for (String kw : keywordslist) {
                        if (StringUtils.isNotBlank(kw)) {
                            // Apply the search to the keywords field for each keyword in the list
                            keywordPredicates.add(
                                    cb.isMember(kw.trim(), root.get("keywords")));
                        }
                    }
                    if (!keywordPredicates.isEmpty()) {
                        // Combine all keyword predicates using OR logic
                        predicates.add(cb.or(keywordPredicates.toArray(new Predicate[0])));
                    }
                }

                // Check for 'etatObjet' being NULL or in the provided list (listEtatObjet)
                if (listEtatObjet != null && !listEtatObjet.isEmpty()) {
                    Predicate etatPredicate = cb.or(
                            cb.isNull(root.get("etatObjet")),
                            root.get("etatObjet").in(listEtatObjet) // Using "IN" for list-based filtering
                    );
                    predicates.add(etatPredicate);
                }

                // Check for the zone field matching the provided zone value
                if (StringUtils.isNotBlank(zone)) {
                    Predicate zonePredicate = cb.like(cb.lower(root.get("zone")),
                            "%" + zone.toLowerCase() + "%");
                    predicates.add(zonePredicate);
                }

                // Combine all predicates (title/description search + keyword list + etatObjet
                // filter + zone filter)
                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
    }
}
