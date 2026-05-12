package com.evfleetmobility.useronboarding.adminservices.service;

import com.evfleetmobility.useronboarding.adminservices.dto.CompanyDetailsResponse;
import com.evfleetmobility.useronboarding.adminservices.dto.StatusRequest;
import com.evfleetmobility.useronboarding.adminservices.dto.UserDetailsResponse;
import com.evfleetmobility.useronboarding.authservices.entity.ApprovalStatus;



import org.springframework.data.domain.Page;

public interface AdminService {
    Page<UserDetailsResponse> getUsersByRoleAndStatus(Long callerId, ApprovalStatus status, int page, int size);
    Page<UserDetailsResponse> getIndividualsByRoleAndStatus(Long callerId, ApprovalStatus status, int page, int size);
    Page<CompanyDetailsResponse> getOrganizationsByRoleAndStatus(Long callerId, ApprovalStatus status, int page, int size);
    void handleUnifiedApproval(Long callerId, StatusRequest request);
    void assignDriverToVehicle(Long callerId, com.evfleetmobility.useronboarding.adminservices.dto.DriverAssignmentRequest request);
    void updateVendorRating(Long callerId, Long targetUserId, Double rating);
}
