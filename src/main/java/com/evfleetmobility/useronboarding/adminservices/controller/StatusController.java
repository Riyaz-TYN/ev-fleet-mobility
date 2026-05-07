package com.evfleetmobility.useronboarding.adminservices.controller;

import com.evfleetmobility.common.response.ApiResponse;
import com.evfleetmobility.useronboarding.adminservices.dto.StatusRequest;
import com.evfleetmobility.useronboarding.adminservices.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
public class StatusController {

    private final AdminService adminService;

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'VENDOR_ADMIN')")
    public ResponseEntity<ApiResponse<String>> updateStatus(
            Principal principal,
            @RequestBody StatusRequest request) {
        Long callerId = Long.valueOf(principal.getName());
        adminService.handleUnifiedApproval(callerId, request);
        return ResponseEntity.ok(new ApiResponse<>("Status updated successfully", "Success"));
    }
}


