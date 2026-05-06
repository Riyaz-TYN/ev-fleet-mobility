package com.evfleetmobility.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.evfleetmobility.common.response.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public JwtFilter(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            if (!"ACCESS".equals(jwtUtil.getTokenType(token))) {
                filterChain.doFilter(request, response);
                return;
            }

            Long userId = jwtUtil.extractUserId(token);
            String role  = jwtUtil.extractRole(token);

            if (userId != null && jwtUtil.validateToken(token, userId)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                // We set the userId (as String) as the principal so controllers can use principal.getName() to get the ID.
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(String.valueOf(userId), null, List.of(authority));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        } catch (ExpiredJwtException e) {
            writeError(response, 401, "Token Expired", "Your access token has expired. Please refresh your token.");
            return;
        } catch (JwtException e) {
            writeError(response, 401, "Invalid Token", "Token is malformed or tampered.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse response, int status, String error, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse body = new ErrorResponse(status, error, message);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
