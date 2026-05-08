package com.evfleetmobility.complaintresolution.aiservices.dto;

public class AIQuerySaveRequestDTO {

    private Long userId;
    private String vehicleId;
    private String vehicleModel;
    private String question;

    public AIQuerySaveRequestDTO() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }

    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
}
