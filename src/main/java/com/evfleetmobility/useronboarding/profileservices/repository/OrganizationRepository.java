package com.evfleetmobility.useronboarding.profileservices.repository;

import com.evfleetmobility.useronboarding.authservices.entity.ApprovalStatus;
import com.evfleetmobility.useronboarding.profileservices.entity.OrganizationDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<OrganizationDetails, Long> {
    boolean existsByPhoneNumber(String phoneNumber);

    Optional<OrganizationDetails> findByCompanyName(String companyName);

    Optional<OrganizationDetails> findByCompanyNameIgnoreCase(String companyName);

    List<OrganizationDetails> findByApprovalStatus(ApprovalStatus status);

    List<OrganizationDetails> findByVendorAvailabilityTrue();
    List<OrganizationDetails> findByExpertiseIgnoreCase(String expertise);
    List<OrganizationDetails> findByVendorAvailability(Boolean availability);
}
