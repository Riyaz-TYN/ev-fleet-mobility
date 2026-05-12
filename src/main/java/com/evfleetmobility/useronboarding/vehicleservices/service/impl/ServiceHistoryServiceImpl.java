package com.evfleetmobility.useronboarding.vehicleservices.service.impl;

import com.evfleetmobility.common.exception.ServiceHistoryNotFoundException;
import com.evfleetmobility.common.exception.VehicleNotFoundException;
import com.evfleetmobility.useronboarding.vehicleservices.dto.ServiceHistoryRequest;
import com.evfleetmobility.useronboarding.vehicleservices.dto.ServiceHistoryResponse;
import com.evfleetmobility.useronboarding.vehicleservices.entity.ServiceHistory;
import com.evfleetmobility.useronboarding.vehicleservices.entity.Vehicle;
import com.evfleetmobility.useronboarding.vehicleservices.repository.ServiceHistoryRepository;
import com.evfleetmobility.useronboarding.vehicleservices.repository.VehicleRepository;
import com.evfleetmobility.useronboarding.vehicleservices.service.ServiceHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class ServiceHistoryServiceImpl implements ServiceHistoryService {

    private final ServiceHistoryRepository serviceHistoryRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    public ServiceHistoryResponse addServiceEntry(ServiceHistoryRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with ID: " + request.getVehicleId()));

        ServiceHistory serviceHistory = new ServiceHistory();
        serviceHistory.setVehicle(vehicle);
        serviceHistory.setServiceDate(request.getServiceDate());
        serviceHistory.setOdometerReading(request.getOdometerReading());
        serviceHistory.setServiceType(request.getServiceType());
        serviceHistory.setDescription(request.getDescription());
        serviceHistory.setCost(request.getCost());
        serviceHistory.setProviderName(request.getProviderName());

        return mapToResponse(serviceHistoryRepository.save(serviceHistory));
    }

    @Override
    public ServiceHistoryResponse updateServiceEntry(Long historyId, ServiceHistoryRequest request) {
        ServiceHistory serviceHistory = serviceHistoryRepository.findById(historyId)
                .orElseThrow(() -> new ServiceHistoryNotFoundException("Service History not found with ID: " + historyId));

        if (request.getVehicleId() != null && !request.getVehicleId().equals(serviceHistory.getVehicle().getId())) {
            Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with ID: " + request.getVehicleId()));
            serviceHistory.setVehicle(vehicle);
        }

        if (request.getServiceDate() != null) serviceHistory.setServiceDate(request.getServiceDate());
        if (request.getOdometerReading() != null) serviceHistory.setOdometerReading(request.getOdometerReading());
        if (request.getServiceType() != null) serviceHistory.setServiceType(request.getServiceType());
        if (request.getDescription() != null) serviceHistory.setDescription(request.getDescription());
        if (request.getCost() != null) serviceHistory.setCost(request.getCost());
        if (request.getProviderName() != null) serviceHistory.setProviderName(request.getProviderName());

        return mapToResponse(serviceHistoryRepository.save(serviceHistory));
    }

    @Override
    public void deleteServiceEntry(Long historyId) {
        if (!serviceHistoryRepository.existsById(historyId)) {
            throw new ServiceHistoryNotFoundException("Service History not found with ID: " + historyId);
        }
        serviceHistoryRepository.deleteById(historyId);
    }

    @Override
    public Page<ServiceHistoryResponse> getHistoryByVehicleId(Long vehicleId, int page, int size) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new VehicleNotFoundException("Vehicle not found with ID: " + vehicleId);
        }
        Pageable pageable = PageRequest.of(page, size);
        return serviceHistoryRepository.findByVehicleId(vehicleId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public BigDecimal getTotalMaintenanceCost(Long vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new VehicleNotFoundException("Vehicle not found with ID: " + vehicleId);
        }
        return serviceHistoryRepository.findByVehicleId(vehicleId).stream()
                .map(ServiceHistory::getCost)
                .filter(cost -> cost != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Long getLatestOdometer(Long vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new VehicleNotFoundException("Vehicle not found with ID: " + vehicleId);
        }
        return serviceHistoryRepository.findByVehicleId(vehicleId).stream()
                .map(ServiceHistory::getOdometerReading)
                .filter(reading -> reading != null)
                .max(Comparator.naturalOrder())
                .orElse(0L);
    }

    private ServiceHistoryResponse mapToResponse(ServiceHistory entity) {
        ServiceHistoryResponse response = new ServiceHistoryResponse();
        response.setId(entity.getId());
        if (entity.getVehicle() != null) {
            response.setVehicleId(entity.getVehicle().getId());
        }
        response.setServiceDate(entity.getServiceDate());
        response.setOdometerReading(entity.getOdometerReading());
        response.setServiceType(entity.getServiceType());
        response.setDescription(entity.getDescription());
        response.setCost(entity.getCost());
        response.setProviderName(entity.getProviderName());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}
