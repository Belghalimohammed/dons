package com.isima.dons.services;

import com.isima.dons.entities.User;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(Long id);

    User createUser(User user);

    User updateUser(Long id, User updatedUser);
   
    
    void deleteUser(Long id);

    User login(String email, String password); // New method for login
}
