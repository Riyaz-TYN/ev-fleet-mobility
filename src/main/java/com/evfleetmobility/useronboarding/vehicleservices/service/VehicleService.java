package com.evfleetmobility.useronboarding.vehicleservices.service;

import com.evfleetmobility.useronboarding.vehicleservices.dto.VehicleRequest;
import com.evfleetmobility.useronboarding.vehicleservices.dto.VehicleResponse;

import java.util.List;

public interface VehicleService {
    VehicleResponse addVehicle(VehicleRequest request);

    VehicleResponse updateVehicle(Long vehicleId, VehicleRequest request);

    void deleteVehicle(Long vehicleId);

    VehicleResponse getVehicleById(Long vehicleId);

    List<VehicleResponse> getAllVehicles();
}
