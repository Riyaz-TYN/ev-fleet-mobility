package com.evfleetmobility.useronboarding.vehicleservices.dto;

import com.evfleetmobility.useronboarding.vehicleservices.entity.ServiceType;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ServiceHistoryRequest {
    private Long vehicleId;
    private LocalDate serviceDate;
    private Long odometerReading;
    private ServiceType serviceType;
    private String description;
    private BigDecimal cost;
    private String providerName;

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
    public LocalDate getServiceDate() { return serviceDate; }
    public void setServiceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; }
    public Long getOdometerReading() { return odometerReading; }
    public void setOdometerReading(Long odometerReading) { this.odometerReading = odometerReading; }
    public ServiceType getServiceType() { return serviceType; }
    public void setServiceType(ServiceType serviceType) { this.serviceType = serviceType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }
    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
}
