package com.isima.dons.controllers;

import com.isima.dons.entities.User;
import com.isima.dons.services.UserService;

import java.beans.Encoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

     @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
    
    
    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username, @RequestParam String email, @RequestParam String password, Model model) {
        User newUser = new User();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(encoder.encode(password));
        System.out.println(newUser);
        userService.createUser(newUser);
        model.addAttribute("message", "Signup successful! Please log in.");
        return "redirect:/login";
    }
    
}

