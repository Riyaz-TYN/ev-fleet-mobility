package com.evfleetmobility.complaintresolution.vendor.service;

import com.evfleetmobility.complaintresolution.vendor.dto.VendorDashboardStatsDTO;

public interface VendorDashboardService {
    VendorDashboardStatsDTO getVendorStats(Long vendorId);
}
