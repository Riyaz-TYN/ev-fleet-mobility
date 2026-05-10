package com.evfleetmobility.complaintresolution.vendor.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VendorDashboardStatsDTO {
    private Long vendorId;
    private String vendorName;
    private long totalAssigned;
    private long resolvedCount;
    private long escalatedCount;
    private long activeComplaints;
    private double successRate;
}
