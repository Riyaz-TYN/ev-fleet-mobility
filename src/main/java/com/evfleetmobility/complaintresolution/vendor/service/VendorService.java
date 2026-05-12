package com.evfleetmobility.complaintresolution.vendor.service;

import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorDTO;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import java.util.List;

public interface VendorService {

    void execute(DelegateExecution execution);

    // ALL VENDORS
    List<VendorDTO> getAllVendors();

    // APPROVED VENDORS
    List<VendorDTO> getApprovedVendors();

    // SINGLE VENDOR
    VendorDTO getVendorById(Long id);

    // AVAILABLE VENDORS
    List<VendorDTO> getAvailableVendors();

    // FILTER BY EXPERTISE
    List<VendorDTO> getVendorsByExpertise(String expertise);

    // FILTER BY AVAILABILITY
    List<VendorDTO> getVendorsByAvailability(Boolean availability);

    // ASSIGNED COMPLAINTS
    List<Complaint> getAssignedComplaints(Long vendorId);

    // UPDATE STATUS
    String updateComplaintStatus(
            Long complaintId,
            String status
    );

    // RESOLVE COMPLAINT
    String resolveComplaint(
            Long complaintId,
            Boolean resolved,
            String remarks
    );
}