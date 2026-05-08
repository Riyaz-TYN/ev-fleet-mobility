package com.evfleetmobility.complaintresolution.complaint.controller;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import com.evfleetmobility.complaintresolution.complaint.dto.WorkflowTaskRequestDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.HashMap;
import java.util.Map;

// Note: @CrossOrigin removed — CORS is handled globally in SecurityConfig via CorsConfigurationSource
@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {

    private final TaskService taskService;

    public WorkflowController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/user-response")
    @PreAuthorize("hasRole('DRIVER')")
    public String submitUserResponseDTO(
            @RequestBody WorkflowTaskRequestDTO request
    ) {
        Task task = taskService.createTaskQuery()
                .taskId(request.getTaskId())
                .singleResult();

        if (task == null) {
            return "Task not found";
        }

        Boolean resolved = request.getResolved() != null ? request.getResolved() : false;
        Boolean continueAi = request.getContinueAi() != null ? request.getContinueAi() : false;

        Map<String, Object> variables = new HashMap<>();
        variables.put("resolved", resolved);
        variables.put("continueAi", continueAi);

        taskService.complete(request.getTaskId(), variables);

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
    @PreAuthorize("hasRole('VENDOR_ADMIN')")
    public String submitVendorResponseDTO(
            @RequestBody WorkflowTaskRequestDTO request
    ) {
        Task task = taskService.createTaskQuery()
                .taskId(request.getTaskId())
                .singleResult();

        if (task == null) {
            return "Task not found";
        }

        Boolean vendorResolved = request.getVendorResolved() != null ? request.getVendorResolved() : false;

        Map<String, Object> variables = new HashMap<>();
        variables.put("vendorResolved", vendorResolved);

        taskService.complete(request.getTaskId(), variables);

        return "Vendor response submitted successfully";
    }

    @Deprecated
    @PostMapping("/vendor-response/{taskId}")
    @PreAuthorize("hasRole('VENDOR_ADMIN')")
    public String submitVendorResponse(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> request
    ) {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            return "Task not found";
        }

        Boolean vendorResolved = request.get("vendorResolved") != null
                ? (Boolean) request.get("vendorResolved")
                : false;

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
        Task task = taskService.createTaskQuery()
                .taskId(request.getTaskId())
                .singleResult();

        if (task == null) {
            return "Task not found";
        }

        String managerDecision = request.getManagerDecision() != null ? request.getManagerDecision() : "RETRY";

        Map<String, Object> variables = new HashMap<>();
        variables.put("managerDecision", managerDecision);

        taskService.complete(request.getTaskId(), variables);

        return "Manager response submitted successfully";
    }

    @Deprecated
    @PostMapping("/manager-response/{taskId}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public String submitManagerResponse(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> request
    ) {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            return "Task not found";
        }

        String managerDecision = request.get("managerDecision") != null
                ? request.get("managerDecision").toString()
                : "RETRY";

        Map<String, Object> variables = new HashMap<>();
        variables.put("managerDecision", managerDecision);

        taskService.complete(taskId, variables);

        return "Manager response submitted successfully";
    }
}