package com.evfleetmobility.complaintresolution.complaint.dto;

import java.util.Map;

public class ComplaintRequestDTO {

    private Map<String, Object> complaintData;

    public Map<String, Object> getComplaintData() {
        return complaintData;
    }

    public void setComplaintData(
            Map<String, Object> complaintData
    ) {
        this.complaintData = complaintData;
    }
}