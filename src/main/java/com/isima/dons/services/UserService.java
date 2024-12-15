package com.isima.dons.services;

import com.isima.dons.entities.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(Long id);

    User createUser(User user);

    User updateUser(Long id, User updatedUser);

    String verify(User user);

    User addFavorisToUserUser(long id, Long annonceId);

    User removeFavorisToUserUser(long id, Long annonceId);

    Boolean deleteUser(Long id);

    User login(String email, String password); // New method for login
}
