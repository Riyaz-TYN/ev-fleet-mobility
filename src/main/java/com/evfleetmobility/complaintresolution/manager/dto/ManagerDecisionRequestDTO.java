package com.evfleetmobility.complaintresolution.manager.dto;

public class ManagerDecisionRequestDTO {

    private Long complaintId;

    private String managerDecision;

    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }

    public String getManagerDecision() {
        return managerDecision;
    }

    public void setManagerDecision(String managerDecision) {
        this.managerDecision = managerDecision;
    }
}