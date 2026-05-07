package com.complaint_resolution.auth.service;

import com.complaint_resolution.auth.entity.User;
import com.complaint_resolution.auth.repository.UserRepository;
public interface AuthService {
    User validateLogin(String email, String password);
}
