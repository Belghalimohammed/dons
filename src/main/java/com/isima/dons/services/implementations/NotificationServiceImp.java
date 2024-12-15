package com.isima.dons.services.implementations;

import com.isima.dons.entities.*;
import com.isima.dons.repositories.NotificationRepository;
import com.isima.dons.repositories.NotificationUserRepository;
import com.isima.dons.services.NotificationService;
import com.isima.dons.services.RechercheService;
import com.isima.dons.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImp implements NotificationService {

    @Autowired
    private RechercheService rechercheService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationUserRepository notificationUserRepository;

    @Override
    public Notification pushNotification(Annonce annonce, Long userId) {
        User user = userService.getUserById(userId);
        Notification notification = new Notification();
        notification.setAnnonce(annonce);
        notification.setContent("une nouvelle annonce a été créée par " + annonce.getVendeur().getUsername() + ": "
                + annonce.getTitre());
        notification.setDate(new Date());

        // Get the users concerned by the notification
        List<Recherche> recherchesAnnonce = rechercheService.getRechercheByAnnonce(annonce);
        List<User> usersConcerne = recherchesAnnonce.stream()
                .map(Recherche::getUser)
                .distinct()
                .collect(Collectors.toList());

        // Remove the user from the list of concerned users
        usersConcerne = usersConcerne.stream()
                .filter(u -> !u.getId().equals(userId)) // Exclude the user with the given ID
                .collect(Collectors.toList());

        // Create NotificationUser entities and set 'seen' to false for each user
        List<NotificationUser> notificationUsers = usersConcerne.stream()
                .map(u -> new NotificationUser(notification, u, false)) // Create NotificationUser for each user
                .collect(Collectors.toList());

        // Set the NotificationUser list to the Notification
        notification.setNotificationUsers(notificationUsers);

        // Save the notification (this will also save the associated NotificationUser
        // entities)
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotification(User user) {
        System.out.println(user.getUsername());
        List<Notification> notifications = notificationRepository.findNotificationByUser(user);
        System.out.println(notifications);
        return notifications;
    }

    @Override
    public Long getNotificationCountForUser(Long userId) {
        return notificationRepository.countByUsers_Id(userId);
    }

    @Transactional
    public void markNotificationsAsSeen(User user) {
        List<NotificationUser> notificationUsers = notificationUserRepository.findByUserAndSeenFalse(user);
        for (NotificationUser notificationUser : notificationUsers) {
            notificationUser.setSeen(true);
        }
        notificationUserRepository.saveAll(notificationUsers);
    }

    @Override
    public boolean deleteNotification(Long notificationId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        if (notification.isPresent()) {
            notificationRepository.delete(notification.get());
            return true;
        }
        return false;
    }

    @Override
    public Notification getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId).orElse(null); // Return null if not found
    }
}
