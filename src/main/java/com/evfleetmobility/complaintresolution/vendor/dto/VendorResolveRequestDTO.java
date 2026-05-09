package com.evfleetmobility.complaintresolution.vendor.dto;

public class VendorResolveRequestDTO {

    private Long complaintId;

    private Boolean resolved;

    private String resolutionRemarks;

    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }

    public Boolean getResolved() {
        return resolved;
    }

    public void setResolved(Boolean resolved) {
        this.resolved = resolved;
    }

    public String getResolutionRemarks() {
        return resolutionRemarks;
    }

    public void setResolutionRemarks(
            String resolutionRemarks) {

        this.resolutionRemarks =
                resolutionRemarks;
    }
}
