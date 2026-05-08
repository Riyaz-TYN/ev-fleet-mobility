package com.evfleetmobility.complaintresolution.manager.controller;

import com.evfleetmobility.complaintresolution.manager.dto.ManagerDecisionRequestDTO;
import com.evfleetmobility.complaintresolution.manager.service.ManagerDashboardService;
import com.evfleetmobility.complaintresolution.manager.service.ManagerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/manager")
@CrossOrigin("*")
@PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
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

    // MANAGER DECISION (NEW)
    @PutMapping("/complaints/decision")
    public String managerDecision(
            @RequestBody
            ManagerDecisionRequestDTO request
    ) {

        return managerService.managerDecision(
                request.getComplaintId(),
                request.getManagerDecision()
        );
    }

    // MANAGER DECISION (OLD)
    @Deprecated
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