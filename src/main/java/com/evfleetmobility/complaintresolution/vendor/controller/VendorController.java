package com.evfleetmobility.complaintresolution.vendor.controller;

import com.evfleetmobility.complaintresolution.vendor.dto.VendorAvailabilityRequestDTO;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorDTO;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorExpertiseRequestDTO;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorIdRequestDTO;
import com.evfleetmobility.complaintresolution.vendor.service.VendorService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    // ALL VENDORS
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public List<VendorDTO> getAllVendors() {
        return vendorService.getAllVendors();
    }

    // APPROVED VENDORS
    @GetMapping("/approved")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public List<VendorDTO> getApprovedVendors() {
        return vendorService.getApprovedVendors();
    }

    // AVAILABLE VENDORS
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public List<VendorDTO> getAvailableVendors() {
        return vendorService.getAvailableVendors();
    }

    // SINGLE VENDOR DETAILS
    @PostMapping("/details")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
    public VendorDTO getVendorDetails(
            @RequestBody VendorIdRequestDTO request) {

        return vendorService.getVendorById(
                request.getVendorId()
        );
    }

    // FILTER BY EXPERTISE
    @PostMapping("/expertise")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public List<VendorDTO> getVendorsByExpertise(
            @RequestBody VendorExpertiseRequestDTO request) {

        return vendorService.getVendorsByExpertise(
                request.getExpertise()
        );
    }

    // FILTER BY AVAILABILITY
    @PostMapping("/availability")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public List<VendorDTO> getVendorsByAvailability(
            @RequestBody VendorAvailabilityRequestDTO request) {

        return vendorService.getVendorsByAvailability(
                request.getAvailability()
        );
    }
}