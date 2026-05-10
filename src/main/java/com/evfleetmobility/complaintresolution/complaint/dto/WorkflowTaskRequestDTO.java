package com.evfleetmobility.complaintresolution.complaint.dto;

import lombok.Data;

@Data
public class WorkflowTaskRequestDTO {
    private String taskId;
    private Long complaintId;
    private Boolean resolved;
    private Boolean continueAi;
    private Boolean vendorResolved;
    private String managerDecision;
    private String userFollowUp;
}


