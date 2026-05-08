package com.evfleetmobility.complaintresolution.aiservices.dto;

public class AIResponseDTO {

    private String suggestion;
    private Double confidence;
    private String predictedCategory;
    private String status;

    public AIResponseDTO() {}

    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public String getPredictedCategory() { return predictedCategory; }
    public void setPredictedCategory(String predictedCategory) { this.predictedCategory = predictedCategory; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
