package com.isima.dons.controllers.web;

import com.isima.dons.configuration.SessionUtils;
import com.isima.dons.configuration.UserPrincipale;
import com.isima.dons.entities.Annonce;
import com.isima.dons.entities.FilterCriteria;
import com.isima.dons.entities.Groupe;
import com.isima.dons.entities.Recherche;
import com.isima.dons.entities.User;
import com.isima.dons.repositories.GroupeRepository;
import com.isima.dons.services.AnnonceService;
import com.isima.dons.services.GroupeService;
import com.isima.dons.services.RechercheService;
import com.isima.dons.services.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recherches")
public class RechercheWebController {

    @Autowired
    private AnnonceService annonceService;

    @Autowired
    private HttpSession session;

    @Autowired
    private GroupeRepository groupeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RechercheService rechercheservice;

    @GetMapping("/clearFilters")
    public String clearSession(HttpSession session) {
        // Clear all session attributes except SPRING_SECURITY_CONTEXT
        SessionUtils.clearSessionExcept(session, "SPRING_SECURITY_CONTEXT");
        return "redirect:/annonces"; // Redirect to a relevant page
    }

    @GetMapping("/save")
    public String save(@RequestParam Map<String, String> params, HttpSession session, Model model) {

        for (Map.Entry<String, String> entry : params.entrySet()) {
            session.setAttribute(entry.getKey(), entry.getValue());
            System.out.println("+" + entry.getKey() + "++" + entry.getValue());
        }

        session.setAttribute("neuf", params.getOrDefault("neuf", "false"));
        session.setAttribute("commeNeuf", params.getOrDefault("commeNeuf", "false"));
        session.setAttribute("tresBonEtat", params.getOrDefault("tresBonEtat", "false"));
        session.setAttribute("bonEtat", params.getOrDefault("bonEtat", "false"));
        session.setAttribute("etatCorrect", params.getOrDefault("etatCorrect", "false"));
        session.setAttribute("occasion", params.getOrDefault("occasion", "false"));

        @SuppressWarnings("unchecked")
        List<String> items = (List<String>) session.getAttribute("items");

        if (items == null) {
            items = new ArrayList<>();
        }

        session.setAttribute("items", items);
        return "redirect:/annonces";
    }

    @PostMapping("/addkeywordfilter")
    public String addKeyword(@RequestParam String key, HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        List<String> items = (List<String>) session.getAttribute("items");

        if (items == null) {
            items = new ArrayList<>();
        }

        items.add(key);
        session.setAttribute("items", items);
        return "redirect:/annonces?addkeyword=true";
    }

}
