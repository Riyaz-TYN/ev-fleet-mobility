package com.evfleetmobility.complaintresolution.auditlog.service.impl;

import com.evfleetmobility.complaintresolution.auditlog.service.AuditLogService;
import com.evfleetmobility.complaintresolution.auditlog.entity.AuditLog;
import com.evfleetmobility.complaintresolution.auditlog.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository,
                           ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }

    public void saveLog(Long complaintId,
                        String vehicleId,
                        String action,
                        String performedBy,
                        String previousStatus,
                        String newStatus,
                        String remarks,
                        Map<String, Object> metadataMap) {

        try {
            String metadataJson = "{}";

            if (metadataMap != null && !metadataMap.isEmpty()) {
                metadataJson = objectMapper.writeValueAsString(metadataMap);
            }

            AuditLog auditLog = new AuditLog(
                    complaintId,
                    vehicleId,
                    action,
                    performedBy,
                    previousStatus,
                    newStatus,
                    remarks,
                    metadataJson
            );

            auditLogRepository.save(auditLog);

        } catch (Exception e) {
            throw new RuntimeException("Failed to save audit log", e);
        }
    }

    public List<AuditLog> getLogsByComplaintId(Long complaintId) {
        return auditLogRepository.findByComplaintIdOrderByCreatedAtAsc(complaintId);
    }

    public List<AuditLog> getLogsByAction(String action) {
        return auditLogRepository.findByActionOrderByCreatedAtDesc(action);
    }

    public List<AuditLog> getLogsByVehicleId(String vehicleId) {
        return auditLogRepository.findByVehicleIdOrderByCreatedAtAsc(vehicleId);
    }
}