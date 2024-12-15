package com.isima.dons.entities;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "annonce_id", nullable = false)
    private Annonce annonce;

    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationUser> notificationUsers;

    @Column(nullable = false)
    private Date date;

    @Column(nullable = false, length = 500)
    private String content;

    // Constructors
    public Notification() {
    }

    public Notification(Annonce annonce, List<NotificationUser> notificationUsers, Date date, String content) {
        this.annonce = annonce;
        this.notificationUsers = notificationUsers;
        this.date = date;
        this.content = content;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Annonce getAnnonce() {
        return annonce;
    }

    public void setAnnonce(Annonce annonce) {
        this.annonce = annonce;
    }

    public List<NotificationUser> getNotificationUsers() {
        return notificationUsers;
    }

    public void setNotificationUsers(List<NotificationUser> notificationUsers) {
        this.notificationUsers = notificationUsers;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
