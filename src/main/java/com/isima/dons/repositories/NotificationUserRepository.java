package com.isima.dons.repositories;

import com.isima.dons.entities.NotificationUser;
import com.isima.dons.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationUserRepository extends JpaRepository<NotificationUser, Long> {
    List<NotificationUser> findByUserAndSeenFalse(User user);
}
