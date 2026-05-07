package com.evfleetmobility.complaintresolution.manager.service.impl;
import com.evfleetmobility.complaintresolution.auditlog.service.AuditLogService;


import com.evfleetmobility.complaintresolution.manager.service.ManagerService;
import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.complaint.repository.ComplaintRepository;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private AuditLogService auditLogService;

    public String managerDecision(
            Long complaintId,
            String decision) {

        Complaint complaint =
                complaintRepository.findById(complaintId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Complaint not found"
                                )
                        );

        Task task = taskService.createTaskQuery()
                .processVariableValueEquals(
                        "complaintId",
                        complaintId
                )
                .taskDefinitionKey("managerTask")
                .singleResult();

        if (task == null) {
            return "Manager task not found";
        }

        // Complete workflow task
        taskService.complete(
                task.getId(),
                java.util.Map.of(
                        "managerDecision",
                        decision
                )
        );

        String previousStatus =
                complaint.getStatus();

        // Update complaint status
        if ("RESOLVE".equalsIgnoreCase(decision)) {

            complaint.setStatus("RESOLVED");

        } else if ("REJECT".equalsIgnoreCase(decision)) {

            complaint.setStatus("REJECTED");

        } else if ("RETRY".equalsIgnoreCase(decision)) {

            complaint.setStatus("RETRY_VENDOR");
        }

        complaintRepository.save(complaint);

        // Save audit log
        auditLogService.saveLog(
                complaintId,
                complaint.getVehicleId(),
                "MANAGER_DECISION",
                "MANAGER",
                previousStatus,
                complaint.getStatus(),
                "Manager decision completed",
                java.util.Map.of(
                        "decision",
                        decision
                )
        );

        return "Manager decision updated";
    }
}