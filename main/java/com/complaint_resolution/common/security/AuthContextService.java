package com.complaint_resolution.common.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthContextService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private HttpServletRequest request;

    // EXTRACT TOKEN FROM AUTH HEADER
    private String getToken() {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            return authHeader.substring(7);
        }

        return null;
    }

    // GET CURRENT ROLE
    public String getCurrentRole() {

        String token = getToken();

        if (token == null) {
            return null;
        }

        return jwtUtil.extractRole(token);
    }

    // GET CURRENT SUBJECT
    public String getCurrentSubject() {

        String token = getToken();

        if (token == null) {
            return null;
        }

        return jwtUtil.extractSubject(token);
    }

    // GET CURRENT USER ID
    public Long getCurrentUserId() {

        String token = getToken();

        if (token == null) {
            return null;
        }

        return jwtUtil.extractUserId(token);
    }
}