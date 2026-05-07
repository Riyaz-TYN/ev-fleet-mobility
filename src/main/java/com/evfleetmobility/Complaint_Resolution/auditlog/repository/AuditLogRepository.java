package com.complaint_resolution.auditlog.repository;

import com.complaint_resolution.auditlog.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByComplaintIdOrderByCreatedAtAsc(Long complaintId);

    // ✅ Fetch logs by action
    List<AuditLog> findByActionOrderByCreatedAtDesc(String action);

    List<AuditLog> findByVehicleIdOrderByCreatedAtAsc(String vehicleId);
}