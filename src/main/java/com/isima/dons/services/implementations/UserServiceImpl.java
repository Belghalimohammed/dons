package com.isima.dons.services.implementations;

import com.isima.dons.entities.Annonce;
import com.isima.dons.entities.User;
import com.isima.dons.repositories.AnnonceRepository;
import com.isima.dons.repositories.UserRepository;
import com.isima.dons.services.JwtService;
import com.isima.dons.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtservice;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Override
    public User createUser(User user) {
        // Check if the user already exists by username or other unique field
        if (userRepository.existsByUsername(user.getUsername())) {
            return null; // Return null if the user already exists
        }

        // Otherwise, save and return the new user
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPassword(updatedUser.getPassword());
            return userRepository.save(existingUser);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @Override
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @Override
    public User login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent() && userOptional.get().getPassword().equals(password)) {
            return userOptional.get();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
    }

    @Override
    public User addFavorisToUserUser(long id, Long annonceId) {
        Optional<User> userOptional = userRepository.findById(id);
        Optional<Annonce> annonceOptional = annonceRepository.findById(annonceId);
        if (userOptional.isPresent() && annonceOptional.isPresent()) {
            userOptional.get().addFavoris(annonceOptional.get());

            return userRepository.save(userOptional.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @Override
    public User removeFavorisToUserUser(long id, Long annonceId) {
        Optional<User> userOptional = userRepository.findById(id);
        Optional<Annonce> annonceOptional = annonceRepository.findById(annonceId);
        if (userOptional.isPresent() && annonceOptional.isPresent()) {
            userOptional.get().removeFavoris(annonceOptional.get());

            return userRepository.save(userOptional.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @Override
    public String verify(User user) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        if (authentication.isAuthenticated()) {
            return jwtservice.generateToken(user.getUsername());
        }
        return "nope";
    }
}
