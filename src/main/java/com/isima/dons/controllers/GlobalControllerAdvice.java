package com.isima.dons.controllers;

import com.isima.dons.configuration.UserPrincipale;
import com.isima.dons.entities.User;
import com.isima.dons.services.NotificationService;
import com.isima.dons.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@Profile("!test")
public class GlobalControllerAdvice {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addNotificationCountToModel(Model model, Authentication authentication) {
        if (authentication != null) {
            // Assuming you have a method to get the current logged-in user's ID
            UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
            User user = userService.getUserById(userPrincipale.getId());
            Long notificationCount = notificationService.getNotificationCountForUser(user.getId());

            // Add the notification count to the model for every request
            model.addAttribute("notificationCount", notificationCount);
        }
    }
}
