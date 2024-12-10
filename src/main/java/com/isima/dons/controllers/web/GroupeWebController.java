package com.isima.dons.controllers.web;

import com.isima.dons.configuration.UserPrincipale;
import com.isima.dons.entities.Annonce;
import com.isima.dons.entities.Groupe;
import com.isima.dons.entities.User;
import com.isima.dons.services.GroupeService;
import com.isima.dons.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/groupes")
public class GroupeWebController {

    @Autowired
    private GroupeService groupeService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String getAll(Model model, Authentication authentication) {
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipale.getId());
        System.out.println(user.getUsername());
        System.out.println("the user ID " + user.getId());

        // Use service to get the groupes and annonces
        List<Groupe> groupe = groupeService.getGroupeByAcheteurAndNotTaken(user.getId());
        List<Annonce> annonces = groupeService.getAnnoncesFromGroupe(user.getId());

        if (!groupe.isEmpty()) {
            System.out.println("not empty ");
            model.addAttribute("groupe", groupe.get(0));
            model.addAttribute("annonces", annonces); // Pass annonces to the model
            model.addAttribute("content", "pages/groupes/groupe");
            return "home";
        } else {
            return "redirect:/";

        }
    }

    @GetMapping("/valide")
    public String valide(Model model, Authentication authentication) {
        System.out.println("validééééééééé");
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipale.getId());
        System.out.println(user.getUsername());

        // Use service to get the groupes
        List<Groupe> groupes = groupeService.getGroupeByAcheteurAndTaken(user.getId());

        // Add the groupes to the model and return the view
        model.addAttribute("groupes", groupes); // Pass groupes to the model
        model.addAttribute("content", "pages/groupes/valideGroup");
        return "home";
    }

    @PostMapping
    public String createGroupe(@RequestParam("annonceId") Long annonceId, Authentication authentication) {
        System.out.println(annonceId);

        // Get the authenticated user
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipale.getId());
        System.out.println(user.getUsername());

        // Create the groupe
        groupeService.createGroupe(annonceId, user.getId());

        // Redirect to /group after the group is created
        return "redirect:/groupes";
    }

    @PostMapping("/validate")
    public String validateGroupe(@RequestParam Long groupeId) {
        groupeService.validateGroupe(groupeId);
        return "redirect:/groupes/valide";
    }

    @DeleteMapping
    public String removeAnnonceFromGroupe(
            @RequestParam Long groupeId,
            @RequestParam Long annonceId) {
        System.out.println("groupeId: " + groupeId);
        System.out.println("annonceId: " + annonceId);
        boolean isRemoved = groupeService.removeAnnonceFromGroupe(groupeId, annonceId);

        if (isRemoved) {

            System.out.println("delllleeeted ");
            return "redirect:/groupes";
        } else {
            return null;
        }
    }
}
