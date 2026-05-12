package com.evfleetmobility.complaintresolution.complaint.service;

import com.evfleetmobility.complaintresolution.complaint.dto.ComplaintRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.useronboarding.profileservices.entity.OrganizationDetails;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ComplaintService {

    String saveComplaint(ComplaintRequestDTO request, String customerId);

    Page<Complaint> getComplaints(int page, int size);

    Complaint getComplaintDetails(Long complaintId);

    Page<Complaint> getComplaintsByVehicle(String vehicleId, int page, int size);
    Page<Complaint> getComplaintStatus(String status, int page, int size);

    List<Complaint> getAssignedComplaintsByVendorId(Long vendorId);

    String updateComplaintStatus(Long complaintId, String status);

    String resolveComplaint(Long complaintId, Boolean resolved, String remarks);

    Complaint approveAndAssignComplaint(Long complaintId, Long vendorId);

    Complaint rejectComplaint(Long complaintId);

    String managerDecision(Long complaintId, String decision, String remarks);

    Complaint reassignVendor(Long complaintId, Long vendorId);

    List<com.evfleetmobility.complaintresolution.vendor.dto.VendorDTO> getNearbyVendors(Long complaintId);

    List<OrganizationDetails> getAvailableVendors();
    OrganizationDetails getVendorById(Long vendorId);

    Page<Complaint> getMyComplaints(String customerId, int page, int size);
    Page<Complaint> getComplaintsByVehicleId(String vehicleId, int page, int size);
    Page<Complaint> getAllComplaints(int page, int size);
    Complaint getComplaintById(Long id);
    Page<Complaint> getComplaintsByStatus(String status, int page, int size);
    Page<Complaint> getComplaintsByPriority(String priority, int page, int size);

    Complaint assignTechnician(Long complaintId, Long technicianId);

    List<com.evfleetmobility.complaintresolution.complaint.dto.AIReplyDTO> getAIChatHistory(Long complaintId);
}

