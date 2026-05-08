package com.evfleetmobility.complaintresolution.aiservices.repository;

import com.evfleetmobility.complaintresolution.aiservices.entity.AIQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AIQueryRepository extends JpaRepository<AIQuery, Long> {
    List<AIQuery> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<AIQuery> findByVehicleIdOrderByCreatedAtDesc(String vehicleId);
}
