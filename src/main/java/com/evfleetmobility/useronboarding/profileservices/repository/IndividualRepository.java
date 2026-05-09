package com.evfleetmobility.useronboarding.profileservices.repository;

import com.evfleetmobility.useronboarding.authservices.entity.ApprovalStatus;
import com.evfleetmobility.useronboarding.profileservices.entity.IndividualDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IndividualRepository extends JpaRepository<IndividualDetails, Long> {
    boolean existsByPhoneNumber(String phoneNumber);

    List<IndividualDetails> findByOrganizationDetailsId(Long orgId);
    List<IndividualDetails> findByOrganizationDetailsIdAndCompanyApprovalStatus(Long orgId, ApprovalStatus status);
}
