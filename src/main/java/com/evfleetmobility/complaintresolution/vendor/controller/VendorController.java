package com.evfleetmobility.complaintresolution.vendor.controller;

import com.evfleetmobility.complaintresolution.vendor.entity.Vendor;
import com.evfleetmobility.complaintresolution.vendor.service.VendorService;
import org.springframework.web.bind.annotation.*;
import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorStatusUpdateDTO;
import java.util.List;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorResolveRequestDTO;
@RestController
@RequestMapping("/api/vendors")
@CrossOrigin("*")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @GetMapping
    public List<Vendor> getAllVendors() {
        return vendorService.getAllVendors();
    }

    // ✅ Get vendor by ID
    @GetMapping("/{id}")
    public Vendor getVendorById(@PathVariable Long id) {
        return vendorService.getVendorById(id);
    }

    @GetMapping("/available")
    public List<Vendor> getAvailableVendors() {
        return vendorService.getAvailableVendors();
    }

    // ✅ Get vendors by expertise
    @GetMapping("/expertise/{expertise}")
    public List<Vendor> getVendorsByExpertise(
            @PathVariable String expertise) {

        return vendorService.getVendorsByExpertise(expertise);
    }

    @GetMapping("/availability/{availability}")
    public List<Vendor> getVendorsByAvailability(
            @PathVariable Boolean availability) {

        return vendorService.getVendorsByAvailability(availability);
    }
    // ✅ Get assigned complaints for vendor dashboard
    @GetMapping("/{vendorName}/complaints")
    public List<Complaint> getAssignedComplaints(
            @PathVariable String vendorName) {

        return vendorService.getAssignedComplaints(vendorName);
    }
    @PutMapping("/complaints/{complaintId}/status")
    public String updateComplaintStatus(
            @PathVariable Long complaintId,
            @RequestBody VendorStatusUpdateDTO request) {

        return vendorService.updateComplaintStatus(
                complaintId,
                request.getStatus()
        );
    }
    // ✅ RESOLVE COMPLAINT
    @PutMapping("/complaints/{complaintId}/resolve")
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