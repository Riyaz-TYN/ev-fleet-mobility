package com.complaint_resolution.auth.controller;
import com.complaint_resolution.auth.entity.User;

import com.complaint_resolution.auth.dto.LoginRequest;
import com.complaint_resolution.common.security.JwtUtil;
import com.complaint_resolution.auth.service.AuthService;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {

        Map<String, String> response = new HashMap<>();

        try {

            User user = authService.validateLogin(
                    request.getEmail(),
                    request.getPassword()
            );

            // Get customerId from DB
            String customerId = user.getCustomerId();

            //  Generate token with customerId
            String token = jwtUtil.generateToken(user.getEmail(), customerId);

            response.put("status", "success");
            response.put("token", token);
            response.put("customerId", customerId); // optional
            response.put("message", "Login successful");

        } catch (Exception e) {

            response.put("status", "error");
            response.put("message", e.getMessage());
        }

        return response;
    }
}