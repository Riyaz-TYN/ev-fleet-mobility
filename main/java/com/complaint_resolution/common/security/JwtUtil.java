package com.complaint_resolution.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // FIXED SECRET
    private static final String SECRET = "mysecretkeymysecretkeymysecretkey12345";

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // TOKEN GENERATION
    public String generateToken(String email, String customerId) {

        return Jwts.builder()
                .setSubject(email)
                .claim("customerId", customerId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key)
                .compact();
    }

    // COMMON CLAIM EXTRACTION
    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // EXTRACT CUSTOMER ID
    public String extractCustomerId(String token) {

        return extractAllClaims(token)
                .get("customerId", String.class);
    }

    // EXTRACT ROLE
    public String extractRole(String token) {

        return extractAllClaims(token)
                .getSubject();
    }

    // EXTRACT USER ID
    public Long extractUserId(String token) {

        Object userId = extractAllClaims(token)
                .get("userId");

        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }

        if (userId instanceof Long) {
            return (Long) userId;
        }

        return null;
    }

    // EXTRACT SUBJECT
    public String extractSubject(String token) {

        return extractAllClaims(token)
                .getSubject();
    }
}