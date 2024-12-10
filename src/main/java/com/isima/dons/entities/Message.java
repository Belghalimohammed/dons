package com.isima.dons.entities;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @ManyToOne
    private User sender;

    @ManyToOne
    private User reciever;

    private LocalDateTime sentDate; // New field for message sent date

    public Message(Long id, String message, User sender, User reciever, LocalDateTime sentDate) {
        this.id = id;
        this.message = message;
        this.sender = sender;
        this.reciever = reciever;
        this.sentDate = sentDate;
    }

    public Message() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setReciever(User reciever) {
        this.reciever = reciever;
    }

    public void setSentDate(LocalDateTime sentDate) {
        this.sentDate = sentDate;
    }
}
