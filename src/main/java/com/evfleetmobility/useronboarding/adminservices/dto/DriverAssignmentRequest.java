package com.evfleetmobility.useronboarding.adminservices.dto;

import lombok.Data;

@Data
public class DriverAssignmentRequest {
    private Long vehicleId;
    private Long driverId;
}
