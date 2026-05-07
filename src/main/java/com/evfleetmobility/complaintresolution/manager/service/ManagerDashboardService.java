package com.evfleetmobility.complaintresolution.manager.service;

import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.complaint.repository.ComplaintRepository;
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
