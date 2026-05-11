package com.evfleetmobility.complaintresolution.manager.service.impl;

import com.evfleetmobility.complaintresolution.auditlog.service.AuditLogService;
import com.evfleetmobility.complaintresolution.manager.service.ManagerDecisionService;
import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.complaint.repository.ComplaintRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("managerDecisionService")
public class ManagerDecisionServiceImpl implements ManagerDecisionService, JavaDelegate {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Override
    public void execute(DelegateExecution execution) {

        System.out.println("Manager decision processing...");

        Long complaintId = (Long) execution.getVariable("complaintId");
        String decision = (String) execution.getVariable("managerDecision");

        if (complaintId == null) {
            System.out.println("Warning: complaintId is null in ManagerDecisionServiceImpl");
            return;
        }

        Complaint complaint = complaintRepository.findById(complaintId).orElse(null);

        if (complaint != null) {

            String previousStatus = complaint.getStatus();

            if ("RESOLVE".equalsIgnoreCase(decision)) {
                complaint.setStatus("RESOLVED");

            } else if ("REJECT".equalsIgnoreCase(decision)) {
                complaint.setStatus("REJECTED");

            } else if ("RETRY".equalsIgnoreCase(decision)) {
                // Consistent with ManagerServiceImpl
                complaint.setStatus("RETRY_VENDOR");

            } else {
                System.out.println("Warning: Unknown manager decision: " + decision);
            }

            // ✅ Record in work summary so vendor/frontend can see it
            complaint.addWorkHistory(
                "Manager Decision",
                "Decision: " + decision,
                null
            );

            complaintRepository.save(complaint);

            // ✅ Record in audit log
            auditLogService.saveLog(
                    complaintId,
                    complaint.getVehicleId(),
                    "MANAGER_DECISION",
                    "MANAGER",
                    previousStatus,
                    complaint.getStatus(),
                    "Manager decision applied via workflow: " + decision,
                    Map.of("decision", decision != null ? decision : "")
            );

            System.out.println("Manager decision applied: " + decision);

        } else {
            System.out.println("Warning: Complaint not found for ID: " + complaintId);
        }
    }
}