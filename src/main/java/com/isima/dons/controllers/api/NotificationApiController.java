package com.isima.dons.controllers.api;

import com.isima.dons.configuration.UserPrincipale;
import com.isima.dons.entities.Notification;
import com.isima.dons.entities.User;
import com.isima.dons.services.NotificationService;
import com.isima.dons.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationApiController {

    @Autowired
    private NotificationService notifservice;

    @Autowired
    private UserService userService;

    // Mark notifications as seen and return the updated list of notifications for
    // the user
    @PutMapping("/markAsSeen")
    public ResponseEntity<Void> markNotificationsAsSeen(Authentication authentication) {
        // Get the authenticated user
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userDetails.getId());

        // Mark notifications as seen
        notifservice.markNotificationsAsSeen(user);

        // Return a 204 No Content status indicating successful operation
        return ResponseEntity.noContent().build();
    }

    // Get all notifications for the authenticated user
    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(Authentication authentication) {
        // Get the authenticated user
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userDetails.getId());

        // Retrieve notifications for the user
        List<Notification> notifications = notifservice.getNotification(user);

        if (notifications.isEmpty()) {
            // If no notifications found, return 204 No Content
            return ResponseEntity.noContent().build();
        } else {
            // Return the notifications list with a 200 OK status
            return ResponseEntity.ok(notifications);
        }
    }

    // Get a specific notification by ID
    @GetMapping("/{notificationId}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long notificationId) {
        // Fetch the notification by ID
        Notification notification = notifservice.getNotificationById(notificationId);

        if (notification == null) {
            // If the notification is not found, return a 404 Not Found response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Return the notification with a 200 OK status
        return ResponseEntity.ok(notification);
    }

    // Delete a notification by ID
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long notificationId) {
        // Delete the notification
        boolean isDeleted = notifservice.deleteNotification(notificationId);

        if (isDeleted) {
            // Return 204 No Content if the deletion was successful
            return ResponseEntity.noContent().build();
        } else {
            // Return 404 Not Found if the notification doesn't exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
