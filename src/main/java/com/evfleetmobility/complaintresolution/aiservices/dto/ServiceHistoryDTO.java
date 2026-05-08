package com.evfleetmobility.complaintresolution.aiservices.dto;

public class ServiceHistoryDTO {

    private String serviceDate;
    private String serviceType;
    private String description;
    private String providerName;

    public ServiceHistoryDTO() {}

    public ServiceHistoryDTO(String serviceDate, String serviceType, String description, String providerName) {
        this.serviceDate = serviceDate;
        this.serviceType = serviceType;
        this.description = description;
        this.providerName = providerName;
    }

    public String getServiceDate() { return serviceDate; }
    public void setServiceDate(String serviceDate) { this.serviceDate = serviceDate; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
}
