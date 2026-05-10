package com.evfleetmobility.complaintresolution.vendor.service.impl;

import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.complaint.repository.ComplaintRepository;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorDashboardStatsDTO;
import com.evfleetmobility.complaintresolution.vendor.service.VendorDashboardService;
import com.evfleetmobility.useronboarding.profileservices.entity.OrganizationDetails;
import com.evfleetmobility.useronboarding.profileservices.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VendorDashboardServiceImpl implements VendorDashboardService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Override
    public VendorDashboardStatsDTO getVendorStats(Long vendorId) {
        OrganizationDetails vendor = organizationRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found with ID: " + vendorId));

        List<Complaint> vendorComplaints = complaintRepository.findByVendorIdOrderByCreatedAtDesc(vendorId);

        long totalHandled = vendorComplaints.size();
        long resolved = vendorComplaints.stream().filter(c -> "RESOLVED".equalsIgnoreCase(c.getStatus())).count();
        long escalated = vendorComplaints.stream().filter(c -> "ESCALATED_TO_MANAGER".equalsIgnoreCase(c.getStatus())).count();
        long active = vendorComplaints.stream().filter(c -> "ASSIGNED_TO_VENDOR".equalsIgnoreCase(c.getStatus())).count();

        double successRate = totalHandled > 0 ? (double) resolved / totalHandled * 100 : 0.0;

        return VendorDashboardStatsDTO.builder()
                .vendorId(vendorId)
                .vendorName(vendor.getCompanyName())
                .totalAssigned(totalHandled)
                .resolvedCount(resolved)
                .escalatedCount(escalated)
                .activeComplaints(active)
                .successRate(Math.round(successRate * 100.0) / 100.0)
                .build();
    }
}
