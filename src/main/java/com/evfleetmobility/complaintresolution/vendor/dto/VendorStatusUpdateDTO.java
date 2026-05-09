package com.evfleetmobility.complaintresolution.vendor.dto;

public class VendorStatusUpdateDTO {

    private Long complaintId;
    private String status;

    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
