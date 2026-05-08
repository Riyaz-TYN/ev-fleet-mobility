package com.evfleetmobility.complaintresolution.manager.controller;

import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.manager.dto.ManagerApproveRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.dto.ComplaintDetailsRequestDTO;
import com.evfleetmobility.complaintresolution.manager.service.ManagerDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/manager")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
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
    // API to approve complaint and assign team (NEW)
    @PutMapping("/complaints/approve")
    public ResponseEntity<Complaint> approveComplaint(
            @RequestBody ManagerApproveRequestDTO request
    ) {
        return ResponseEntity.ok(
                managerDashboardService.approveAndAssignComplaint(request.getComplaintId(), request.getTeamName())
        );
    }

    // API to approve complaint and assign team (OLD)
    @Deprecated
    @PutMapping("/complaints/{complaintId}/approve")
    public ResponseEntity<Complaint> approveComplaint(
            @PathVariable Long complaintId,
            @RequestParam String teamName
    ) {
        return ResponseEntity.ok(
                managerDashboardService.approveAndAssignComplaint(complaintId, teamName)
        );
    }
    
    // API to reject complaint (NEW)
    @PutMapping("/complaints/reject")
    public ResponseEntity<Complaint> rejectComplaint(
            @RequestBody ComplaintDetailsRequestDTO request
    ) {
        return ResponseEntity.ok(
                managerDashboardService.rejectComplaint(request.getComplaintId())
        );
    }

    // API to reject complaint (OLD)
    @Deprecated
    @PutMapping("/complaints/{complaintId}/reject")
    public ResponseEntity<Complaint> rejectComplaint(
            @PathVariable Long complaintId
    ) {
        return ResponseEntity.ok(
                managerDashboardService.rejectComplaint(complaintId)
        );
    }
}