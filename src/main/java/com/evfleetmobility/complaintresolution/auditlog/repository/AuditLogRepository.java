package com.evfleetmobility.complaintresolution.auditlog.repository;

import com.evfleetmobility.complaintresolution.auditlog.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByComplaintIdOrderByCreatedAtAsc(Long complaintId);

    // ✅ Fetch logs by action
    List<AuditLog> findByActionOrderByCreatedAtDesc(String action);

    List<AuditLog> findByVehicleIdOrderByCreatedAtAsc(String vehicleId);
}