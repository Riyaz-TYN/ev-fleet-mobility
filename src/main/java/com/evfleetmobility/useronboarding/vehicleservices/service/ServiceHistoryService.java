package com.evfleetmobility.useronboarding.vehicleservices.service;

import com.evfleetmobility.useronboarding.vehicleservices.dto.ServiceHistoryRequest;
import com.evfleetmobility.useronboarding.vehicleservices.dto.ServiceHistoryResponse;

import java.math.BigDecimal;
import java.util.List;

public interface ServiceHistoryService {
    ServiceHistoryResponse addServiceEntry(ServiceHistoryRequest request);
    ServiceHistoryResponse updateServiceEntry(Long historyId, ServiceHistoryRequest request);
    void deleteServiceEntry(Long historyId);
    List<ServiceHistoryResponse> getHistoryByVehicleId(Long vehicleId);
    BigDecimal getTotalMaintenanceCost(Long vehicleId);
    Long getLatestOdometer(Long vehicleId);
}
