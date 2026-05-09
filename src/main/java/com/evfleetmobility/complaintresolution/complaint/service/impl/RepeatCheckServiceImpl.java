package com.evfleetmobility.complaintresolution.complaint.service.impl;
import com.evfleetmobility.complaintresolution.auditlog.service.AuditLogService;

import com.evfleetmobility.complaintresolution.complaint.service.RepeatCheckService;
import com.evfleetmobility.complaintresolution.complaint.repository.ComplaintRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("repeatCheckService")
public class RepeatCheckServiceImpl implements RepeatCheckService, JavaDelegate {

    @Autowired
    private ComplaintRepository repository;

    @Autowired
    private AuditLogService auditLogService;

    @Override
    public void execute(DelegateExecution execution) {

        System.out.println(" Repeat Check running...");

        String customerId = (String) execution.getVariable("customerId");
        String issueCategory = (String) execution.getVariable("issueCategory");
        Long complaintId = (Long) execution.getVariable("complaintId");
        String vehicleId = (String) execution.getVariable("vehicleId"); 

        int count = repository
                .findByCustomerIdAndIssueCategory(customerId, issueCategory)
                .size();

        boolean isRepeated = count > 0;

        String priority = "LOW";
        if (count > 3) {
            priority = "CRITICAL";
        } else if (count > 1) {
            priority = "HIGH";
        }

        execution.setVariable("isRepeated", isRepeated);
        execution.setVariable("repeatCount", count);
        execution.setVariable("priority", priority);

        System.out.println("Repeat count: " + count + " | Priority: " + priority);

        auditLogService.saveLog(
                complaintId,
                vehicleId, 
                "REPEAT_CHECKED",
                "SYSTEM",
                null,
                null,
                "Repeat check executed",
                Map.of(
                        "customerId", customerId,
                        "vehicleId", vehicleId,
                        "issueCategory", issueCategory,
                        "repeatCount", count,
                        "isRepeated", isRepeated,
                        "calculatedPriority", priority
                )
        );

        if (isRepeated) {
            auditLogService.saveLog(
                    complaintId,
                    vehicleId, 
                    "REPEAT_DETECTED",
                    "SYSTEM",
                    null,
                    null,
                    "Repeated complaint detected",
                    Map.of(
                            "vehicleId", vehicleId,
                            "repeatCount", count,
                            "priority", priority
                    )
            );
        }

        if (!"LOW".equals(priority)) {
            auditLogService.saveLog(
                    complaintId,
                    vehicleId, 
                    "PRIORITY_UPDATED",
                    "SYSTEM",
                    "LOW",
                    priority,
                    "Priority increased due to repeat complaints",
                    Map.of(
                            "vehicleId", vehicleId,
                            "repeatCount", count
                    )
            );
        }
    }
}