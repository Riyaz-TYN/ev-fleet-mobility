package com.evfleetmobility.useronboarding.vehicleservices.repository;

import com.evfleetmobility.useronboarding.vehicleservices.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByUserId(Long userId);
}
