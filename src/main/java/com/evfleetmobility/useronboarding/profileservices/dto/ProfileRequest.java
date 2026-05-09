package com.evfleetmobility.useronboarding.profileservices.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProfileRequest {

    private String fullName;
    private String personalEmail;
    private String phoneNumber;
    private String countryCode;
    private String gender;
    private String addressLine1;
    private String addressLine2;
    private String panNumber;
    private Double latitude;
    private Double longitude;

    private String companyName;
    private String companyEmail;
    private String gstin;

    private Boolean vendorAvailability;
    private String expertise;

    private MultipartFile panCardFile;
    private MultipartFile gstinDocumentFile;
}
