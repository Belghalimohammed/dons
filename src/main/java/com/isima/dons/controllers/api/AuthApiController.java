package com.isima.dons.controllers.api;

import com.isima.dons.entities.User;
import com.isima.dons.services.UserService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthApiController {

    private final UserService userService;

    public AuthApiController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody User user) {
        // Verify the user credentials using the userService
        String token = userService.verify(user);

        if (token == null || token.isEmpty()) {
            // If token is null or empty, return an error message (Unauthorized)
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Invalid username or password");
            response.put("status", HttpStatus.UNAUTHORIZED.value());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // If successful, return a success message along with the token (200 OK)
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("status", HttpStatus.OK.value());
        response.put("token", token); // Assuming the token is returned after successful login

        return ResponseEntity.ok(response);
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
