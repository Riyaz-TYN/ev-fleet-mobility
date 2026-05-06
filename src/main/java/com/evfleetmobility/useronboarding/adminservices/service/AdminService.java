package com.evfleetmobility.useronboarding.adminservices.service;

import com.evfleetmobility.useronboarding.adminservices.dto.CompanyDetailsResponse;
import com.evfleetmobility.useronboarding.adminservices.dto.StatusRequest;
import com.evfleetmobility.useronboarding.adminservices.dto.UserDetailsResponse;
import com.evfleetmobility.useronboarding.authservices.entity.ApprovalStatus;

import java.util.List;

public interface AdminService {
    List<UserDetailsResponse> getUsersByRoleAndStatus(Long callerId, ApprovalStatus status);
    List<UserDetailsResponse> getIndividualsByRoleAndStatus(Long callerId, ApprovalStatus status);
    List<CompanyDetailsResponse> getOrganizationsByRoleAndStatus(Long callerId, ApprovalStatus status);
    void handleUnifiedApproval(Long callerId, StatusRequest request);
}


