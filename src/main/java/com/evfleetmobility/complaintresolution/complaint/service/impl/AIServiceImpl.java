package com.evfleetmobility.complaintresolution.complaint.service.impl;
import com.evfleetmobility.complaintresolution.auditlog.service.AuditLogService;


import com.evfleetmobility.complaintresolution.complaint.service.AIService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("aiService")
public class AIServiceImpl implements AIService, JavaDelegate {

    @Autowired
    private AuditLogService auditLogService;

    @Override
    public void execute(DelegateExecution execution) {

        System.out.println("AI Service running...");

        Long complaintId = (Long) execution.getVariable("complaintId");
        String vehicleId = (String) execution.getVariable("vehicleId");
        String issueCategory = (String) execution.getVariable("issueCategory");
        String issueDescription = (String) execution.getVariable("issueDescription");

        Integer aiAttemptCount = execution.getVariable("aiAttemptCount") != null
                ? (Integer) execution.getVariable("aiAttemptCount")
                : 0;

        aiAttemptCount = aiAttemptCount + 1;

        String suggestion = "Check battery health and wiring";
        double confidence = 0.85;

        String predictedCategory = issueCategory;
        if (issueDescription != null && issueDescription.toLowerCase().contains("battery")) {
            predictedCategory = "Battery";
        }

        execution.setVariable("aiAttemptCount", aiAttemptCount);
        execution.setVariable("aiSuggestion", suggestion);
        execution.setVariable("aiConfidence", confidence);
        execution.setVariable("predictedCategory", predictedCategory);

        System.out.println("AI Attempt Count: " + aiAttemptCount);
        System.out.println("AI Suggestion: " + suggestion);

        auditLogService.saveLog(
                complaintId,
                vehicleId,
                "AI_ANALYZED",
                "SYSTEM",
                null,
                null,
                "AI analyzed complaint and generated suggestion",
                Map.of(
                        "vehicleId", vehicleId,
                        "issueCategory", issueCategory,
                        "predictedCategory", predictedCategory,
                        "suggestion", suggestion,
                        "confidence", confidence,
                        "aiAttemptCount", aiAttemptCount
                )
        );

        if (aiAttemptCount >= 3) {
            auditLogService.saveLog(
                    complaintId,
                    vehicleId,
                    "AI_LIMIT_REACHED",
                    "SYSTEM",
                    null,
                    "VENDOR_ASSIGNMENT_REQUIRED",
                    "AI support limit reached, complaint will move to vendor assignment",
                    Map.of(
                            "vehicleId", vehicleId,
                            "aiAttemptCount", aiAttemptCount,
                            "message", "Let me assign a vendor to help you"
                    )
            );
        }

        if (issueCategory != null && !issueCategory.equalsIgnoreCase(predictedCategory)) {
            auditLogService.saveLog(
                    complaintId,
                    vehicleId,
                    "CATEGORY_PREDICTED",
                    "SYSTEM",
                    issueCategory,
                    predictedCategory,
                    "AI updated issue category",
                    Map.of(
                            "vehicleId", vehicleId,
                            "confidence", confidence,
                            "aiAttemptCount", aiAttemptCount
                    )
            );
        }
    }
}