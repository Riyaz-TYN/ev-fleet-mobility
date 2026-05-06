package com.evfleetmobility.useronboarding.profileservices.entity;

import com.evfleetmobility.useronboarding.authservices.entity.ApprovalStatus;
import com.evfleetmobility.useronboarding.authservices.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "individual_details")
public class IndividualDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(nullable = true)
    private String phoneNumber;

    @Column(nullable = true)
    private String gender;

    private String fullName;
    private String companyName;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_approval_status", nullable = true)
    private ApprovalStatus companyApprovalStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = true)
    private OrganizationDetails organizationDetails;

    private String personalEmail;
    private String countryCode;
    private String addressLine1;
    private String addressLine2;

    @Column(name = "pan_card_content", columnDefinition = "BYTEA")
    private byte[] panCardContent;

    private String panCardContentType;

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

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public ApprovalStatus getCompanyApprovalStatus() { return companyApprovalStatus; }
    public void setCompanyApprovalStatus(ApprovalStatus companyApprovalStatus) { this.companyApprovalStatus = companyApprovalStatus; }

    public OrganizationDetails getOrganizationDetails() { return organizationDetails; }
    public void setOrganizationDetails(OrganizationDetails organizationDetails) { this.organizationDetails = organizationDetails; }

    public String getPersonalEmail() { return personalEmail; }
    public void setPersonalEmail(String personalEmail) { this.personalEmail = personalEmail; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

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


