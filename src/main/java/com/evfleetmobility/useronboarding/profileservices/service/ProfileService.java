package com.evfleetmobility.useronboarding.profileservices.service;

import com.evfleetmobility.useronboarding.profileservices.dto.ProfileRequest;
import com.evfleetmobility.useronboarding.profileservices.dto.ProfileResponse;

public interface ProfileService {
    ProfileResponse getMyProfile(Long userId);
    String completeProfile(Long userId, ProfileRequest request);
}


