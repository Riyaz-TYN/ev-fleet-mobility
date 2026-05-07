package com.complaint_resolution.manager.service;

import com.complaint_resolution.complaint.entity.Complaint;
import com.complaint_resolution.complaint.repository.ComplaintRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

public interface ManagerDashboardService {
    List<Map<String, Object>> getAllComplaintsForManager();
    List<Map<String, Object>> getEscalatedComplaintsForManager();
    Complaint approveAndAssignComplaint(Long complaintId,
            String teamName);
    Complaint rejectComplaint(Long complaintId);
    List<Map<String, Object>> getManagerHistory();
}
