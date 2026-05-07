package com.evfleetmobility.complaintresolution.complaint.repository;

import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    // FETCH COMPLAINTS FOR LOGGED-IN USER
    List<Complaint> findByCustomerIdOrderByCreatedAtDesc(String customerId);

    List<Complaint> findByCustomerIdAndIssueCategory(
            String customerId,
            String issueCategory
    );

    // REPEAT DETECTION
    long countByCustomerIdAndIssueCategory(
            String customerId,
            String issueCategory
    );

    // VEHICLE FILTER
    List<Complaint> findByVehicleIdOrderByCreatedAtDesc(String vehicleId);

    // STATUS FILTER
    List<Complaint> findByStatusOrderByCreatedAtDesc(String status);

    // PRIORITY FILTER
    List<Complaint> findByPriorityOrderByCreatedAtDesc(String priority);

    // VENDOR TEAM COMPLAINTS
    List<Complaint> findByAssignedTeamOrderByCreatedAtDesc(
            String assignedTeam
    );

    // GENERAL USER FILTERING (RBAC READY)
    List<Complaint> findByCustomerId(String customerId);

    // GENERAL VEHICLE FILTERING (RBAC READY)
    List<Complaint> findByVehicleId(String vehicleId);

    // GENERAL STATUS FILTERING (RBAC READY)
    List<Complaint> findByStatus(String status);
}