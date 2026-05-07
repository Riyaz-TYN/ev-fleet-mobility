package com.complaint_resolution.manager.controller;

import com.complaint_resolution.complaint.entity.Complaint;
import com.complaint_resolution.manager.service.ManagerDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/manager")
@CrossOrigin(origins = "*")
public class ManagerDashboardController {

    private final ManagerDashboardService managerDashboardService;

    public ManagerDashboardController(ManagerDashboardService managerDashboardService) {
        this.managerDashboardService = managerDashboardService;
    }

    @GetMapping("/dashboard/complaints")
    public ResponseEntity<List<Map<String, Object>>> getManagerDashboardComplaints() {
        return ResponseEntity.ok(
                managerDashboardService.getAllComplaintsForManager()
        );
    }
    // API to approve complaint and assign team
    @PutMapping("/complaints/{complaintId}/approve")
    public ResponseEntity<Complaint> approveComplaint(
            @PathVariable Long complaintId,
            @RequestParam String teamName
    ) {
        return ResponseEntity.ok(
                managerDashboardService.approveAndAssignComplaint(complaintId, teamName)
        );
    }
    // API to reject complaint
    @PutMapping("/complaints/{complaintId}/reject")
    public ResponseEntity<Complaint> rejectComplaint(
            @PathVariable Long complaintId
    ) {
        return ResponseEntity.ok(
                managerDashboardService.rejectComplaint(complaintId)
        );
    }
}