package com.complaint_resolution.complaint.controller;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {

    private final TaskService taskService;

    public WorkflowController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/user-response/{taskId}")
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

    @PostMapping("/vendor-response/{taskId}")
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

    @PostMapping("/manager-response/{taskId}")
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