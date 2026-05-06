package com.evfleetmobility.useronboarding.adminservices.dto;

import com.evfleetmobility.useronboarding.authservices.entity.ApprovalStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusRequest {
    private Long targetId;
    private ApprovalStatus status;
}


