package com.isima.dons.controllers;

import com.isima.dons.configuration.UserPrincipale;
import com.isima.dons.entities.Annonce;
import com.isima.dons.entities.Groupe;
import com.isima.dons.entities.User;
import com.isima.dons.repositories.GroupeRepository;
import com.isima.dons.services.AnnonceService;
import com.isima.dons.services.GroupeService;
import com.isima.dons.services.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private AnnonceService annonceService;

    @Autowired
    private GroupeRepository groupeRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        return "redirect:/annonces";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("content", "pages/about");
        return "home";
    }

    @GetMapping("/modal/{annonceId}")
    public String modal(@PathVariable Long annonceId,
            @RequestParam(value = "oldPath", required = false, defaultValue = "/") String oldPath, Model model,
            HttpSession session) {
        // model.addAttribute("modal", "fragments/modal");

        Model sessionModel = (Model) session.getAttribute("model");
        model.addAllAttributes(sessionModel.asMap());

        model.addAttribute("modal", "fragments/modal");
        model.addAttribute("annonceId", annonceId);
        // System.out.println("////////////////////" + content);
        // model.addAttribute("content", "pages/contact");
        model.addAttribute("oldPath", oldPath);
        return "home";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("content", "pages/contact");
        return "home";
    }

}
