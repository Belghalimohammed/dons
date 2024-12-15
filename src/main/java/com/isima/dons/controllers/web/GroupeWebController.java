package com.isima.dons.controllers.web;

import com.isima.dons.configuration.UserPrincipale;
import com.isima.dons.entities.Annonce;
import com.isima.dons.entities.Groupe;
import com.isima.dons.entities.User;
import com.isima.dons.services.GroupeService;
import com.isima.dons.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

        // Use service to get the groupes and annonces
        List<Groupe> groupes = groupeService.getGroupeByAcheteurAndNotTaken(user.getId());

        if (!groupes.isEmpty()) {
            model.addAttribute("groupes", groupes);
            model.addAttribute("content", "pages/groupes/NonvalideGroup");
            return "home";
        } else {
            return "redirect:/";

        }
    }

    @PostMapping("/access-group")
    public String getAllAnnoncesDuGroupe(@RequestParam("groupeId") Long groupeId, Model model,
            Authentication authentication) {
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipale.getId());

        // Use service to get the groupes and annonces
        Groupe groupe = groupeService.getGroupeById(groupeId);
        List<Annonce> annonces = groupe.getAnnonces();
        model.addAttribute("groupe", groupe);
        model.addAttribute("annonces", annonces);
        model.addAttribute("content", "pages/groupes/groupe");
        return "home";
    }

    @GetMapping("/valide")
    public String valide(Model model, Authentication authentication) {
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipale.getId());

        // Use service to get the groupes
        List<Groupe> groupes = groupeService.getGroupeByAcheteurAndTaken(user.getId());

        // Add the groupes to the model and return the view
        model.addAttribute("groupes", groupes); // Pass groupes to the model
        model.addAttribute("content", "pages/groupes/valideGroup");
        return "home";
    }

    @PostMapping
    public String createGroupe(@RequestParam("annonceId") Long annonceId, Authentication authentication) {

        // Get the authenticated user
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipale.getId());

        // Create the groupe
        groupeService.createGroupe(annonceId, user.getId());

        // Redirect to /group after the group is created
        return "redirect:/groupes";
    }

    @PostMapping("/validate")
    public String validateGroupe(@RequestParam Long groupeId, Authentication authentication) {
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        Long achteur = userDetails.getId();
        groupeService.validateGroupe(groupeId, achteur);
        return "redirect:/groupes/valide";
    }

    @PostMapping("/remove")
    public String removeAnnonceFromGroupe(
            @RequestParam Long groupeId,
            @RequestParam Long annonceId) {
        boolean isRemoved = groupeService.removeAnnonceFromGroupe(groupeId, annonceId);

        if (isRemoved) {

            return "redirect:/groupes";
        } else {
            return null;
        }
    }
}
