package com.isima.dons.controllers;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

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

        Model sessionModel = (Model) session.getAttribute("model");
        model.addAllAttributes(sessionModel.asMap());

        model.addAttribute("modal", "fragments/modal");
        model.addAttribute("annonceId", annonceId);
        model.addAttribute("oldPath", oldPath);
        return "home";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("content", "pages/contact");
        return "home";
    }

}
