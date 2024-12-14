package com.isima.dons.controllers.web;

import com.isima.dons.configuration.UserPrincipale;
import com.isima.dons.entities.Notification;
import com.isima.dons.entities.User;
import com.isima.dons.services.NotificationService;
import com.isima.dons.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/notification")
public class NotificationWebController {

    @Autowired
    NotificationService notifservice;

    @Autowired
    UserService userService;

    @GetMapping("/display")
    public String displayNotifAndRedirect() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userDetails.getId());

        // Mark notifications as seen for this user
        notifservice.markNotificationsAsSeen(user);

        // Redirect to the actual API that fetches and displays notifications
        return "redirect:/notification/notifications";
    }

    @GetMapping("/notifications")
    public String displayNotifications(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userDetails.getId());

        // Retrieve notifications for the user
        List<Notification> notifications = notifservice.getNotification(user);

        model.addAttribute("notifications", notifications);
        model.addAttribute("content", "pages/notification");
        return "home";
    }


}
