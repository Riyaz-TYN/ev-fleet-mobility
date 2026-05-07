package com.complaint_resolution.manager.service;

import com.complaint_resolution.complaint.entity.Complaint;
import com.complaint_resolution.complaint.repository.ComplaintRepository;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
public interface ManagerService {
    String managerDecision(Long complaintId,
            String decision);
}
