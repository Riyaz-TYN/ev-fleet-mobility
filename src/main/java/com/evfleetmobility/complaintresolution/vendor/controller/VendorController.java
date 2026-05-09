package com.evfleetmobility.complaintresolution.vendor.controller;

import com.evfleetmobility.useronboarding.profileservices.entity.OrganizationDetails;
import com.evfleetmobility.complaintresolution.vendor.service.VendorService;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorIdRequestDTO;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorExpertiseRequestDTO;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorAvailabilityRequestDTO;
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

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public List<OrganizationDetails> getAllVendors() {
        return vendorService.getAllVendors();
    }

    @PostMapping("/details")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
    public OrganizationDetails getVendorDetails(@RequestBody VendorIdRequestDTO request) {
        return vendorService.getVendorById(request.getVendorId());
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public List<OrganizationDetails> getAvailableVendors() {
        return vendorService.getAvailableVendors();
    }

    @PostMapping("/expertise")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public List<OrganizationDetails> getVendorsByExpertise(
            @RequestBody VendorExpertiseRequestDTO request) {
        return vendorService.getVendorsByExpertise(request.getExpertise());
    }

    @PostMapping("/availability")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public List<OrganizationDetails> getVendorsByAvailability(
            @RequestBody VendorAvailabilityRequestDTO request) {
        return vendorService.getVendorsByAvailability(request.getAvailability());
    }
}
