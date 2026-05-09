package com.evfleetmobility.useronboarding.adminservices.service.impl;

import com.evfleetmobility.common.exception.UserNotFoundException;
import com.evfleetmobility.useronboarding.adminservices.dto.CompanyDetailsResponse;
import com.evfleetmobility.useronboarding.adminservices.dto.StatusRequest;
import com.evfleetmobility.useronboarding.adminservices.dto.UserDetailsResponse;
import com.evfleetmobility.useronboarding.adminservices.service.AdminService;
import com.evfleetmobility.useronboarding.authservices.entity.ApprovalStatus;
import com.evfleetmobility.useronboarding.authservices.entity.User;
import com.evfleetmobility.useronboarding.authservices.entity.UserType;
import com.evfleetmobility.useronboarding.authservices.repository.UserRepository;
import com.evfleetmobility.useronboarding.profileservices.entity.IndividualDetails;
import com.evfleetmobility.useronboarding.profileservices.entity.OrganizationDetails;
import com.evfleetmobility.useronboarding.profileservices.repository.IndividualRepository;
import com.evfleetmobility.useronboarding.profileservices.repository.OrganizationRepository;
import com.evfleetmobility.useronboarding.vehicleservices.repository.VehicleRepository;
import com.evfleetmobility.useronboarding.vehicleservices.entity.Vehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final IndividualRepository individualRepo;
    private final OrganizationRepository organizationRepo;
    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDetailsResponse> getUsersByRoleAndStatus(Long callerId, ApprovalStatus status) {
        User caller = userRepository.findById(callerId)
                .orElseThrow(() -> new UserNotFoundException("Caller not found"));

        if ("SUPER_ADMIN".equalsIgnoreCase(caller.getRole())) {
            List<User> users = status != null ? userRepository.findByApprovalStatus(status) : userRepository.findAll();
            return users.stream().map(this::mapToUserDetailsResponse).collect(Collectors.toList());
        } else if ("ADMIN".equalsIgnoreCase(caller.getRole())) {
            List<User> users = status != null ? userRepository.findByApprovalStatus(status) : userRepository.findAll();
            return users.stream()
                    .filter(u -> !"SUPER_ADMIN".equalsIgnoreCase(u.getRole()) && !"ADMIN".equalsIgnoreCase(u.getRole()))
                    .map(this::mapToUserDetailsResponse)
                    .collect(Collectors.toList());
        } else if ("VENDOR_ADMIN".equalsIgnoreCase(caller.getRole())) {
            return getIndividualsByRoleAndStatus(callerId, status);
        }
        throw new AccessDeniedException("You do not have permission to list users");
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDetailsResponse> getIndividualsByRoleAndStatus(Long callerId, ApprovalStatus status) {
        User caller = userRepository.findById(callerId)
                .orElseThrow(() -> new UserNotFoundException("Caller not found"));

        if ("SUPER_ADMIN".equalsIgnoreCase(caller.getRole())) {
            List<User> inds = status != null
                    ? userRepository.findByUserTypeAndApprovalStatus(UserType.INDIVIDUAL, status)
                    : userRepository.findByUserType(UserType.INDIVIDUAL);
            return inds.stream().map(this::mapToUserDetailsResponse).collect(Collectors.toList());
        } else if ("ADMIN".equalsIgnoreCase(caller.getRole())) {
            List<User> inds = status != null
                    ? userRepository.findByUserTypeAndApprovalStatus(UserType.INDIVIDUAL, status)
                    : userRepository.findByUserType(UserType.INDIVIDUAL);
            return inds.stream()
                    .filter(u -> !"SUPER_ADMIN".equalsIgnoreCase(u.getRole()) && !"ADMIN".equalsIgnoreCase(u.getRole()))
                    .map(this::mapToUserDetailsResponse)
                    .collect(Collectors.toList());
        } else if ("VENDOR_ADMIN".equalsIgnoreCase(caller.getRole())) {
            if (caller.getOrganizationDetails() == null) {
                throw new RuntimeException("Vendor admin not linked to an organization");
            }
            Long orgId = caller.getOrganizationDetails().getId();
            List<IndividualDetails> individuals = status != null
                    ? individualRepo.findByOrganizationDetailsIdAndCompanyApprovalStatus(orgId, status)
                    : individualRepo.findByOrganizationDetailsId(orgId);
            return individuals.stream()
                    .map(ind -> mapToUserDetailsResponse(ind.getUser()))
                    .collect(Collectors.toList());
        }
        throw new AccessDeniedException("You do not have permission to list individuals");
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyDetailsResponse> getOrganizationsByRoleAndStatus(Long callerId, ApprovalStatus status) {
        User caller = userRepository.findById(callerId)
                .orElseThrow(() -> new UserNotFoundException("Caller not found"));

        List<OrganizationDetails> orgs = status != null
                ? organizationRepo.findByApprovalStatus(status)
                : organizationRepo.findAll();

        if ("SUPER_ADMIN".equalsIgnoreCase(caller.getRole())) {
            return orgs.stream().map(this::mapToCompanyDetailsResponse).collect(Collectors.toList());
        } else if ("ADMIN".equalsIgnoreCase(caller.getRole())) {
            return orgs.stream()
                    .filter(org -> {
                        User orgUser = userRepository.findByUserType(UserType.ORGANIZATION).stream()
                                .filter(u -> u.getOrganizationDetails() != null && u.getOrganizationDetails().getId().equals(org.getId()))
                                .findFirst().orElse(null);
                        return orgUser != null && "VENDOR_ADMIN".equalsIgnoreCase(orgUser.getRole());
                    })
                    .map(this::mapToCompanyDetailsResponse)
                    .collect(Collectors.toList());
        }
        throw new AccessDeniedException("You do not have permission to list organizations");
    }


    @Override
    @Transactional
    public void handleUnifiedApproval(Long callerId, StatusRequest request) {
        User caller = userRepository.findById(callerId)
                .orElseThrow(() -> new UserNotFoundException("Caller not found"));

        Long targetId = request.getTargetId();
        ApprovalStatus newStatus = request.getStatus();

        if ("SUPER_ADMIN".equalsIgnoreCase(caller.getRole()) || "ADMIN".equalsIgnoreCase(caller.getRole())) {
            User targetUser = userRepository.findById(targetId)
                    .orElseThrow(() -> new UserNotFoundException("Target user not found"));

            targetUser.setApprovalStatus(newStatus);

            if (targetUser.getUserType() == UserType.ORGANIZATION && targetUser.getOrganizationDetails() != null) {
                targetUser.getOrganizationDetails().setApprovalStatus(newStatus);
            }
            if (targetUser.getUserType() == UserType.INDIVIDUAL && targetUser.getIndividualDetails() != null) {
                targetUser.getIndividualDetails().setCompanyApprovalStatus(newStatus);
            }
            userRepository.save(targetUser);

        } else if ("VENDOR_ADMIN".equalsIgnoreCase(caller.getRole())) {
            updateEmployeeStatus(callerId, targetId, newStatus);
        } else {
            throw new AccessDeniedException("You do not have permission to perform this action");
        }
    }

    private void updateEmployeeStatus(Long vendorUserId, Long targetUserId, ApprovalStatus status) {
        User vendorUser = userRepository.findById(vendorUserId)
                .orElseThrow(() -> new UserNotFoundException("Vendor user not found"));

        if (vendorUser.getUserType() != UserType.ORGANIZATION || vendorUser.getOrganizationDetails() == null) {
            throw new AccessDeniedException("Only vendor admins can approve employees");
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("Target user not found"));

        if (targetUser.getUserType() != UserType.INDIVIDUAL || targetUser.getIndividualDetails() == null) {
            throw new RuntimeException("Target user is not an individual");
        }

        IndividualDetails ind = targetUser.getIndividualDetails();
        if (ind.getOrganizationDetails() == null || !ind.getOrganizationDetails().getId().equals(vendorUser.getOrganizationDetails().getId())) {
            throw new AccessDeniedException("This user does not belong to your organization");
        }

        ind.setCompanyApprovalStatus(status);
        individualRepo.save(ind);
    }

    @Override
    @Transactional
    public void assignDriverToVehicle(Long callerId, com.evfleetmobility.useronboarding.adminservices.dto.DriverAssignmentRequest request) {
        User caller = userRepository.findById(callerId)
                .orElseThrow(() -> new UserNotFoundException("Caller not found"));

        if (!"SUPER_ADMIN".equalsIgnoreCase(caller.getRole()) && !"ADMIN".equalsIgnoreCase(caller.getRole()) && !"VENDOR_ADMIN".equalsIgnoreCase(caller.getRole())) {
            throw new AccessDeniedException("You do not have permission to assign drivers to vehicles");
        }

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        User driver = userRepository.findById(request.getDriverId())
                .orElseThrow(() -> new UserNotFoundException("Driver not found"));

        vehicle.setUser(driver);
        vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public void updateVendorRating(Long callerId, Long targetUserId, Double rating) {
        User caller = userRepository.findById(callerId)
                .orElseThrow(() -> new UserNotFoundException("Caller not found"));

        if (!"SUPER_ADMIN".equalsIgnoreCase(caller.getRole()) && !"ADMIN".equalsIgnoreCase(caller.getRole())) {
            throw new AccessDeniedException("Only Admins can update vendor ratings");
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("Target user not found"));

        if (targetUser.getUserType() == UserType.ORGANIZATION && targetUser.getOrganizationDetails() != null) {
            targetUser.getOrganizationDetails().setVendorRating(rating);
        } else if (targetUser.getUserType() == UserType.INDIVIDUAL && targetUser.getIndividualDetails() != null) {
            targetUser.getIndividualDetails().setVendorRating(rating);
        } else {
            throw new RuntimeException("Target user does not have a profile capable of receiving a vendor rating");
        }
        
        userRepository.save(targetUser);
    }

    private UserDetailsResponse mapToUserDetailsResponse(User user) {
        UserDetailsResponse.UserDetailsResponseBuilder builder = UserDetailsResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .userType(user.getUserType())
                .approvalStatus(user.getApprovalStatus());

        if (user.getUserType() == UserType.INDIVIDUAL && user.getIndividualDetails() != null) {
            IndividualDetails ind = user.getIndividualDetails();
            builder.phoneNumber(ind.getPhoneNumber())
                   .fullName(ind.getFullName())
                   .panNumber(ind.getPanNumber())
                   .vendorRating(ind.getVendorRating())
                   .vendorAvailability(ind.getVendorAvailability())
                   .expertise(ind.getExpertise());

            if (ind.getOrganizationDetails() != null) {
                builder.companyName(ind.getOrganizationDetails().getCompanyName())
                       .companyApprovalStatus(ind.getCompanyApprovalStatus() != null
                               ? ind.getCompanyApprovalStatus().name() : "PENDING");
            } else if (ind.getCompanyName() != null) {
                builder.companyName(ind.getCompanyName());
            }

        } else if (user.getUserType() == UserType.ORGANIZATION && user.getOrganizationDetails() != null) {
            OrganizationDetails org = user.getOrganizationDetails();
            builder.phoneNumber(org.getPhoneNumber())
                   .companyName(org.getCompanyName())
                   .panNumber(org.getPanNumber())
                   .gstin(org.getGstin())
                   .gstinDocumentUrl(org.getGstinDocumentUrl())
                   .vendorRating(org.getVendorRating())
                   .vendorAvailability(org.getVendorAvailability())
                   .expertise(org.getExpertise());
        }

        return builder.build();
    }

    private CompanyDetailsResponse mapToCompanyDetailsResponse(OrganizationDetails org) {
        return CompanyDetailsResponse.builder()
                .id(org.getId())
                .companyName(org.getCompanyName())
                .email(org.getEmail())
                .phoneNumber(org.getPhoneNumber())
                .gstin(org.getGstin())
                .panNumber(org.getPanNumber())
                .approvalStatus(org.getApprovalStatus() != null ? org.getApprovalStatus().name() : "PENDING")
                .latitude(org.getLatitude())
                .longitude(org.getLongitude())
                .gstinDocumentUrl(org.getGstinDocumentUrl())
                .vendorRating(org.getVendorRating())
                .vendorAvailability(org.getVendorAvailability())
                .expertise(org.getExpertise())
                .build();
    }
}


