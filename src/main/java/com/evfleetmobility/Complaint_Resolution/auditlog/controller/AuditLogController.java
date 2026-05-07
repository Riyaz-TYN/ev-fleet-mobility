package com.complaint_resolution.auditlog.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.complaint_resolution.auditlog.entity.AuditLog;
import com.complaint_resolution.auditlog.service.AuditLogService;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("/complaint/{complaintId}")
    public List<AuditLog> getLogsByComplaintId(@PathVariable Long complaintId) {
        return auditLogService.getLogsByComplaintId(complaintId);
    }

    // ✅ Fetch logs by action
    @GetMapping("/action/{action}")
    public List<AuditLog> getLogsByAction(@PathVariable String action) {
        return auditLogService.getLogsByAction(action);
    }

    @GetMapping("/vehicle/{vehicleId}")
    public List<AuditLog> getLogsByVehicleId(@PathVariable String vehicleId) {
        return auditLogService.getLogsByVehicleId(vehicleId);
    }
}