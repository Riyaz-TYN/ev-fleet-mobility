package com.evfleetmobility.complaintresolution.aiservices.repository;

import com.evfleetmobility.complaintresolution.aiservices.entity.AIResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AIResponseRepository extends JpaRepository<AIResponse, Long> {
    List<AIResponse> findByQueryIdOrderByCreatedAtDesc(Long queryId);
    List<AIResponse> findByUserIdOrderByCreatedAtDesc(Long userId);
}
