package com.evfleetmobility.complaintresolution.vendor.controller;

import com.evfleetmobility.complaintresolution.vendor.dto.VendorAvailabilityRequestDTO;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorDTO;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorExpertiseRequestDTO;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorIdRequestDTO;
import com.evfleetmobility.complaintresolution.vendor.service.VendorService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> getAllVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(vendorService.getAllVendors(page, size));
    }

    @GetMapping("/approved")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> getApprovedVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(vendorService.getApprovedVendors(page, size));
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> getAvailableVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(vendorService.getAvailableVendors(page, size));
    }

    @PostMapping("/details")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
    public VendorDTO getVendorDetails(
            @RequestBody VendorIdRequestDTO request) {

        return vendorService.getVendorById(
                request.getVendorId()
        );
    }

    
    @PostMapping("/expertise")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> getVendorsByExpertise(
            @RequestBody VendorExpertiseRequestDTO request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(vendorService.getVendorsByExpertise(
                request.getExpertise(), page, size
        ));
    }

    @PostMapping("/availability")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> getVendorsByAvailability(
            @RequestBody VendorAvailabilityRequestDTO request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(vendorService.getVendorsByAvailability(
                request.getAvailability(), page, size
        ));
    }
}