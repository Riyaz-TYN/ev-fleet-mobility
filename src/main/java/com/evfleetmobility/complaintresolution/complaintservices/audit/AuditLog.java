package com.evfleetmobility.complaintresolution.complaintservices.audit;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long complaintId;

    @Column(name = "vehicle_id")
    private String vehicleId;

    private String action;

    private String performedBy;

    private String previousStatus;

    private String newStatus;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    private LocalDateTime createdAt;

    public AuditLog(Long complaintId,
                    String vehicleId,
                    String action,
                    String performedBy,
                    String previousStatus,
                    String newStatus,
                    String remarks,
                    String metadata) {
        this.complaintId = complaintId;
        this.vehicleId = vehicleId;
        this.action = action;
        this.performedBy = performedBy;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.remarks = remarks;
        this.metadata = metadata;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
