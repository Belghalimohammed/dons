package com.isima.dons.entities;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Entity
public class Annonce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    @Column(length = 500)
    private String description;



    

    public Annonce() {

    }

    public Annonce(Long id, String titre, String description, EtatObjet etatObjet, LocalDate datePublication, String zone, boolean typeDon, boolean pri, User vendeur, List<String> keywords) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.etatObjet = etatObjet;
        this.datePublication = datePublication;
        this.zone = zone;
        this.typeDon = typeDon;
        this.vendeur = vendeur;
        this.keywords = keywords;
        this.pri = pri;
    }

    public Long getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    public EtatObjet getEtatObjet() {
        return etatObjet;
    }

    public LocalDate getDatePublication() {
        return datePublication;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public boolean isTypeDon() {
        return typeDon;
    }

    public User getVendeur() {
        return vendeur;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public enum EtatObjet {
        Neuf, CommeNeuf, TresBonEtat, BonEtat, EtatCorrect, Occasion
    }


    @Enumerated(EnumType.STRING)
    private EtatObjet etatObjet;

    private LocalDate datePublication;

    private String zone;

    private boolean typeDon;

    public boolean isPri() {
        return pri;
    }

    public void setPri(boolean pri) {
        this.pri = pri;
    }

    private boolean pri;

    public void setVendeur(User vendeur) {
        this.vendeur = vendeur;
    }

    @ManyToOne
    private User vendeur;

    @ElementCollection
    private List<String> keywords;

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEtatObjet(EtatObjet etatObjet) {
        this.etatObjet = etatObjet;
    }

    public void setDatePublication(LocalDate datePublication) {
        this.datePublication = datePublication;
    }


    public boolean getTypeDon() {
        return typeDon;
    }

    public void setTypeDon(boolean typeDon) {
        this.typeDon = typeDon;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}

