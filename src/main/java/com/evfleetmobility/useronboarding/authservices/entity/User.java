package com.evfleetmobility.useronboarding.authservices.entity;

import com.evfleetmobility.useronboarding.profileservices.entity.IndividualDetails;
import com.evfleetmobility.useronboarding.profileservices.entity.OrganizationDetails;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) default 'PENDING'")
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private IndividualDetails individualDetails;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private OrganizationDetails organizationDetails;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }

    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }

    public IndividualDetails getIndividualDetails() { return individualDetails; }
    public void setIndividualDetails(IndividualDetails individualDetails) { this.individualDetails = individualDetails; }

    public OrganizationDetails getOrganizationDetails() { return organizationDetails; }
    public void setOrganizationDetails(OrganizationDetails organizationDetails) { this.organizationDetails = organizationDetails; }
}


