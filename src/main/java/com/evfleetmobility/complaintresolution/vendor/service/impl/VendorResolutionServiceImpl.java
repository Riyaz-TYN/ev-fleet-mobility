package com.evfleetmobility.complaintresolution.vendor.service.impl;

import com.evfleetmobility.complaintresolution.auditlog.service.AuditLogService;
import com.evfleetmobility.complaintresolution.vendor.service.VendorResolutionService;
import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.complaint.repository.ComplaintRepository;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("vendorResolutionService")
public class VendorResolutionServiceImpl implements VendorResolutionService, JavaDelegate {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Override
    public void execute(DelegateExecution execution) {

        System.out.println("Vendor resolution running...");

        Long complaintId = (Long) execution.getVariable("complaintId");
        String vehicleId = (String) execution.getVariable("vehicleId");

        String vendorName = execution.getVariable("vendorName") != null
                ? execution.getVariable("vendorName").toString()
                : "Unknown Vendor";

        Boolean vendorResolved = execution.getVariable("vendorResolved") != null
                ? (Boolean) execution.getVariable("vendorResolved")
                : false;

        Complaint complaint = complaintRepository.findById(complaintId).orElse(null);

        if (complaint != null) {

            String assignedVendor = complaint.getAssignedTeam() != null
                    ? complaint.getAssignedTeam()
                    : vendorName;

            String previousStatus = complaint.getStatus();

            if (Boolean.TRUE.equals(vendorResolved)) {
                complaint.setStatus("RESOLVED");

               
                complaint.addWorkHistory(
                    "Resolved by Vendor",
                    "Vendor: " + assignedVendor,
                    "Issue successfully resolved by vendor"
                );

                complaint.setAssignedTeam(assignedVendor);
                complaintRepository.save(complaint);

             
                auditLogService.saveLog(
                        complaintId,
                        vehicleId != null ? vehicleId : complaint.getVehicleId(),
                        "VENDOR_RESOLVED",
                        "VENDOR",
                        previousStatus,
                        "RESOLVED",
                        "Complaint resolved by vendor: " + assignedVendor,
                        Map.of(
                            "vendorName", assignedVendor,
                            "vehicleId", vehicleId != null ? vehicleId : ""
                        )
                );

                System.out.println("Vendor resolved -> DB updated. Vendor: " + assignedVendor);

            } else {
              
                complaint.setStatus("ESCALATED_TO_MANAGER");
                complaint.setEscalationReason("Vendor could not resolve: " + assignedVendor);

             
                complaint.addWorkHistory(
                    "Vendor Unresolved",
                    "Vendor: " + assignedVendor,
                    "Vendor could not resolve the issue"
                );

                complaint.setAssignedTeam(assignedVendor);
                complaintRepository.save(complaint);

        
                auditLogService.saveLog(
                        complaintId,
                        vehicleId != null ? vehicleId : complaint.getVehicleId(),
                        "VENDOR_UNRESOLVED",
                        "VENDOR",
                        previousStatus,
                        "ESCALATED_TO_MANAGER",
                        "Vendor " + assignedVendor + " could not resolve the complaint",
                        Map.of(
                            "vendorName", assignedVendor,
                            "vehicleId", vehicleId != null ? vehicleId : ""
                        )
                );

                System.out.println("Vendor unresolved -> escalating. Vendor: " + assignedVendor);
            }
        }
    }
}
