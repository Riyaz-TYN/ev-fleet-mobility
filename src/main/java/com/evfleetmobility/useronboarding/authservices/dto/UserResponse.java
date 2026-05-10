package com.evfleetmobility.useronboarding.authservices.dto;

public class UserResponse {
    private String fullName;
    private String email;

    public UserResponse(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
}
