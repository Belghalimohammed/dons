package com.isima.dons.entities;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;

@Entity
public class Recherche {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private String searchTerm;

    @ElementCollection
    @CollectionTable(name = "recherche_keywords", joinColumns = @JoinColumn(name = "recherche_id"))
    @Column(name = "keyword")
    private List<String> keywordsList;

    @ElementCollection
    @CollectionTable(name = "recherche_etats", joinColumns = @JoinColumn(name = "recherche_id"))
    @Column(name = "etat_objet")
    private List<String> etatObjetList;

    private String zone;

    public Recherche() {
    }

    public Recherche(Long id, User user, String searchTerm, List<String> keywordsList, List<String> etatObjetList,
            String zone) {
        this.id = id;
        this.user = user;
        this.searchTerm = searchTerm;
        this.keywordsList = keywordsList;
        this.etatObjetList = etatObjetList;
        this.zone = zone;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public List<String> getKeywordsList() {
        return keywordsList;
    }

    public void setKeywordsList(List<String> keywordsList) {
        this.keywordsList = keywordsList;
    }

    public List<String> getEtatObjetList() {
        return etatObjetList;
    }

    public void setEtatObjetList(List<String> etatObjetList) {
        this.etatObjetList = etatObjetList;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Recherche recherche = (Recherche) o;
        return Objects.equals(user, recherche.user) &&
                Objects.equals(searchTerm, recherche.searchTerm) &&
                Objects.equals(keywordsList, recherche.keywordsList) &&
                Objects.equals(etatObjetList, recherche.etatObjetList) &&
                Objects.equals(zone, recherche.zone);
    }

}
