package com.evfleetmobility.complaintresolution.complaint.repository;

import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByCustomerIdOrderByCreatedAtDesc(String customerId);

    List<Complaint> findByCustomerIdAndIssueCategory(
            String customerId,
            String issueCategory
    );

    long countByCustomerIdAndIssueCategory(
            String customerId,
            String issueCategory
    );

    List<Complaint> findByVehicleIdOrderByCreatedAtDesc(String vehicleId);

    List<Complaint> findByStatusOrderByCreatedAtDesc(String status);

    List<Complaint> findByPriorityOrderByCreatedAtDesc(String priority);

    List<Complaint> findByAssignedTeamOrderByCreatedAtDesc(
            String assignedTeam
    );

    List<Complaint> findByVendorIdOrderByCreatedAtDesc(Long vendorId);

    List<Complaint> findByCustomerId(String customerId);

    List<Complaint> findByVehicleId(String vehicleId);

    List<Complaint> findByStatus(String status);
}