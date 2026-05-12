package com.evfleetmobility.complaintresolution.complaint.dto;

public class AIReplyDTO {
    private String sender; // "AI" or "USER"
    private String message;
    private Double confidence;
    private String timestamp;
    private Integer attemptCount;

    public AIReplyDTO() {}

    public AIReplyDTO(String sender, String message, Double confidence, String timestamp, Integer attemptCount) {
        this.sender = sender;
        this.message = message;
        this.confidence = confidence;
        this.timestamp = timestamp;
        this.attemptCount = attemptCount;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(Integer attemptCount) {
        this.attemptCount = attemptCount;
    }
}
