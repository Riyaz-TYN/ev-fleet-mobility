package com.evfleetmobility.complaintresolution.complaint.repository;

import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    Page<Complaint> findByCustomerIdOrderByCreatedAtDesc(String customerId, Pageable pageable);

    List<Complaint> findByCustomerIdAndIssueCategory(
            String customerId,
            String issueCategory
    );

    long countByCustomerIdAndIssueCategory(
            String customerId,
            String issueCategory
    );

    Page<Complaint> findByVehicleIdOrderByCreatedAtDesc(String vehicleId, Pageable pageable);

    Page<Complaint> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    Page<Complaint> findByPriorityOrderByCreatedAtDesc(String priority, Pageable pageable);

    Page<Complaint> findByAssignedTeamOrderByCreatedAtDesc(
            String assignedTeam, Pageable pageable
    );

    Page<Complaint> findByVendorIdOrderByCreatedAtDesc(Long vendorId, Pageable pageable);

    List<Complaint> findByCustomerId(String customerId);

    List<Complaint> findByVehicleId(String vehicleId);

    List<Complaint> findByStatus(String status);
    
    Page<Complaint> findByTechnicianIdOrderByCreatedAtDesc(Long technicianId, Pageable pageable);

    List<Complaint> findByVendorId(Long vendorId);
}