package com.evfleetmobility.complaintresolution.auditlog.controller;

import com.evfleetmobility.complaintresolution.auditlog.entity.AuditLog;
import com.evfleetmobility.complaintresolution.auditlog.service.AuditLogService;
import com.evfleetmobility.complaintresolution.complaint.dto.AuditLogRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.dto.AuditLogActionRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.dto.VehicleComplaintRequestDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/audit-logs")
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','MANAGER')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @PostMapping("/complaint")
    public List<AuditLog> getLogsByComplaintIdDTO(@RequestBody AuditLogRequestDTO request) {
        return auditLogService.getLogsByComplaintId(request.getComplaintId());
    }

    @GetMapping("/complaint/{complaintId}")
    public List<AuditLog> getLogsByComplaintId(@PathVariable Long complaintId) {
        return auditLogService.getLogsByComplaintId(complaintId);
    }

    @PostMapping("/action")
    public List<AuditLog> getLogsByActionDTO(@RequestBody AuditLogActionRequestDTO request) {
        return auditLogService.getLogsByAction(request.getAction());
    }

    @Deprecated
    @GetMapping("/action/{action}")
    public List<AuditLog> getLogsByAction(@PathVariable String action) {
        return auditLogService.getLogsByAction(action);
    }

    @PostMapping("/vehicle")
    public List<AuditLog> getLogsByVehicleIdDTO(@RequestBody VehicleComplaintRequestDTO request) {
        return auditLogService.getLogsByVehicleId(request.getVehicleId());
    }

    @Deprecated
    @GetMapping("/vehicle/{vehicleId}")
    public List<AuditLog> getLogsByVehicleId(@PathVariable String vehicleId) {
        return auditLogService.getLogsByVehicleId(vehicleId);
    }
}