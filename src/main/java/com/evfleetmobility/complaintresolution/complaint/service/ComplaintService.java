package com.evfleetmobility.complaintresolution.complaint.service;

import com.evfleetmobility.complaintresolution.complaint.dto.ComplaintRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.vendor.entity.Vendor;

import java.util.List;

public interface ComplaintService {

    // ---- USER: FILE A COMPLAINT ----
    String saveComplaint(ComplaintRequestDTO request, String customerId);

    // ---- ROLE-BASED: GET COMPLAINTS ----
    // Returns complaints filtered by the caller's role automatically
    List<Complaint> getComplaints();

    // ---- SHARED: COMPLAINT DETAILS ----
    Complaint getComplaintDetails(Long complaintId);

    // ---- FILTERS ----
    List<Complaint> getComplaintsByVehicle(String vehicleId);
    List<Complaint> getComplaintStatus(String status);

    // ---- VENDOR: ASSIGNED COMPLAINTS ----
    List<Complaint> getAssignedComplaintsByVendorName(String vendorName);

    // ---- VENDOR: UPDATE STATUS ----
    String updateComplaintStatus(Long complaintId, String status);

    // ---- VENDOR: RESOLVE (or escalate to manager) ----
    String resolveComplaint(Long complaintId, Boolean resolved, String remarks);

    // ---- MANAGER: APPROVE AND ASSIGN TO TEAM ----
    Complaint approveAndAssignComplaint(Long complaintId, String teamName);

    // ---- MANAGER: REJECT COMPLAINT ----
    Complaint rejectComplaint(Long complaintId);

    // ---- MANAGER: CAMUNDA WORKFLOW DECISION ----
    // decision: "RESOLVE" | "REJECT" | "RETRY"
    String managerDecision(Long complaintId, String decision);

    // ---- MANAGER: VENDOR LISTING (for assignment UI) ----
    List<Vendor> getAvailableVendors();
    Vendor getVendorById(Long vendorId);

    // ---- DEPRECATED (kept for backward compat) ----
    List<Complaint> getMyComplaints(String customerId);
    List<Complaint> getComplaintsByVehicleId(String vehicleId);
    List<Complaint> getAllComplaints();
    Complaint getComplaintById(Long id);
    List<Complaint> getComplaintsByStatus(String status);
    List<Complaint> getComplaintsByPriority(String priority);
}
