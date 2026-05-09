package com.evfleetmobility.complaintresolution.complaint.service;

import com.evfleetmobility.complaintresolution.complaint.dto.ComplaintRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.useronboarding.profileservices.entity.OrganizationDetails;

import java.util.List;

public interface ComplaintService {

    String saveComplaint(ComplaintRequestDTO request, String customerId);

    List<Complaint> getComplaints();

    Complaint getComplaintDetails(Long complaintId);

    List<Complaint> getComplaintsByVehicle(String vehicleId);
    List<Complaint> getComplaintStatus(String status);

    List<Complaint> getAssignedComplaintsByVendorName(String vendorName);

    String updateComplaintStatus(Long complaintId, String status);

    String resolveComplaint(Long complaintId, Boolean resolved, String remarks);

    Complaint approveAndAssignComplaint(Long complaintId, String teamName);

    Complaint rejectComplaint(Long complaintId);

    String managerDecision(Long complaintId, String decision);

    List<OrganizationDetails> getAvailableVendors();
    OrganizationDetails getVendorById(Long vendorId);

    List<Complaint> getMyComplaints(String customerId);
    List<Complaint> getComplaintsByVehicleId(String vehicleId);
    List<Complaint> getAllComplaints();
    Complaint getComplaintById(Long id);
    List<Complaint> getComplaintsByStatus(String status);
    List<Complaint> getComplaintsByPriority(String priority);
}
