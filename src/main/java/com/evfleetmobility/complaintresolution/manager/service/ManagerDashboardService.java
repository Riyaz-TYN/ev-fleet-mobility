package com.evfleetmobility.complaintresolution.manager.service;

import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorDTO;
import java.util.*;

public interface ManagerDashboardService {
    List<Map<String, Object>> getAllComplaintsForManager();
    List<Map<String, Object>> getEscalatedComplaintsForManager();
    Complaint approveAndAssignComplaint(Long complaintId, Long vendorId);
    Complaint rejectComplaint(Long complaintId);
    List<Map<String, Object>> getManagerHistory();
    List<VendorDTO> getNearbyVendorsForComplaint(Long complaintId);
    Complaint reassignVendor(Long complaintId, Long vendorId);
}
