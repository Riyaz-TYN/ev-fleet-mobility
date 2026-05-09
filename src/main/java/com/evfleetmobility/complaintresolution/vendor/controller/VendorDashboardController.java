package com.evfleetmobility.complaintresolution.vendor.controller;

import com.evfleetmobility.common.security.AuthContextService;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorDashboardStatsDTO;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorIdRequestDTO;
import com.evfleetmobility.complaintresolution.vendor.service.VendorDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/organizationStats")
public class VendorDashboardController {

    @Autowired
    private VendorDashboardService vendorDashboardService;

    @Autowired
    private AuthContextService authContextService;

    @PostMapping
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'MANAGER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<VendorDashboardStatsDTO> getVendorStats(@RequestBody VendorIdRequestDTO request) {
        Long vendorId = request.getVendorId();
        
        // Security check: Vendor can only see their own stats
        String role = authContextService.getCurrentRole();
        Long currentUserId = authContextService.getCurrentUserId();
        
        if ("VENDOR_ADMIN".equalsIgnoreCase(role) && !currentUserId.equals(vendorId)) {
            return ResponseEntity.status(403).build(); // Forbidden
        }
        
        return ResponseEntity.ok(vendorDashboardService.getVendorStats(vendorId));
    }
}
