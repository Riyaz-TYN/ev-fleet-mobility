package com.evfleetmobility.useronboarding.profileservices.service.impl;

import com.evfleetmobility.common.exception.UserNotFoundException;
import com.evfleetmobility.useronboarding.authservices.entity.ApprovalStatus;
import com.evfleetmobility.useronboarding.authservices.entity.User;
import com.evfleetmobility.useronboarding.authservices.entity.UserType;
import com.evfleetmobility.useronboarding.authservices.repository.UserRepository;
import com.evfleetmobility.useronboarding.documentservices.service.DocumentService;
import com.evfleetmobility.useronboarding.profileservices.dto.ProfileRequest;
import com.evfleetmobility.useronboarding.profileservices.dto.ProfileResponse;
import com.evfleetmobility.useronboarding.profileservices.entity.IndividualDetails;
import com.evfleetmobility.useronboarding.profileservices.entity.OrganizationDetails;
import com.evfleetmobility.useronboarding.profileservices.repository.OrganizationRepository;
import com.evfleetmobility.useronboarding.profileservices.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepo;
    private final DocumentService documentService;

    @Override
    @Transactional
    public String completeProfile(Long userId, ProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getUserType() == UserType.INDIVIDUAL) {
            IndividualDetails ind = user.getIndividualDetails();
            if (ind == null) {
                ind = new IndividualDetails();
                ind.setUser(user);
            }

            if (request.getFullName() != null) ind.setFullName(request.getFullName().trim());
            if (request.getPersonalEmail() != null) ind.setPersonalEmail(request.getPersonalEmail().trim());
            if (request.getPhoneNumber() != null) ind.setPhoneNumber(request.getPhoneNumber().trim());
            if (request.getCountryCode() != null) ind.setCountryCode(request.getCountryCode().trim());
            if (request.getAddressLine1() != null) ind.setAddressLine1(request.getAddressLine1().trim());
            if (request.getAddressLine2() != null) ind.setAddressLine2(request.getAddressLine2().trim());
            if (request.getPanNumber() != null) ind.setPanNumber(request.getPanNumber().trim());
            if (request.getLatitude() != null) ind.setLatitude(request.getLatitude());
            if (request.getLongitude() != null) ind.setLongitude(request.getLongitude());

            ind.setVendorAvailability(request.getVendorAvailability());
            ind.setExpertise(request.getExpertise());

            final IndividualDetails finalInd = ind;
            if (request.getCompanyName() != null && !request.getCompanyName().isBlank()) {
                finalInd.setCompanyName(request.getCompanyName());
                organizationRepo.findByCompanyNameIgnoreCase(request.getCompanyName())
                        .ifPresent(org -> {
                            finalInd.setOrganizationDetails(org);
                            finalInd.setCompanyApprovalStatus(ApprovalStatus.PENDING);
                        });
            }

            if (request.getPanCardFile() != null && !request.getPanCardFile().isEmpty()) {
                try {
                    ind.setPanCardContent(request.getPanCardFile().getBytes());
                    ind.setPanCardContentType(request.getPanCardFile().getContentType());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to store PAN card content", e);
                }
            }

            user.setIndividualDetails(ind);

        } else if (user.getUserType() == UserType.ORGANIZATION) {
            OrganizationDetails org = user.getOrganizationDetails();
            if (org == null) {
                org = new OrganizationDetails();
            }

            if (request.getCompanyName() != null) org.setCompanyName(request.getCompanyName().trim());
            if (request.getCompanyEmail() != null) org.setEmail(request.getCompanyEmail().trim());
            if (request.getGstin() != null) org.setGstin(request.getGstin().trim());
            if (request.getPhoneNumber() != null) org.setPhoneNumber(request.getPhoneNumber().trim());
            if (request.getCountryCode() != null) org.setCountryCode(request.getCountryCode().trim());
            if (request.getAddressLine1() != null) org.setAddressLine1(request.getAddressLine1().trim());
            if (request.getPanNumber() != null) org.setPanNumber(request.getPanNumber().trim());
            if (request.getLatitude() != null) org.setLatitude(request.getLatitude());
            if (request.getLongitude() != null) org.setLongitude(request.getLongitude());

            org.setVendorAvailability(request.getVendorAvailability());
            org.setExpertise(request.getExpertise());

            if (request.getPanCardFile() != null && !request.getPanCardFile().isEmpty()) {
                try {
                    org.setPanCardContent(request.getPanCardFile().getBytes());
                    org.setPanCardContentType(request.getPanCardFile().getContentType());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to store PAN card content", e);
                }
            }
            if (request.getGstinDocumentFile() != null && !request.getGstinDocumentFile().isEmpty()) {
                var docRes = documentService.upload(userId, request.getGstinDocumentFile(), "GSTIN");
                org.setGstinDocumentUrl(docRes.getFileUrl());
            }

            user.setOrganizationDetails(org);
        }

        userRepository.save(user);
        return "Profile and documents saved successfully";
    }

    @Override
    @Transactional
    public ProfileResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        ProfileResponse.ProfileResponseBuilder builder = ProfileResponse.builder()
                .userType(user.getUserType().name())
                .email(user.getEmail())
                .approvalStatus(user.getApprovalStatus().name());

        if (user.getUserType() == UserType.INDIVIDUAL) {
            IndividualDetails ind = user.getIndividualDetails();
            if (ind != null) {
                builder
                    .fullName(ind.getFullName())
                    .personalEmail(ind.getPersonalEmail())
                    .gender(ind.getGender())
                    .phoneNumber(ind.getPhoneNumber())
                    .countryCode(ind.getCountryCode())
                    .addressLine1(ind.getAddressLine1())
                    .addressLine2(ind.getAddressLine2())
                    .panNumber(ind.getPanNumber())
                    .latitude(ind.getLatitude())
                    .longitude(ind.getLongitude());

                if (ind.getOrganizationDetails() != null) {
                    builder.companyName(ind.getOrganizationDetails().getCompanyName())
                           .companyApprovalStatus(
                               ind.getCompanyApprovalStatus() != null ? ind.getCompanyApprovalStatus().name() : "PENDING"
                           );
                } else {
                    builder.companyName(ind.getCompanyName());
                }
            }
        } else if (user.getUserType() == UserType.ORGANIZATION) {
            OrganizationDetails org = user.getOrganizationDetails();
            if (org != null) {
                builder
                    .fullName(user.getUsername())
                    .companyName(org.getCompanyName())
                    .companyEmail(org.getEmail())
                    .gstin(org.getGstin())
                    .companyPhoneNumber(org.getPhoneNumber())
                    .companyCountryCode(org.getCountryCode())
                    .companyAddressLine1(org.getAddressLine1())
                    .panNumber(org.getPanNumber())
                    .latitude(org.getLatitude())
                    .longitude(org.getLongitude())
                    .gstinDocumentUrl(org.getGstinDocumentUrl());
            }
        }

        return builder.build();
    }
}


