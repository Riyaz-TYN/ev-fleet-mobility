package com.evfleetmobility.complaintresolution.complaintservices.vendor.controller;

import com.evfleetmobility.complaintresolution.complaintservices.vendor.entity.Vendor;
import com.evfleetmobility.complaintresolution.complaintservices.vendor.service.VendorService;
import com.evfleetmobility.complaintresolution.complaintservices.vendor.dto.VendorIdRequestDTO;
import com.evfleetmobility.complaintresolution.complaintservices.vendor.dto.VendorExpertiseRequestDTO;
import com.evfleetmobility.complaintresolution.complaintservices.vendor.dto.VendorAvailabilityRequestDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Note: @CrossOrigin removed — CORS is handled globally in SecurityConfig via CorsConfigurationSource
// Roles in this system: DRIVER, VENDOR_ADMIN, MANAGER, ADMIN, SUPER_ADMIN
//
// Complaint-related endpoints previously here have been moved to:
//   ComplaintController @ /api/complaints
//
// Endpoint migration:
//   POST /api/vendors/complaints         -> POST /api/complaints/assigned
//   PUT  /api/vendors/complaints/status  -> PUT  /api/complaints/status
//   PUT  /api/vendors/complaints/resolve -> PUT  /api/complaints/resolve

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    // =========================================================
    // GET ALL VENDORS
    // Roles: MANAGER, ADMIN, SUPER_ADMIN
    // =========================================================
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public List<Vendor> getAllVendors() {
        return vendorService.getAllVendors();
    }

    // =========================================================
    // GET VENDOR DETAILS BY ID
    // Roles: VENDOR_ADMIN, MANAGER, ADMIN, SUPER_ADMIN
    // =========================================================
    @PostMapping("/details")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
    public Vendor getVendorDetails(@RequestBody VendorIdRequestDTO request) {
        return vendorService.getVendorById(request.getVendorId());
    }

    // =========================================================
    // GET AVAILABLE VENDORS
    // Roles: MANAGER, ADMIN, SUPER_ADMIN
    // =========================================================
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public List<Vendor> getAvailableVendors() {
        return vendorService.getAvailableVendors();
    }

    // =========================================================
    // FILTER VENDORS BY EXPERTISE
    // Roles: MANAGER, ADMIN, SUPER_ADMIN
    // =========================================================
    @PostMapping("/expertise")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public List<Vendor> getVendorsByExpertise(
            @RequestBody VendorExpertiseRequestDTO request) {
        return vendorService.getVendorsByExpertise(request.getExpertise());
    }

    // =========================================================
    // FILTER VENDORS BY AVAILABILITY
    // Roles: MANAGER, ADMIN, SUPER_ADMIN
    // =========================================================
    @PostMapping("/availability")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public List<Vendor> getVendorsByAvailability(
            @RequestBody VendorAvailabilityRequestDTO request) {
        return vendorService.getVendorsByAvailability(request.getAvailability());
    }
}