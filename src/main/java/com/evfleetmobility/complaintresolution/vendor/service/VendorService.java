package com.evfleetmobility.complaintresolution.vendor.service;

import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorDTO;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import java.util.List;

public interface VendorService {

    void execute(DelegateExecution execution);

   
    List<VendorDTO> getAllVendors();

    List<VendorDTO> getApprovedVendors();

   
    VendorDTO getVendorById(Long id);

   
    List<VendorDTO> getAvailableVendors();

 
    List<VendorDTO> getVendorsByExpertise(String expertise);

  
    List<VendorDTO> getVendorsByAvailability(Boolean availability);

    List<Complaint> getAssignedComplaints(Long vendorId);


    String updateComplaintStatus(
            Long complaintId,
            String status
    );

    String resolveComplaint(
            Long complaintId,
            Boolean resolved,
            String remarks
    );
}