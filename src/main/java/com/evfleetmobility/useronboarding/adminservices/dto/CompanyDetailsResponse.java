package com.evfleetmobility.useronboarding.adminservices.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyDetailsResponse {
    private Long id;
    private String companyName;
    private String email;
    private String phoneNumber;
    private String gstin;
    private String panNumber;
    private String approvalStatus;
    private Double latitude;
    private Double longitude;
    private String gstinDocumentUrl;

    // Admin-only vendor fields
    private Double vendorRating;
    private Boolean vendorAvailability;
    private String expertise;
}


