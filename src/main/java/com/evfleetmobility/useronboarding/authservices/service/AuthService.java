package com.evfleetmobility.useronboarding.authservices.service;

import com.evfleetmobility.useronboarding.authservices.dto.AuthResponse;
import com.evfleetmobility.useronboarding.authservices.dto.LoginRequest;
import com.evfleetmobility.useronboarding.authservices.dto.RefreshRequest;
import com.evfleetmobility.useronboarding.authservices.dto.SignupRequest;
import com.evfleetmobility.useronboarding.authservices.dto.UserResponse;

public interface AuthService {
    UserResponse signup(SignupRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refresh(RefreshRequest request);
}


