package com.isima.dons.repositories;

import com.isima.dons.entities.Message;
import com.isima.dons.entities.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE (m.sender.id = :senderId AND m.reciever.id = :receiverId) OR (m.sender.id = :receiverId AND m.reciever.id = :senderId)")
    List<Message> findMessagesBetweenUsers(Long senderId, Long receiverId);

    @Query("SELECT r FROM User r " +
            "JOIN Message m ON  ( ( r.id = m.reciever.id ) or ( r.id = m.sender.id ) ) " +
            "WHERE (m.sender.id = :sender OR m.reciever.id = :sender) " +
            "AND r.id != :sender " +
            "GROUP BY r.id " +
            "ORDER BY MAX(m.sentDate) DESC")
    List<User> findAllUsersInvolvedWithSender(Long sender);

}
