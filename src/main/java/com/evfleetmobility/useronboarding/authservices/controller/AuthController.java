package com.evfleetmobility.useronboarding.authservices.controller;

import com.evfleetmobility.common.response.ApiResponse;
import com.evfleetmobility.useronboarding.authservices.dto.AuthResponse;
import com.evfleetmobility.useronboarding.authservices.dto.LoginRequest;
import com.evfleetmobility.useronboarding.authservices.dto.RefreshRequest;
import com.evfleetmobility.useronboarding.authservices.dto.SignupRequest;
import com.evfleetmobility.useronboarding.authservices.dto.UserResponse;
import com.evfleetmobility.useronboarding.authservices.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signup(@Valid @RequestBody SignupRequest signup) {
        UserResponse response = authService.signup(signup);
        return ResponseEntity.ok(new ApiResponse<>("Signup successful", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse tokens = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>("Login successful", tokens));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        AuthResponse tokens = authService.refresh(request);
        return ResponseEntity.ok(new ApiResponse<>("Token refreshed", tokens));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        return ResponseEntity.ok(new ApiResponse<>("Logged out successfully", "Clear tokens from client"));
    }
}


