package com.evfleetmobility.complaintresolution.manager.service;

import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;

public interface ManagerService {
    String managerDecision(Long complaintId, String decision, String remarks);
}
