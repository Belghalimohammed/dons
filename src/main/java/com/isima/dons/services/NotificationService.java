package com.isima.dons.services;

import com.isima.dons.entities.Annonce;
import com.isima.dons.entities.Notification;
import com.isima.dons.entities.User;

import java.util.List;

public interface NotificationService {

    Notification pushNotification(Annonce annonce,Long userId);

    List<Notification> getNotification(User user);

    Long getNotificationCountForUser(Long userId);

    void markNotificationsAsSeen(User user);
}
