package com.poojamart.service;

import com.poojamart.dto.AuthRequest;
import com.poojamart.dto.AuthResponse;
import com.poojamart.dto.RegisterRequest;
import com.poojamart.model.User;
import com.poojamart.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResponse login(AuthRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail().trim().toLowerCase());
        if (userOpt.isEmpty()) {
            return AuthResponse.failure("Invalid email or password");
        }

        User user = userOpt.get();
        if (!user.getPassword().equals(request.getPassword())) {
            return AuthResponse.failure("Invalid email or password");
        }

        String token = "PMT_" + UUID.randomUUID().toString().replace("-", "");
        return AuthResponse.success("Login successful", token, user);
    }

    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            return AuthResponse.failure("Email is already registered. Please login.");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setState(request.getState());
        user.setPostalCode(request.getPostalCode());
        user.setRole("ROLE_USER");

        User savedUser = userRepository.save(user);
        String token = "PMT_" + UUID.randomUUID().toString().replace("-", "");
        return AuthResponse.success("Registration successful! Welcome to PoojaMart Toys", token, savedUser);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUserProfile(Long id, User updatedInfo) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (updatedInfo.getName() != null) user.setName(updatedInfo.getName());
        if (updatedInfo.getPhone() != null) user.setPhone(updatedInfo.getPhone());
        if (updatedInfo.getAddress() != null) user.setAddress(updatedInfo.getAddress());
        if (updatedInfo.getCity() != null) user.setCity(updatedInfo.getCity());
        if (updatedInfo.getState() != null) user.setState(updatedInfo.getState());
        if (updatedInfo.getPostalCode() != null) user.setPostalCode(updatedInfo.getPostalCode());
        if (updatedInfo.getPassword() != null && !updatedInfo.getPassword().isBlank()) {
            user.setPassword(updatedInfo.getPassword());
        }

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public long getTotalUserCount() {
        return userRepository.count();
    }
}
