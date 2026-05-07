package com.complaint_resolution.escalation.service.impl;
import com.complaint_resolution.auditlog.service.AuditLogService;


import com.complaint_resolution.escalation.service.EscalationService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("escalationService")
public class EscalationServiceImpl implements EscalationService, JavaDelegate {

    @Autowired
    private AuditLogService auditLogService;

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
        execution.setVariable("status", "ESCALATED");

        System.out.println("Escalation reason: " + reason);

        auditLogService.saveLog(
                complaintId,
                vehicleId,
                "ESCALATED",
                "SYSTEM",
                currentStatus,
                "ESCALATED",
                "Complaint escalated to manager because vendor could not resolve the issue",
                Map.of(
                        "reason", reason,
                        "vehicleId", vehicleId,
                        "vendorName", vendorName
                )
        );
    }
}