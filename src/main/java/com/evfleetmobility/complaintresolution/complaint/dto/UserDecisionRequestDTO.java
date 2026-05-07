package com.evfleetmobility.complaintresolution.complaint.dto;

public class UserDecisionRequestDTO {

    private Boolean resolved;
    private Boolean continueAi;

    public Boolean getResolved() {
        return resolved;
    }

    public void setResolved(Boolean resolved) {
        this.resolved = resolved;
    }

    public Boolean getContinueAi() {
        return continueAi;
    }

    public void setContinueAi(Boolean continueAi) {
        this.continueAi = continueAi;
    }
}