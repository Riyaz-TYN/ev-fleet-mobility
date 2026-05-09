package com.evfleetmobility.useronboarding.profileservices.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileResponse {

    private String userType;
    private String email;
    private String panNumber;
    private String approvalStatus;
    private Double latitude;
    private Double longitude;

    private String fullName;
    private String personalEmail;
    private String gender;
    private String phoneNumber;
    private String countryCode;
    private String addressLine1;
    private String addressLine2;

    private String companyName;
    private String companyEmail;
    private String gstin;
    private String companyPhoneNumber;
    private String companyCountryCode;
    private String companyAddressLine1;
    private String gstinDocumentUrl;

    private String companyApprovalStatus;
}
