package com.evfleetmobility.useronboarding.profileservices.repository;

import com.evfleetmobility.useronboarding.authservices.entity.ApprovalStatus;
import com.evfleetmobility.useronboarding.profileservices.entity.OrganizationDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<OrganizationDetails, Long> {
    boolean existsByPhoneNumber(String phoneNumber);

    // Used during signup to find an existing org by exact company name
    Optional<OrganizationDetails> findByCompanyName(String companyName);

    // Used when individual fills profile — case-insensitive match
    Optional<OrganizationDetails> findByCompanyNameIgnoreCase(String companyName);

    // Used by admin to filter companies by approval status
    List<OrganizationDetails> findByApprovalStatus(ApprovalStatus status);
}


