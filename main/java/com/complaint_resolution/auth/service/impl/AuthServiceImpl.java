package com.complaint_resolution.auth.service.impl;


import com.complaint_resolution.auth.service.AuthService;
import com.complaint_resolution.auth.entity.User;
import com.complaint_resolution.auth.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Validate login and return full user (needed for customerId)
    public User validateLogin(String email, String password) {

        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            throw new RuntimeException("Email or Password cannot be empty");
        }

        // Fetch user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not registered"));

        // Validate password
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        return user; // return user to extract customerId in controller
    }
}