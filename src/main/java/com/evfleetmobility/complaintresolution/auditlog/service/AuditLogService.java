package com.evfleetmobility.complaintresolution.auditlog.service;

import com.evfleetmobility.complaintresolution.auditlog.entity.AuditLog;

import java.util.List;
import java.util.Map;

public interface AuditLogService {
    void saveLog(Long complaintId,
                        String vehicleId,
                        String action,
                        String performedBy,
                        String previousStatus,
                        String newStatus,
                        String remarks,
                        Map<String, Object> metadataMap);
    List<AuditLog> getLogsByComplaintId(Long complaintId);
    List<AuditLog> getLogsByAction(String action);
    List<AuditLog> getLogsByVehicleId(String vehicleId);
}
