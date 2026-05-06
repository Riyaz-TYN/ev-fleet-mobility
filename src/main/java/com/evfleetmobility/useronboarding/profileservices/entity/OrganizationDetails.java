package com.evfleetmobility.useronboarding.profileservices.entity;

import com.evfleetmobility.useronboarding.authservices.entity.ApprovalStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "organization_details")
public class OrganizationDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) default 'PENDING'")
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Column(nullable = true)
    private String companyName;

    @Column(nullable = true)
    private String gstin;

    @Column(nullable = true)
    private String email;

    @Column(nullable = true)
    private String countryCode;

    @Column(nullable = true)
    private String phoneNumber;

    @Column(nullable = true)
    private String addressLine1;

    @Column(name = "pan_card_content", columnDefinition = "BYTEA")
    private byte[] panCardContent;

    private String panCardContentType;

    @Column(nullable = true)
    private String gstinDocumentUrl;

    @Column(nullable = true)
    private String panNumber;

    @Column(nullable = true)
    private Double latitude;

    @Column(nullable = true)
    private Double longitude;

    @Column(name = "vendor_rating", nullable = false)
    private Double vendorRating = 0.0;

    @Column(name = "vendor_availability", nullable = false)
    private Boolean vendorAvailability = false;

    @Column(name = "expertise", nullable = true)
    private String expertise;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getGstin() { return gstin; }
    public void setGstin(String gstin) { this.gstin = gstin; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getGstinDocumentUrl() { return gstinDocumentUrl; }
    public void setGstinDocumentUrl(String gstinDocumentUrl) { this.gstinDocumentUrl = gstinDocumentUrl; }

    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getVendorRating() { return vendorRating; }
    public void setVendorRating(Double vendorRating) { this.vendorRating = vendorRating; }

    public Boolean getVendorAvailability() { return vendorAvailability; }
    public void setVendorAvailability(Boolean vendorAvailability) { this.vendorAvailability = vendorAvailability; }

    public String getExpertise() { return expertise; }
    public void setExpertise(String expertise) { this.expertise = expertise; }

    public byte[] getPanCardContent() { return panCardContent; }
    public void setPanCardContent(byte[] panCardContent) { this.panCardContent = panCardContent; }

    public String getPanCardContentType() { return panCardContentType; }
    public void setPanCardContentType(String panCardContentType) { this.panCardContentType = panCardContentType; }
}


