package com.evfleetmobility.complaintresolution.complaint.controller;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import com.evfleetmobility.complaintresolution.complaint.dto.WorkflowTaskRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.repository.ComplaintRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {

    private final TaskService taskService;

    @Autowired
    private ComplaintRepository complaintRepository;

    public WorkflowController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/user-response")
    @PreAuthorize("hasAnyRole('DRIVER','MANAGER','ADMIN','SUPER_ADMIN')")
    public String submitUserResponseDTO(
            @RequestBody WorkflowTaskRequestDTO request
    ) {
        Long complaintId = request.getComplaintId();
        String taskId = request.getTaskId();
        
        if (taskId == null || taskId.isBlank()) {
            Task task = taskService.createTaskQuery()
                    .processVariableValueEquals("complaintId", complaintId)
                    .taskDefinitionKey("userTask")
                    .singleResult();
            if (task == null) return "User task not found for complaint: " + complaintId;
            taskId = task.getId();
        }

        Boolean resolved = request.getResolved() != null ? request.getResolved() : false;
        Boolean continueAi = request.getContinueAi() != null ? request.getContinueAi() : false;

        // We set the escalation reason if the user is not satisfied, 
        // but we leave the Work Summary for Vendor/Manager remarks.
        if (complaintId != null && !Boolean.TRUE.equals(resolved) && !Boolean.TRUE.equals(continueAi)) {
            complaintRepository.findById(complaintId).ifPresent(complaint -> {
                complaint.setEscalationReason("User not satisfied with AI assistance.");
                complaintRepository.save(complaint);
            });
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("resolved", resolved);
        variables.put("continueAi", continueAi);
        variables.put("userFollowUp", request.getUserFollowUp());

        taskService.complete(taskId, variables);

        return "User response submitted successfully";
    }

    @Deprecated
    @PostMapping("/user-response/{taskId}")
    @PreAuthorize("hasRole('DRIVER')")
    public String submitUserResponse(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> request
    ) {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            return "Task not found";
        }

        Boolean resolved = request.get("resolved") != null
                ? (Boolean) request.get("resolved")
                : false;

        Boolean continueAi = request.get("continueAi") != null
                ? (Boolean) request.get("continueAi")
                : false;

        Map<String, Object> variables = new HashMap<>();
        variables.put("resolved", resolved);
        variables.put("continueAi", continueAi);

        taskService.complete(taskId, variables);

        return "User response submitted successfully";
    }

    @PostMapping("/vendor-response")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
    public String submitVendorResponseDTO(
            @RequestBody WorkflowTaskRequestDTO request
    ) {
        String taskId = request.getTaskId();
        Long complaintId = request.getComplaintId();

        if (taskId == null || taskId.isBlank()) {
            Task task = taskService.createTaskQuery()
                    .processVariableValueEquals("complaintId", complaintId)
                    .taskDefinitionKey("vendorTask")
                    .singleResult();
            if (task == null) return "Vendor task not found for complaint: " + complaintId;
            taskId = task.getId();
        }

        Boolean vendorResolved = request.getVendorResolved() != null ? request.getVendorResolved() : false;

        Map<String, Object> variables = new HashMap<>();
        variables.put("vendorResolved", vendorResolved);

        taskService.complete(taskId, variables);

        return "Vendor response submitted successfully";
    }

    @PostMapping("/manager-response")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public String submitManagerResponseDTO(
            @RequestBody WorkflowTaskRequestDTO request
    ) {
        String taskId = request.getTaskId();
        Long complaintId = request.getComplaintId();

        if (taskId == null || taskId.isBlank()) {
            Task task = taskService.createTaskQuery()
                    .processVariableValueEquals("complaintId", complaintId)
                    .taskDefinitionKey("managerTask")
                    .singleResult();
            if (task == null) return "Manager task not found for complaint: " + complaintId;
            taskId = task.getId();
        }

        String managerDecision = request.getManagerDecision() != null ? request.getManagerDecision() : "RETRY";

        Map<String, Object> variables = new HashMap<>();
        variables.put("managerDecision", managerDecision);

        taskService.complete(taskId, variables);

        return "Manager response submitted successfully";
    }
}