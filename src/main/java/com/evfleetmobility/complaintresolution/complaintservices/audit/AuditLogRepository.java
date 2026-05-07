package com.evfleetmobility.complaintresolution.complaintservices.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByComplaintIdOrderByCreatedAtAsc(Long complaintId);
    List<AuditLog> findByActionOrderByCreatedAtDesc(String action);
    List<AuditLog> findByVehicleIdOrderByCreatedAtAsc(String vehicleId);
}
