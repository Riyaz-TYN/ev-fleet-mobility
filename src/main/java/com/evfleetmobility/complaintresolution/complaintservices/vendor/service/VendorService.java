package com.evfleetmobility.complaintresolution.complaintservices.vendor.service;

import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.complaintservices.vendor.entity.Vendor;
import com.evfleetmobility.complaintresolution.complaint.repository.ComplaintRepository;
import com.evfleetmobility.complaintresolution.complaintservices.vendor.repository.VendorRepository;
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
