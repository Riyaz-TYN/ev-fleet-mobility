package com.evfleetmobility.complaintresolution.complaint.dto;

import java.util.Map;

public class ComplaintRequestDTO {

    private Map<String, Object> complaintData;
    private Double latitude;
    private Double longitude;

    public Map<String, Object> getComplaintData() {
        return complaintData;
    }

    public void setComplaintData(Map<String, Object> complaintData) {
        this.complaintData = complaintData;
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
}