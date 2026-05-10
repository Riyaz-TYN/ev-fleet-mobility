package com.evfleetmobility.complaintresolution.aiservices.dto;

import java.util.List;

public class AIRequestDTO {

    private Long complaintId;
    private String title;
    private String description;
    private String issueType;
    private String priority;

    private String vehicleId;
    private String vehicleModel;
    private String vehicleMake;
    private Integer yearOfManufacture;
    private Double batteryCapacityKwh;

    private List<ServiceHistoryDTO> serviceHistory;

    private Integer aiAttemptCount;
    private String userFollowUp;
    private String previousSuggestion;

    private Long userId;

    public AIRequestDTO() {}

    public Long getComplaintId() { return complaintId; }
    public void setComplaintId(Long complaintId) { this.complaintId = complaintId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIssueType() { return issueType; }
    public void setIssueType(String issueType) { this.issueType = issueType; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }

    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }

    public String getVehicleMake() { return vehicleMake; }
    public void setVehicleMake(String vehicleMake) { this.vehicleMake = vehicleMake; }

    public Integer getYearOfManufacture() { return yearOfManufacture; }
    public void setYearOfManufacture(Integer yearOfManufacture) { this.yearOfManufacture = yearOfManufacture; }

    public Double getBatteryCapacityKwh() { return batteryCapacityKwh; }
    public void setBatteryCapacityKwh(Double batteryCapacityKwh) { this.batteryCapacityKwh = batteryCapacityKwh; }

    public List<ServiceHistoryDTO> getServiceHistory() { return serviceHistory; }
    public void setServiceHistory(List<ServiceHistoryDTO> serviceHistory) { this.serviceHistory = serviceHistory; }

    public Integer getAiAttemptCount() { return aiAttemptCount; }
    public void setAiAttemptCount(Integer aiAttemptCount) { this.aiAttemptCount = aiAttemptCount; }

    public String getUserFollowUp() { return userFollowUp; }
    public void setUserFollowUp(String userFollowUp) { this.userFollowUp = userFollowUp; }

    public String getPreviousSuggestion() { return previousSuggestion; }
    public void setPreviousSuggestion(String previousSuggestion) { this.previousSuggestion = previousSuggestion; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
