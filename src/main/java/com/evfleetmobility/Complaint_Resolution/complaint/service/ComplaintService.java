package com.complaint_resolution.complaint.service;

import com.complaint_resolution.complaint.dto.ComplaintActionRequestDTO;
import com.complaint_resolution.complaint.dto.ComplaintRequestDTO;
import com.complaint_resolution.complaint.entity.Complaint;

import java.util.List;

public interface ComplaintService {

    // EXISTING COMPLAINT FLOW
    String saveComplaint(
            ComplaintRequestDTO request,
            String customerId
    );

    List<Complaint> getMyComplaints(String customerId);

    List<Complaint> getComplaintsByVehicleId(String vehicleId);

    List<Complaint> getAllComplaints();

    Complaint getComplaintById(Long id);

    List<Complaint> getComplaintsByStatus(String status);

    List<Complaint> getComplaintsByPriority(String priority);

    String userAiDecision(
            Long complaintId,
            Boolean resolved,
            Boolean continueAi
    );

    // NEW UNIFIED RBAC METHODS

    // ROLE-BASED COMPLAINT FETCHING
    List<Complaint> getComplaints();

    // COMPLAINT DETAILS
    Complaint getComplaintDetails(Long complaintId);

    // VEHICLE FILTER
    List<Complaint> getComplaintsByVehicle(String vehicleId);

    // STATUS FILTER
    List<Complaint> getComplaintStatus(String status);

    // CENTRALIZED ACTION HANDLER
    Object handleComplaintAction(
            ComplaintActionRequestDTO request
    );
}