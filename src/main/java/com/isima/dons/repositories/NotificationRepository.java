package com.isima.dons.repositories;

import com.isima.dons.entities.Notification;
import com.isima.dons.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    @Query("SELECT n FROM Notification n JOIN n.notificationUsers nu WHERE nu.user = :user")
    List<Notification> findNotificationByUser(@Param("user") User user);

    @Query("SELECT COUNT(n) FROM Notification n JOIN n.notificationUsers nu WHERE nu.user.id = :userId and nu.seen=false")
    long countByUsers_Id(@Param("userId") Long userId);

}
