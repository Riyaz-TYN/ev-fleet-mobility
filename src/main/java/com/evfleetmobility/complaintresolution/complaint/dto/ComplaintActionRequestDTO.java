package com.evfleetmobility.complaintresolution.complaint.dto;

import lombok.Data;

@Data
public class ComplaintActionRequestDTO {

    private Long complaintId;

    private String action;

    private Boolean resolved;

    private Boolean continueAi;

    private String status;

    private String remarks;

    private String managerDecision;
}