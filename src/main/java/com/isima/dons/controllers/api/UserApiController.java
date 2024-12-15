package com.isima.dons.controllers.api;

import com.isima.dons.entities.User;
import com.isima.dons.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    @Autowired
    private UserService userService;

    // Fetch all users
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        Map<String, Object> response = new HashMap<>();
        if (users.isEmpty()) {
            response.put("status", "fail");
            response.put("message", "No users found.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response); // No users found
        } else {
            response.put("status", "success");
            response.put("data", users);
            return ResponseEntity.ok(response); // Return users list
        }
    }

    // Get user by id
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        Map<String, Object> response = new HashMap<>();
        if (user == null) {
            response.put("status", "fail");
            response.put("message", "User not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // User not found
        } else {
            response.put("status", "success");
            response.put("data", user);
            return ResponseEntity.ok(response); // Return the user
        }
    }

    // Create a new user
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // Return the created user
    }

    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        User updated = userService.updateUser(id, updatedUser);
        Map<String, Object> response = new HashMap<>();
        if (updated == null) {
            response.put("status", "fail");
            response.put("message", "User not found or update failed.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // User not found
        } else {
            response.put("status", "success");
            response.put("data", updated);
            return ResponseEntity.ok(response); // Return updated user
        }
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        boolean isDeleted = userService.deleteUser(id);
        Map<String, Object> response = new HashMap<>();
        if (isDeleted) {
            response.put("status", "success");
            response.put("message", "User deleted successfully.");
            return ResponseEntity.noContent().build(); // No Content for successful deletion
        } else {
            response.put("status", "fail");
            response.put("message", "User not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // User not found
        }
    }
}
