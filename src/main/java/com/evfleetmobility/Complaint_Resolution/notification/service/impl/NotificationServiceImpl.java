package com.complaint_resolution.notification.service.impl;
import com.complaint_resolution.auditlog.service.AuditLogService;


import com.complaint_resolution.notification.service.NotificationService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("notificationService")
public class NotificationServiceImpl implements NotificationService, JavaDelegate {

    @Autowired
    private AuditLogService auditLogService;

    @Override
    public void execute(DelegateExecution execution) {

        Long complaintId = (Long) execution.getVariable("complaintId");

        String vehicleId = execution.getVariable("vehicleId") != null
                ? execution.getVariable("vehicleId").toString()
                : "UNKNOWN";

        String customerId = execution.getVariable("customerId") != null
                ? execution.getVariable("customerId").toString()
                : "UNKNOWN";

        String vendorName = execution.getVariable("vendorName") != null
                ? execution.getVariable("vendorName").toString()
                : "UNKNOWN";

        String vendorLocation = execution.getVariable("vendorLocation") != null
                ? execution.getVariable("vendorLocation").toString()
                : "UNKNOWN";

        String vendorExpertise = execution.getVariable("vendorExpertise") != null
                ? execution.getVariable("vendorExpertise").toString()
                : "UNKNOWN";

        String vendorRating = execution.getVariable("vendorRating") != null
                ? execution.getVariable("vendorRating").toString()
                : "0";

        System.out.println(" Notifying vendor: " + vendorName);

        Map<String, Object> vendorMetadata = new HashMap<>();
        vendorMetadata.put("vehicleId", vehicleId);
        vendorMetadata.put("vendorName", vendorName);
        vendorMetadata.put("vendorLocation", vendorLocation);
        vendorMetadata.put("vendorExpertise", vendorExpertise);
        vendorMetadata.put("vendorRating", vendorRating);

        auditLogService.saveLog(
                complaintId,
                vehicleId,
                "VENDOR_NOTIFIED",
                "SYSTEM",
                null,
                null,
                "Vendor notified about complaint",
                vendorMetadata
        );

        Map<String, Object> userMetadata = new HashMap<>();
        userMetadata.put("vehicleId", vehicleId);
        userMetadata.put("customerId", customerId);
        userMetadata.put("message", "A vendor has been assigned to help you.");
        userMetadata.put("vendorName", vendorName);
        userMetadata.put("vendorLocation", vendorLocation);
        userMetadata.put("vendorExpertise", vendorExpertise);
        userMetadata.put("vendorRating", vendorRating);

        auditLogService.saveLog(
                complaintId,
                vehicleId,
                "USER_NOTIFIED",
                "SYSTEM",
                null,
                null,
                "User notified with assigned vendor details",
                userMetadata
        );
    }
}