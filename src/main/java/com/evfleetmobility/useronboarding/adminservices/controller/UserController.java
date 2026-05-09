package com.evfleetmobility.useronboarding.adminservices.controller;

import com.evfleetmobility.common.response.ApiResponse;
import com.evfleetmobility.useronboarding.adminservices.dto.CompanyDetailsResponse;
import com.evfleetmobility.useronboarding.adminservices.dto.UserDetailsResponse;
import com.evfleetmobility.useronboarding.adminservices.service.AdminService;
import com.evfleetmobility.useronboarding.authservices.entity.ApprovalStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AdminService adminService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'VENDOR_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDetailsResponse>>> getUsers(
            Principal principal,
            @RequestParam(required = false) ApprovalStatus status) {
        Long callerId = Long.valueOf(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>("Users fetched successfully",
                adminService.getUsersByRoleAndStatus(callerId, status)));
    }

    @GetMapping("/organizations")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<CompanyDetailsResponse>>> getOrganizations(
            Principal principal,
            @RequestParam(required = false) ApprovalStatus status) {
        Long callerId = Long.valueOf(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>("Organizations fetched successfully",
                adminService.getOrganizationsByRoleAndStatus(callerId, status)));
    }

    @GetMapping("/individuals")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'VENDOR_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDetailsResponse>>> getIndividuals(
            Principal principal,
            @RequestParam(required = false) ApprovalStatus status) {
        Long callerId = Long.valueOf(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>("Individuals fetched successfully",
                adminService.getIndividualsByRoleAndStatus(callerId, status)));
    }

    @PutMapping("/assign-vehicle")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'VENDOR_ADMIN')")
    public ResponseEntity<ApiResponse<String>> assignDriverToVehicle(
            Principal principal,
            @RequestBody com.evfleetmobility.useronboarding.adminservices.dto.DriverAssignmentRequest request) {
        Long callerId = Long.valueOf(principal.getName());
        adminService.assignDriverToVehicle(callerId, request);
        return ResponseEntity.ok(new ApiResponse<>("Driver assigned to vehicle successfully", null));
    }
    @PutMapping("/{targetUserId}/rating")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> updateVendorRating(
            Principal principal,
            @PathVariable Long targetUserId,
            @RequestBody com.evfleetmobility.useronboarding.adminservices.dto.VendorRatingRequest request) {
        Long callerId = Long.valueOf(principal.getName());
        adminService.updateVendorRating(callerId, targetUserId, request.getRating());
        return ResponseEntity.ok(new ApiResponse<>("Vendor rating updated successfully", null));
    }
}
