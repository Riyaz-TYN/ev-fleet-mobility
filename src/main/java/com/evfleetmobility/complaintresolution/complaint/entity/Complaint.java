package com.evfleetmobility.complaintresolution.complaint.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;

    private String issueCategory;

    private String priority;

    private String assignedTeam;

    @Column(columnDefinition = "TEXT")
    private String data;

    private final LocalDateTime createdAt;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "vehicle_id")
    private String vehicleId;

    @Column(name = "vendor_id")
    private Long vendorId;

    @Column(name = "technician_id")
    private Long technicianId;

    private Double latitude;
    private Double longitude;

    @Column(name = "escalation_reason", columnDefinition = "TEXT")
    private String escalationReason;

    @Column(name = "work_summary", columnDefinition = "TEXT")
    private String workSummary;

    public Complaint() {
        this.status = "OPEN";
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIssueCategory() {
        return issueCategory;
    }

    public void setIssueCategory(String issueCategory) {
        this.issueCategory = issueCategory;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getAssignedTeam() {
        return assignedTeam;
    }

    public void setAssignedTeam(String assignedTeam) {
        this.assignedTeam = assignedTeam;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public Long getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(Long technicianId) {
        this.technicianId = technicianId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getEscalationReason() {
        return escalationReason;
    }

    public void setEscalationReason(String escalationReason) {
        this.escalationReason = escalationReason;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getWorkSummary() {
        return workSummary;
    }

    public void setWorkSummary(String workSummary) {
        this.workSummary = workSummary;
    }

    public void addWorkHistory(String action, String actorInfo, String remarks) {
        String time = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a", java.util.Locale.ENGLISH));
        
        StringBuilder entry = new StringBuilder();
        entry.append(time).append(" — ").append(action).append("\n");
        
        if (actorInfo != null && !actorInfo.isBlank()) {
            entry.append(actorInfo).append("\n");
        }
        
        if (remarks != null && !remarks.isBlank()) {
            entry.append("Remarks: \"").append(remarks).append("\"\n");
        }
        
        // Prevent exact duplicate consecutive entries (e.g., double assignment logs)
        String newEntry = entry.toString();
        if (this.workSummary != null && this.workSummary.endsWith(newEntry)) {
            return; 
        }

        if (this.workSummary == null || this.workSummary.isBlank()) {
            this.workSummary = newEntry;
        } else {
            this.workSummary = this.workSummary + "\n" + newEntry;
        }
    }
}