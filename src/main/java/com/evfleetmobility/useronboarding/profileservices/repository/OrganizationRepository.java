package com.evfleetmobility.useronboarding.profileservices.repository;

import com.evfleetmobility.useronboarding.authservices.entity.ApprovalStatus;
import com.evfleetmobility.useronboarding.profileservices.entity.OrganizationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<OrganizationDetails, Long> {
    boolean existsByPhoneNumber(String phoneNumber);

    Optional<OrganizationDetails> findByCompanyName(String companyName);

    Optional<OrganizationDetails> findByCompanyNameIgnoreCase(String companyName);

    List<OrganizationDetails> findByApprovalStatus(ApprovalStatus status);
    Page<OrganizationDetails> findByApprovalStatus(ApprovalStatus status, Pageable pageable);

    List<OrganizationDetails> findByApprovalStatusAndVendorAvailabilityTrue(ApprovalStatus status);
    Page<OrganizationDetails> findByApprovalStatusAndVendorAvailabilityTrue(ApprovalStatus status, Pageable pageable);

    List<OrganizationDetails> findByExpertiseIgnoreCase(String expertise);
    Page<OrganizationDetails> findByExpertiseIgnoreCase(String expertise, Pageable pageable);

    List<OrganizationDetails> findByVendorAvailability(Boolean availability);
    Page<OrganizationDetails> findByVendorAvailability(Boolean availability, Pageable pageable);
}
