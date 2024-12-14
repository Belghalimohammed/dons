package com.isima.dons.entities;

import jakarta.persistence.*;

@Entity
public class NotificationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Boolean seen = false;  // Default to false when a new NotificationUser is created

    // Constructors
    public NotificationUser() {}

    public NotificationUser(Notification notification, User user) {
        this.notification = notification;
        this.user = user;
    }

    public NotificationUser(Notification notification, User user, Boolean seen) {
        this.notification = notification;
        this.user = user;
        this.seen = seen;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }
}

