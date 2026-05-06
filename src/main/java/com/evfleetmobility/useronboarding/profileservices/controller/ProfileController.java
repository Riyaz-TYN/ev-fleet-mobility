package com.evfleetmobility.useronboarding.profileservices.controller;

import com.evfleetmobility.common.response.ApiResponse;
import com.evfleetmobility.useronboarding.profileservices.dto.ProfileRequest;
import com.evfleetmobility.useronboarding.profileservices.dto.ProfileResponse;
import com.evfleetmobility.useronboarding.profileservices.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> getMyProfile(Principal principal) {
        Long userId = Long.valueOf(principal.getName());
        ProfileResponse profile = profileService.getMyProfile(userId);
        return ResponseEntity.ok(new ApiResponse<>("Profile fetched successfully", profile));
    }

    @PostMapping(value = "/complete", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> completeProfile(@ModelAttribute ProfileRequest request) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        String message = profileService.completeProfile(userId, request);
        return ResponseEntity.ok(new ApiResponse<>(message, null));
    }
}


