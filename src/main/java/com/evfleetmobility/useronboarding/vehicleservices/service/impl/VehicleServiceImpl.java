package com.evfleetmobility.useronboarding.vehicleservices.service.impl;

import com.evfleetmobility.common.exception.UserNotFoundException;
import com.evfleetmobility.common.exception.VehicleNotFoundException;
import com.evfleetmobility.useronboarding.authservices.entity.User;
import com.evfleetmobility.useronboarding.authservices.repository.UserRepository;
import com.evfleetmobility.useronboarding.vehicleservices.dto.VehicleRequest;
import com.evfleetmobility.useronboarding.vehicleservices.dto.VehicleResponse;
import com.evfleetmobility.useronboarding.vehicleservices.entity.Vehicle;
import com.evfleetmobility.useronboarding.vehicleservices.entity.VehicleStatus;
import com.evfleetmobility.useronboarding.vehicleservices.repository.VehicleRepository;
import com.evfleetmobility.useronboarding.vehicleservices.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    @Override
    public VehicleResponse addVehicle(VehicleRequest request) {

        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + request.getUserId()));
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setUser(user);
        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setVin(request.getVin());
        vehicle.setChassisNo(request.getChassisNo());
        if (request.getStatus() != null) {
            try {
                vehicle.setStatus(VehicleStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }
        }
        vehicle.setYearOfManufacture(request.getYearOfManufacture());
        vehicle.setBatteryCapacityKwh(request.getBatteryCapacityKwh());

        return mapToResponse(vehicleRepository.save(vehicle));
    }

    @Override
    public VehicleResponse updateVehicle(Long vehicleId, VehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with ID: " + vehicleId));

        if (request.getUserId() != null && !request.getUserId().equals(vehicle.getUser().getId())) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + request.getUserId()));
            vehicle.setUser(user);
        }

        if (request.getMake() != null)
            vehicle.setMake(request.getMake());
        if (request.getModel() != null)
            vehicle.setModel(request.getModel());
        if (request.getLicensePlate() != null)
            vehicle.setLicensePlate(request.getLicensePlate());
        if (request.getVin() != null)
            vehicle.setVin(request.getVin());
        if (request.getChassisNo() != null)
            vehicle.setChassisNo(request.getChassisNo());
        if (request.getStatus() != null) {
            try {
                vehicle.setStatus(VehicleStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (request.getYearOfManufacture() != null)
            vehicle.setYearOfManufacture(request.getYearOfManufacture());
        if (request.getBatteryCapacityKwh() != null)
            vehicle.setBatteryCapacityKwh(request.getBatteryCapacityKwh());

        return mapToResponse(vehicleRepository.save(vehicle));
    }

    @Override
    public void deleteVehicle(Long vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new VehicleNotFoundException("Vehicle not found with ID: " + vehicleId);
        }
        vehicleRepository.deleteById(vehicleId);
    }

    @Override
    public VehicleResponse getVehicleById(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with ID: " + vehicleId));
        return mapToResponse(vehicle);
    }

    @Override
    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private VehicleResponse mapToResponse(Vehicle vehicle) {
        VehicleResponse response = new VehicleResponse();
        response.setId(vehicle.getId());
        if (vehicle.getUser() != null) {
            response.setUserId(vehicle.getUser().getId());
        }
        response.setMake(vehicle.getMake());
        response.setModel(vehicle.getModel());
        response.setLicensePlate(vehicle.getLicensePlate());
        response.setVin(vehicle.getVin());
        response.setStatus(vehicle.getStatus() != null ? vehicle.getStatus().name() : null);
        response.setYearOfManufacture(vehicle.getYearOfManufacture());
        response.setBatteryCapacityKwh(vehicle.getBatteryCapacityKwh());
        response.setChassisNo(vehicle.getChassisNo());
        return response;
    }
}
