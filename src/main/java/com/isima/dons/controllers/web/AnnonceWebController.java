package com.isima.dons.controllers.web;

import java.time.LocalDate;
import com.isima.dons.entities.Annonce;
import com.isima.dons.entities.Recherche;
import com.isima.dons.entities.User;
import com.isima.dons.services.AnnonceService;
import com.isima.dons.services.RechercheService;
import com.isima.dons.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.isima.dons.configuration.UserPrincipale;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/annonces")
public class AnnonceWebController {

    private final AnnonceService annonceService;
    private final UserService userService;
    @Autowired
    private RechercheService rechercheservice;

    public AnnonceWebController(AnnonceService annonceService, UserService userService) {
        this.annonceService = annonceService;
        this.userService = userService;
    }

    @GetMapping
    public String home(
            HttpServletRequest request,
            @RequestParam(value = "addkeyword", required = false) String addkeyword,
            HttpSession session,
            Model model,
            Authentication authentication) {

        @SuppressWarnings("unchecked")
        List<String> items = (List<String>) session.getAttribute("items");

        if (items == null) {
            items = new ArrayList<>();
        }
        String zone = (String) session.getAttribute("zone");
        String key = (String) session.getAttribute("search");
        int page = session.getAttribute("page") == null ? 0
                : Integer.parseInt((String) session.getAttribute("page")) - 1;
        model.addAttribute("neuf", Boolean.parseBoolean((String) session.getAttribute("neuf")));
        model.addAttribute("commeNeuf", Boolean.parseBoolean((String) session.getAttribute("commeNeuf")));
        model.addAttribute("tresBonEtat", Boolean.parseBoolean((String) session.getAttribute("tresBonEtat")));
        model.addAttribute("bonEtat", Boolean.parseBoolean((String) session.getAttribute("bonEtat")));
        model.addAttribute("etatCorrect", Boolean.parseBoolean((String) session.getAttribute("etatCorrect")));
        model.addAttribute("occasion", Boolean.parseBoolean((String) session.getAttribute("occasion")));
        model.addAttribute("zone", zone);
        model.addAttribute("search", key);
        model.addAttribute("page", page + 1);
        model.addAttribute("items", items);

        String[] attributes = { "neuf", "commeNeuf", "tresBonEtat", "bonEtat", "etatCorrect", "occasion" };
        List<String> etatList = new ArrayList<>();
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipale.getId());

        for (String attribute : attributes) {
            if (Boolean.parseBoolean((String) session.getAttribute(attribute))) {
                etatList.add(attribute);
            }
        }
        if (addkeyword == null) {
            Recherche rch = new Recherche();
            rch.setEtatObjetList(etatList);
            rch.setKeywordsList(items);
            rch.setZone(zone);
            rch.setUser(user);
            rch.setSearchTerm(key);

            if (rch.getEtatObjetList().size() != 0 || rch.getKeywordsList().size() != 0 || rch.getSearchTerm() != null
                    || rch.getZone() != null) {
                if (!rechercheservice.exists(rch)) {
                    rechercheservice.createRecherche(rch);
                }

            }

        }

        Page<Annonce> annonces = annonceService.findFilteredAnnonces(key, zone, items, etatList, page);

        Long annoncesCount = annonceService.findFilteredAnnoncesCount(key, zone, items, etatList);
        List<String> zones = annonceService.findDistinctZones();
        model.addAttribute("zones", zones);
        model.addAttribute("pages", (int) Math.ceil((double) annoncesCount / 20));
        model.addAttribute("annonces", annonces);
        model.addAttribute("oldPath", request.getRequestURI());
        model.addAttribute("content", "pages/dashboard");
        model.addAttribute("favoris", user.getFavoris());

        model.addAttribute("filters", "fragments/filters");
        session.setAttribute("model", model);
        return "home";
    }

    @GetMapping("/add")
    public String addAnnoce(Model model, HttpSession session) {
        List<String> zones = annonceService.findDistinctZones();
        model.addAttribute("zones", zones);
        model.addAttribute("content", "pages/annonces/add-annonce");
        return "home";
    }

    @GetMapping("/mes-annonces")
    public String mesAnnonces(Model model, Authentication authentication) {
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipale.getId());
        List<Annonce> annonces = annonceService.getAnnoncesByUser(user.getId()); // Fetch annonces
        model.addAttribute("annonces", annonces); // Pass annonces to the model
        model.addAttribute("content", "pages/annonces/mes-annonces");
        return "home";
    }

    @GetMapping("/mes-achats")
    public String mesAchats(Model model, Authentication authentication) {
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipale.getId());

        List<Annonce> annonces = annonceService.getAnnoncesByAcheteur(user); // Fetch annonces
        model.addAttribute("annonces", annonces); // Pass annonces to the model
        model.addAttribute("content", "pages/annonces/mes-annonces");
        return "home";
    }

    @GetMapping("/{id}")
    public String getAnnonceDetails(@PathVariable Long id, Model model, HttpServletRequest request,
            HttpSession session) {
        Annonce annonce = annonceService.getAnnonceById(id); // Méthode pour récupérer l'annonce par son ID
        model.addAttribute("annonce", annonce);
        model.addAttribute("content", "pages/annonces/annonce-details");
        model.addAttribute("oldPath", request.getRequestURI());
        session.setAttribute("model", model);
        return "home"; // Le nom de la page Thymeleaf à afficher
    }

    @GetMapping("/vendeur/{id}")
    public String getAnnoncesByVendeurId(@PathVariable Long id, Model model, HttpServletRequest request,
            HttpSession session) {
        List<Annonce> annonces = annonceService.getAnnoncesByVendeurId(id);
        model.addAttribute("annonces", annonces);
        model.addAttribute("content", "pages/annonces/veundeur-annonces");
        model.addAttribute("oldPath", request.getRequestURI());
        session.setAttribute("model", model);

        return "home";
    }

    @PostMapping
    public String createAnnonce(@ModelAttribute Annonce annonce) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipale userDetails = (UserPrincipale) authentication.getPrincipal();
        annonce.setDatePublication(LocalDate.now());
        annonce.setVendeur(userService.getUserById(userDetails.getId()));
        annonce.setKeywords(Arrays.asList(annonce.getKeywords().get(0).split(" ")));
        annonceService.createAnnonce(annonce, userDetails.getId());

        return "redirect:/annonces/mes-annonces";
    }

    @PutMapping("/{id}")
    public String updateAnnonce(@PathVariable Long id, @ModelAttribute Annonce updatedAnnonce) {
        Annonce annonce = annonceService.getAnnonceById(id);

        updatedAnnonce.setDatePublication(annonce.getDatePublication());
        updatedAnnonce.setVendeur(annonce.getVendeur());
        updatedAnnonce.setKeywords(
                Arrays.asList(updatedAnnonce.getKeywords().get(0)
                        .replaceAll("\\s+", " ")
                        .trim()
                        .split(" ")));

        return "redirect:/";
    }

    @DeleteMapping("/{id}")
    public String deleteAnnonce(@PathVariable Long id) {
        annonceService.deleteAnnonce(id);
        return "redirect:/";
    }

    @GetMapping("/buy/{annonceId}")
    public String addAcheteur(@PathVariable Long annonceId,
            @RequestParam(value = "oldPath", required = false, defaultValue = "/") String oldPath,
            Authentication authentication) {
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipale.getId());
        annonceService.addAcheteurAndMarkAsPri(annonceId, user);
        return "redirect:" + oldPath; // Redirect to the annonce list or another appropriate view
    }

    @GetMapping("/addfavoris/{annonceId}")
    public String addFavoris(@PathVariable Long annonceId,
            @RequestParam(value = "oldPath", required = false, defaultValue = "/") String oldPath,
            Authentication authentication) {
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        userService.addFavorisToUserUser(userPrincipale.getId(), annonceId);
        return "redirect:" + oldPath; // Redirect to the annonce list or another appropriate view
    }

    @GetMapping("/removefavoris/{annonceId}")
    public String removeFavoris(@PathVariable Long annonceId,
            @RequestParam(value = "oldPath", required = false, defaultValue = "/") String oldPath,
            Authentication authentication) {
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        userService.removeFavorisToUserUser(userPrincipale.getId(), annonceId);
        return "redirect:" + oldPath; // Redirect to the annonce list or another appropriate view
    }

    @GetMapping("/favoris")
    public String listFavoris(Authentication authentication, Model model, HttpServletRequest request) {
        UserPrincipale userPrincipale = (UserPrincipale) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipale.getId());
        model.addAttribute("favoris", user.getFavoris());
        model.addAttribute("oldPath", request.getRequestURI());

        model.addAttribute("annonces", user.getFavoris());
        model.addAttribute("content", "pages/annonces/mes-annonces");
        return "home";
    }

}
