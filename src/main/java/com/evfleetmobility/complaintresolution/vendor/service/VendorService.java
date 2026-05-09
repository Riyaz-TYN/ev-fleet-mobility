package com.evfleetmobility.complaintresolution.vendor.service;

import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.useronboarding.profileservices.entity.OrganizationDetails;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import java.util.List;

public interface VendorService {
    void execute(DelegateExecution execution);
    List<OrganizationDetails> getAllVendors();
    OrganizationDetails getVendorById(Long id);
    List<OrganizationDetails> getAvailableVendors();
    List<OrganizationDetails> getVendorsByExpertise(String expertise);
    List<OrganizationDetails> getVendorsByAvailability(Boolean availability);
    List<Complaint> getAssignedComplaints(Long vendorId);
    String updateComplaintStatus(Long complaintId,
            String status);
    String resolveComplaint(Long complaintId,
            Boolean resolved,
            String remarks);
}
