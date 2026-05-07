package com.evfleetmobility.complaintresolution.manager.service;

import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.complaint.repository.ComplaintRepository;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
public interface ManagerService {
    String managerDecision(Long complaintId,
            String decision);
}
