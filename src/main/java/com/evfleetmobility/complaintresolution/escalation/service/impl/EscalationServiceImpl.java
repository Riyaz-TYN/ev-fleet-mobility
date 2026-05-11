package com.evfleetmobility.complaintresolution.escalation.service.impl;
import com.evfleetmobility.complaintresolution.auditlog.service.AuditLogService;
import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.complaint.repository.ComplaintRepository;
import com.evfleetmobility.complaintresolution.escalation.service.EscalationService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("escalationService")
public class EscalationServiceImpl implements EscalationService, JavaDelegate {

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Override
    public void execute(DelegateExecution execution) {

        System.out.println(" Escalation triggered");

        Long complaintId = (Long) execution.getVariable("complaintId");
        String vehicleId = (String) execution.getVariable("vehicleId");

        String vendorName = execution.getVariable("vendorName") != null
                ? execution.getVariable("vendorName").toString()
                : "UNKNOWN";

        String currentStatus = execution.getVariable("status") != null
                ? execution.getVariable("status").toString()
                : "ASSIGNED_TO_VENDOR";

        String reason = "VENDOR_UNRESOLVED";

        execution.setVariable("escalationReason", reason);
        execution.setVariable("status", "ESCALATED_TO_MANAGER");

        System.out.println("Escalation reason: " + reason);

        // ✅ Persist escalation status and reason to DB
        complaintRepository.findById(complaintId).ifPresent(complaint -> {
            complaint.setStatus("ESCALATED_TO_MANAGER");
            complaint.setEscalationReason("Vendor could not resolve the issue: " + vendorName);
            complaint.addWorkHistory(
                "Escalated to Manager",
                "Vendor: " + vendorName,
                "Vendor could not resolve — escalating to manager for review"
            );
            complaintRepository.save(complaint);
        });

        auditLogService.saveLog(
                complaintId,
                vehicleId,
                "ESCALATED_TO_MANAGER",
                "SYSTEM",
                currentStatus,
                "ESCALATED_TO_MANAGER",
                "Complaint escalated to manager because vendor " + vendorName + " could not resolve the issue",
                Map.of(
                        "reason", reason,
                        "vehicleId", vehicleId != null ? vehicleId : "",
                        "vendorName", vendorName
                )
        );
    }
}