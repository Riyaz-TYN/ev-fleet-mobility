package com.complaint_resolution.manager.controller;

import com.complaint_resolution.manager.dto.ManagerDecisionRequestDTO;
import com.complaint_resolution.manager.service.ManagerDashboardService;
import com.complaint_resolution.manager.service.ManagerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/manager")
@CrossOrigin("*")
public class ManagerController {

    @Autowired
    private ManagerDashboardService managerDashboardService;

    private final ManagerService managerService;

    public ManagerController(
            ManagerService managerService
    ) {

        this.managerService =
                managerService;
    }

    @GetMapping("/complaints/escalated")
    public List<Map<String, Object>>
    getEscalatedComplaints() {

        return managerDashboardService
                .getEscalatedComplaintsForManager();
    }

    // ✅ MANAGER DECISION
    @PutMapping("/complaints/{complaintId}/decision")
    public String managerDecision(

            @PathVariable Long complaintId,

            @RequestBody
            ManagerDecisionRequestDTO request
    ) {

        return managerService.managerDecision(
                complaintId,
                request.getManagerDecision()
        );
    }
    @GetMapping("/complaints/history")
    public List<Map<String, Object>>
    getManagerHistory() {

        return managerDashboardService
                .getManagerHistory();
    }
}