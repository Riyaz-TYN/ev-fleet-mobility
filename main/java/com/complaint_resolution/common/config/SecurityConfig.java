package com.complaint_resolution.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security configuration with CORS + RBAC support
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // ENABLE CORS
                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                // DISABLE CSRF FOR APIs
                .csrf(csrf -> csrf.disable())

                // AUTHORIZATION RULES
                .authorizeHttpRequests(auth -> auth

                        // AUTH APIs
                        .requestMatchers("/auth/**")
                        .permitAll()

                        // COMPLAINT APIs
                        .requestMatchers("/api/complaints/**")
                        .authenticated()

                        // ALL OTHER APIs
                        .anyRequest()
                        .permitAll()
                );

        return http.build();
    }

    /**
     * CORS CONFIGURATION
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config =
                new CorsConfiguration();

        // ALLOW ALL ORIGINS (DEV ONLY)
        config.setAllowedOrigins(
                List.of("*")
        );

        // ALLOWED METHODS
        config.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        // ALLOWED HEADERS
        config.setAllowedHeaders(
                List.of("*")
        );

        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                config
        );

        return source;
    }
}