package com.evfleetmobility.useronboarding.vehicleservices.service;

import com.evfleetmobility.useronboarding.vehicleservices.dto.ServiceHistoryRequest;
import com.evfleetmobility.useronboarding.vehicleservices.dto.ServiceHistoryResponse;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;

public interface ServiceHistoryService {
    ServiceHistoryResponse addServiceEntry(ServiceHistoryRequest request);
    ServiceHistoryResponse updateServiceEntry(Long historyId, ServiceHistoryRequest request);
    void deleteServiceEntry(Long historyId);
    Page<ServiceHistoryResponse> getHistoryByVehicleId(Long vehicleId, int page, int size);
    BigDecimal getTotalMaintenanceCost(Long vehicleId);
    Long getLatestOdometer(Long vehicleId);
}
