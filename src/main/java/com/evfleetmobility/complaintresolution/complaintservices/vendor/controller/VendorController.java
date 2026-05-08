package com.evfleetmobility.complaintresolution.complaintservices.vendor.controller;

import com.evfleetmobility.complaintresolution.complaintservices.vendor.entity.Vendor;
import com.evfleetmobility.complaintresolution.complaintservices.vendor.service.VendorService;
import org.springframework.web.bind.annotation.*;
import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.complaintservices.vendor.dto.VendorStatusUpdateDTO;
import java.util.List;
import com.evfleetmobility.complaintresolution.complaintservices.vendor.dto.VendorResolveRequestDTO;
import com.evfleetmobility.complaintresolution.complaintservices.vendor.dto.VendorIdRequestDTO;
import com.evfleetmobility.complaintresolution.complaintservices.vendor.dto.VendorExpertiseRequestDTO;
import com.evfleetmobility.complaintresolution.complaintservices.vendor.dto.VendorAvailabilityRequestDTO;
import com.evfleetmobility.complaintresolution.complaintservices.vendor.dto.VendorNameRequestDTO;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/vendors")
@CrossOrigin("*")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public List<Vendor> getAllVendors() {
        return vendorService.getAllVendors();
    }

    // ✅ New secure DTO API
    @PostMapping("/details")
    @PreAuthorize("hasAnyRole('VENDOR','VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
    public Vendor getVendorDetails(@RequestBody VendorIdRequestDTO request) {
        return vendorService.getVendorById(request.getVendorId());
    }

    // ✅ Get vendor by ID
    @Deprecated
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDOR','VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
    public Vendor getVendorById(@PathVariable Long id) {
        return vendorService.getVendorById(id);
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public List<Vendor> getAvailableVendors() {
        return vendorService.getAvailableVendors();
    }

    // ✅ New secure DTO API
    @PostMapping("/expertise")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public List<Vendor> getVendorsByExpertise(
            @RequestBody VendorExpertiseRequestDTO request) {

        return vendorService.getVendorsByExpertise(request.getExpertise());
    }

    // ✅ Get vendors by expertise
    @Deprecated
    @GetMapping("/expertise/{expertise}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public List<Vendor> getVendorsByExpertise(
            @PathVariable String expertise) {

        return vendorService.getVendorsByExpertise(expertise);
    }

    @PostMapping("/availability")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public List<Vendor> getVendorsByAvailability(
            @RequestBody VendorAvailabilityRequestDTO request) {

        return vendorService.getVendorsByAvailability(request.getAvailability());
    }

    @Deprecated
    @GetMapping("/availability/{availability}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public List<Vendor> getVendorsByAvailability(
            @PathVariable Boolean availability) {

        return vendorService.getVendorsByAvailability(availability);
    }
    // ✅ New secure DTO API
    @PostMapping("/complaints")
    @PreAuthorize("hasAnyRole('VENDOR','VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
    public List<Complaint> getAssignedComplaints(
            @RequestBody VendorNameRequestDTO request) {

        return vendorService.getAssignedComplaints(request.getVendorName());
    }

    // ✅ Get assigned complaints for vendor dashboard
    @Deprecated
    @GetMapping("/{vendorName}/complaints")
    @PreAuthorize("hasAnyRole('VENDOR','VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
    public List<Complaint> getAssignedComplaints(
            @PathVariable String vendorName) {

        return vendorService.getAssignedComplaints(vendorName);
    }
    @PutMapping("/complaints/status")
    @PreAuthorize("hasAnyRole('VENDOR','VENDOR_ADMIN','MANAGER')")
    public String updateComplaintStatus(
            @RequestBody VendorStatusUpdateDTO request) {

        return vendorService.updateComplaintStatus(
                request.getComplaintId(),
                request.getStatus()
        );
    }

    @Deprecated
    @PutMapping("/complaints/{complaintId}/status")
    @PreAuthorize("hasAnyRole('VENDOR','VENDOR_ADMIN','MANAGER')")
    public String updateComplaintStatus(
            @PathVariable Long complaintId,
            @RequestBody VendorStatusUpdateDTO request) {

        return vendorService.updateComplaintStatus(
                complaintId,
                request.getStatus()
        );
    }
    // ✅ RESOLVE COMPLAINT (New secure DTO API)
    @PutMapping("/complaints/resolve")
    @PreAuthorize("hasAnyRole('VENDOR','VENDOR_ADMIN','MANAGER')")
    public String resolveComplaint(
            @RequestBody VendorResolveRequestDTO request) {

        return vendorService.resolveComplaint(
                request.getComplaintId(),
                request.getResolved(),
                request.getResolutionRemarks()
        );
    }

    // ✅ RESOLVE COMPLAINT (OLD)
    @Deprecated
    @PutMapping("/complaints/{complaintId}/resolve")
    @PreAuthorize("hasAnyRole('VENDOR','VENDOR_ADMIN','MANAGER')")
    public String resolveComplaint(
            @PathVariable Long complaintId,
            @RequestBody VendorResolveRequestDTO request) {

        return vendorService.resolveComplaint(
                complaintId,
                request.getResolved(),
                request.getResolutionRemarks()
        );
    }
}