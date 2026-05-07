package com.complaint_resolution.vendor.service.impl;


import com.complaint_resolution.vendor.service.VendorResolutionService;
import com.complaint_resolution.complaint.entity.Complaint;
import com.complaint_resolution.complaint.repository.ComplaintRepository;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("vendorResolutionService")
public class VendorResolutionServiceImpl implements VendorResolutionService, JavaDelegate {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Override
    public void execute(DelegateExecution execution) {

        System.out.println(" Vendor resolution running...");

        Long complaintId =
                (Long) execution.getVariable("complaintId");

        Complaint complaint =
                complaintRepository.findById(complaintId)
                        .orElse(null);

        if (complaint != null) {

            // Preserve existing vendor
            String assignedVendor =
                    complaint.getAssignedTeam();

            // Update only status
            complaint.setStatus("RESOLVED");

            // Keep original vendor name
            complaint.setAssignedTeam(
                    assignedVendor
            );

            complaintRepository.save(complaint);

            System.out.println(
                    "✅ Vendor resolved → DB updated"
            );
        }
    }
}