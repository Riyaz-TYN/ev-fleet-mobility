package com.evfleetmobility.useronboarding.authservices.repository;

import com.evfleetmobility.useronboarding.authservices.entity.ApprovalStatus;
import com.evfleetmobility.useronboarding.authservices.entity.User;
import com.evfleetmobility.useronboarding.authservices.entity.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByApprovalStatus(ApprovalStatus status);
    List<User> findByUserType(UserType userType);
    List<User> findByUserTypeAndApprovalStatus(UserType userType, ApprovalStatus status);
}
