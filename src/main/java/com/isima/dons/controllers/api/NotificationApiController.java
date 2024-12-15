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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> markNotificationsAsSeen(Authentication authentication) {
        // Get the authenticated user
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userDetails.getId());

        // Mark notifications as seen
        notifservice.markNotificationsAsSeen(user);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Notifications marked as seen.");
        return ResponseEntity.noContent().build(); // No Content as there's no data to return
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getNotifications(Authentication authentication) {
        // Get the authenticated user
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userDetails.getId());

        // Retrieve notifications for the user
        List<Notification> notifications = notifservice.getNotification(user);

        Map<String, Object> response = new HashMap<>();
        if (notifications.isEmpty()) {
            response.put("status", "fail");
            response.put("message", "No notifications found.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response); // No notifications found
        } else {
            response.put("status", "success");
            response.put("data", notifications);
            return ResponseEntity.ok(response); // Return notifications list
        }
    }

    // Get a specific notification by ID
    @GetMapping("/{notificationId}")
    public ResponseEntity<Map<String, Object>> getNotificationById(@PathVariable Long notificationId) {
        // Fetch the notification by ID
        Notification notification = notifservice.getNotificationById(notificationId);

        Map<String, Object> response = new HashMap<>();
        if (notification == null) {
            response.put("status", "fail");
            response.put("message", "Notification not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // Notification not found
        }

        response.put("status", "success");
        response.put("data", notification);
        return ResponseEntity.ok(response); // Return the notification
    }

    // Delete a notification by ID
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Map<String, Object>> deleteNotification(@PathVariable Long notificationId) {
        // Delete the notification
        boolean isDeleted = notifservice.deleteNotification(notificationId);

        Map<String, Object> response = new HashMap<>();
        if (isDeleted) {
            response.put("status", "success");
            response.put("message", "Notification deleted successfully.");
            return ResponseEntity.noContent().build(); // No Content for successful deletion
        } else {
            response.put("status", "fail");
            response.put("message", "Notification not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // Notification not found
        }
    }
}
