package com.evfleetmobility.useronboarding.authservices.service.impl;

import com.evfleetmobility.common.exception.InvalidPasswordException;
import com.evfleetmobility.common.exception.TokenException;
import com.evfleetmobility.common.exception.UserAlreadyExistsException;
import com.evfleetmobility.common.exception.UserNotFoundException;
import com.evfleetmobility.common.security.JwtUtil;
import com.evfleetmobility.useronboarding.authservices.dto.AuthResponse;
import com.evfleetmobility.useronboarding.authservices.dto.LoginRequest;
import com.evfleetmobility.useronboarding.authservices.dto.RefreshRequest;
import com.evfleetmobility.useronboarding.authservices.dto.SignupRequest;
import com.evfleetmobility.useronboarding.authservices.dto.UserResponse;
import com.evfleetmobility.useronboarding.authservices.entity.ApprovalStatus;
import com.evfleetmobility.useronboarding.authservices.entity.User;
import com.evfleetmobility.useronboarding.authservices.entity.UserType;
import com.evfleetmobility.useronboarding.authservices.repository.UserRepository;
import com.evfleetmobility.useronboarding.authservices.service.AuthService;
import com.evfleetmobility.useronboarding.profileservices.entity.IndividualDetails;
import com.evfleetmobility.useronboarding.profileservices.entity.OrganizationDetails;
import com.evfleetmobility.useronboarding.profileservices.repository.IndividualRepository;
import com.evfleetmobility.useronboarding.profileservices.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final IndividualRepository individualRepo;
    private final OrganizationRepository organizationRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public UserResponse signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already registered");
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            if (individualRepo.existsByPhoneNumber(request.getPhoneNumber())
                    || organizationRepo.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new UserAlreadyExistsException("Phone number is already registered");
            }
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setUsername(request.getFullName());
        user.setUserType(UserType.valueOf(request.getUserType().toUpperCase()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (user.getUserType() == UserType.INDIVIDUAL) {
            IndividualDetails ind = new IndividualDetails();
            ind.setFullName(request.getFullName());
            ind.setPhoneNumber(request.getPhoneNumber());
            ind.setCountryCode(request.getCountryCode());
            ind.setGender(request.getGender());
            ind.setUser(user);
            user.setIndividualDetails(ind);
        } else {
            OrganizationDetails org = organizationRepo.findByCompanyName(request.getCompanyName())
                    .orElseGet(() -> {
                        OrganizationDetails newOrg = new OrganizationDetails();
                        newOrg.setCompanyName(request.getCompanyName());
                        newOrg.setPhoneNumber(request.getPhoneNumber());
                        newOrg.setCountryCode(request.getCountryCode());
                        newOrg.setEmail(request.getEmail());
                        return newOrg;
                    });
            user.setOrganizationDetails(org);
        }

        // Auto-approve SUPER_ADMIN (TYN)
        if ("SUPER_ADMIN".equalsIgnoreCase(user.getRole())) {
            user.setApprovalStatus(ApprovalStatus.APPROVED);
            if (user.getOrganizationDetails() != null) {
                user.getOrganizationDetails().setApprovalStatus(ApprovalStatus.APPROVED);
            }
        } else {
            user.setApprovalStatus(ApprovalStatus.PENDING);
        }

        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser.getUsername(), savedUser.getEmail());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Invalid password");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public AuthResponse refresh(RefreshRequest request) {
        String token = request.getRefreshToken();

        if (!"REFRESH".equals(jwtUtil.getTokenType(token))) {
            throw new TokenException("Invalid token type. Refresh token required.");
        }

        Long userId = Long.valueOf(jwtUtil.extractSubject(token));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole());
        return new AuthResponse(newAccessToken, token);
    }
}
