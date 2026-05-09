package com.evfleetmobility.useronboarding.adminservices.dto;

import com.evfleetmobility.useronboarding.authservices.entity.ApprovalStatus;
import com.evfleetmobility.useronboarding.authservices.entity.UserType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDetailsResponse {
    private Long id;
    private String email;
    private String role;
    private UserType userType;
    private ApprovalStatus approvalStatus;
    private String phoneNumber;
    private String companyName;
    private String fullName;
    private String panNumber;
    private String gstin;
    private String gstinDocumentUrl;
    private String companyApprovalStatus;

    private Double vendorRating;
    private Boolean vendorAvailability;
    private String expertise;
}
