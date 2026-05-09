package com.evfleetmobility.useronboarding.vehicleservices.repository;

import com.evfleetmobility.useronboarding.vehicleservices.entity.ServiceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceHistoryRepository extends JpaRepository<ServiceHistory, Long> {
    List<ServiceHistory> findByVehicleId(Long vehicleId);
}
