package com.isima.dons.entities;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Entity
public class Groupe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public List<Annonce> getAnnonces() {
        return annonces;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Long getId() {
        return id;
    }

    private Date creationDate;

    public Date getValidationDate() {
        return validationDate;
    }

    public void setValidationDate(Date validationDate) {
        this.validationDate = validationDate;
    }

    private Date validationDate;

    // ManyToMany relationship with CascadeType.PERSIST to automatically persist Annonce when saving Groupe
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "groupe_annonces",
            joinColumns = @JoinColumn(name = "groupe_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "annonce_id", referencedColumnName = "id")
    )
    private List<Annonce> annonces = new ArrayList<>();

    public User getAcheteur() {
        return acheteur;
    }

    public boolean isPri() {
        return pri;
    }

    @ManyToOne
    private User acheteur;

    private boolean pri;

    public Groupe() {
    }

    // Constructor
    public Groupe(Long id, List<Annonce> annonces, User acheteur) {
        this.id = id;
        this.annonces = annonces;
        this.acheteur = acheteur;
    }

    // Getters and Setters (Lombok will generate these automatically, but it's good practice to define them explicitly if needed)
    public void setId(Long id) {
        this.id = id;
    }

    public void setAnnonces(List<Annonce> annonces) {
        this.annonces = annonces;
    }

    public void setAcheteur(User acheteur) {
        this.acheteur = acheteur;
    }

    public void setPri(boolean pri) {
        this.pri = pri;
    }
}
