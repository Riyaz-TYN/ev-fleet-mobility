package com.complaint_resolution.vendor.service;

import com.complaint_resolution.complaint.entity.Complaint;
import com.complaint_resolution.vendor.entity.Vendor;
import com.complaint_resolution.complaint.repository.ComplaintRepository;
import com.complaint_resolution.vendor.repository.VendorRepository;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import java.util.Comparator;
import java.util.List;
public interface VendorService {
    void execute(DelegateExecution execution);
    List<Vendor> getAllVendors();
    Vendor getVendorById(Long id);
    List<Vendor> getAvailableVendors();
    List<Vendor> getVendorsByExpertise(String expertise);
    List<Vendor> getVendorsByAvailability(Boolean availability);
    List<Complaint> getAssignedComplaints(String vendorName);
    String updateComplaintStatus(Long complaintId,
            String status);
    String resolveComplaint(Long complaintId,
            Boolean resolved,
            String remarks);
}
