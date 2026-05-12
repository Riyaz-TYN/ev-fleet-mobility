package com.evfleetmobility.complaintresolution.vendor.service;

import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorDTO;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import org.springframework.data.domain.Page;
import java.util.List;

public interface VendorService {

    void execute(DelegateExecution execution);

   
    Page<VendorDTO> getAllVendors(int page, int size);

    Page<VendorDTO> getApprovedVendors(int page, int size);

   
    VendorDTO getVendorById(Long id);

   
    Page<VendorDTO> getAvailableVendors(int page, int size);

 
    Page<VendorDTO> getVendorsByExpertise(String expertise, int page, int size);

  
    Page<VendorDTO> getVendorsByAvailability(Boolean availability, int page, int size);

    Page<Complaint> getAssignedComplaints(Long vendorId, int page, int size);


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