package com.evfleetmobility.complaintresolution.complaintservices.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Override
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

    @Override
    public List<AuditLog> getLogsByComplaintId(Long complaintId) {
        return auditLogRepository.findByComplaintIdOrderByCreatedAtAsc(complaintId);
    }

    @Override
    public List<AuditLog> getLogsByAction(String action) {
        return auditLogRepository.findByActionOrderByCreatedAtDesc(action);
    }

    @Override
    public List<AuditLog> getLogsByVehicleId(String vehicleId) {
        return auditLogRepository.findByVehicleIdOrderByCreatedAtAsc(vehicleId);
    }
}
