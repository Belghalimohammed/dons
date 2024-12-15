package com.isima.dons.controllers.api;

import com.isima.dons.entities.User;
import com.isima.dons.services.UserService;

import java.beans.Encoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthApiController {

    private final UserService userService;

    @Autowired
    public AuthApiController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        return userService.verify(user);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        // Create a new User object based on the signup request
        User newUser = new User();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(encoder.encode(user.getPassword()));

        // Try to create the user
        User createdUser = userService.createUser(newUser);
        if (createdUser == null) {
            // Return a 400 Bad Request with an error message if the user already exists
            return new ResponseEntity<>("User already exists", HttpStatus.BAD_REQUEST);
        }

        // Return a success message with a 201 Created status
        return new ResponseEntity<>("Signup successful! Please log in.", HttpStatus.CREATED);
    }
}
