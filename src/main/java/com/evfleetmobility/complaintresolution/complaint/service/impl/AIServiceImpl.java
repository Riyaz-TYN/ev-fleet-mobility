package com.evfleetmobility.complaintresolution.complaint.service.impl;

import com.evfleetmobility.complaintresolution.aiservices.dto.AIRequestDTO;
import com.evfleetmobility.complaintresolution.aiservices.dto.AIResponseDTO;
import com.evfleetmobility.complaintresolution.aiservices.dto.ServiceHistoryDTO;
import com.evfleetmobility.complaintresolution.aiservices.service.AIIntegrationService;
import com.evfleetmobility.complaintresolution.auditlog.service.AuditLogService;
import com.evfleetmobility.complaintresolution.complaint.service.AIService;
import com.evfleetmobility.useronboarding.vehicleservices.entity.ServiceHistory;
import com.evfleetmobility.useronboarding.vehicleservices.entity.Vehicle;
import com.evfleetmobility.useronboarding.vehicleservices.repository.ServiceHistoryRepository;
import com.evfleetmobility.useronboarding.vehicleservices.repository.VehicleRepository;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("aiService")
public class AIServiceImpl implements AIService, JavaDelegate {

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private AIIntegrationService aiIntegrationService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ServiceHistoryRepository serviceHistoryRepository;

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

        // ---- Build AI request with full context ----
        AIRequestDTO aiRequest = buildAIRequest(
                execution, complaintId, vehicleId, issueCategory, issueDescription, aiAttemptCount
        );

        // ---- Call external FastAPI AI service ----
        AIResponseDTO aiResponse = aiIntegrationService.callAI(aiRequest);

        String suggestion = aiResponse.getSuggestion();
        double confidence = aiResponse.getConfidence() != null ? aiResponse.getConfidence() : 0.0;
        String predictedCategory = aiResponse.getPredictedCategory() != null
                ? aiResponse.getPredictedCategory()
                : issueCategory;

        // ---- Set workflow variables (UNCHANGED from original) ----
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

    /**
     * Builds the AI request payload with full context:
     * - Complaint details from workflow variables
     * - Vehicle details from VehicleRepository
     * - Service history from ServiceHistoryRepository
     * - Previous AI suggestion for conversational retry context
     *
     * NOTE: SecurityContext is NOT available in Camunda delegates.
     *       All data comes from workflow execution variables.
     */
    private AIRequestDTO buildAIRequest(
            DelegateExecution execution,
            Long complaintId,
            String vehicleId,
            String issueCategory,
            String issueDescription,
            Integer aiAttemptCount
    ) {
        AIRequestDTO request = new AIRequestDTO();
        request.setComplaintId(complaintId);
        request.setTitle(issueCategory);
        request.setDescription(issueDescription);
        request.setIssueType(issueCategory);
        request.setVehicleId(vehicleId);
        request.setAiAttemptCount(aiAttemptCount);

        // Priority from workflow
        String priority = (String) execution.getVariable("priority");
        request.setPriority(priority != null ? priority : "LOW");

        // User ID from workflow
        String customerId = (String) execution.getVariable("customerId");
        if (customerId != null) {
            try {
                request.setUserId(Long.parseLong(customerId));
            } catch (NumberFormatException e) {
                System.out.println("Could not parse customerId to Long: " + customerId);
            }
        }

        // Previous suggestion for conversational retry
        if (aiAttemptCount > 1) {
            String previousSuggestion = (String) execution.getVariable("aiSuggestion");
            request.setPreviousSuggestion(previousSuggestion);
        }

        // ---- Auto-fetch vehicle context ----
        enrichWithVehicleContext(request, vehicleId);

        return request;
    }

    /**
     * Fetches vehicle details and service history from the database.
     * Gracefully handles missing/invalid vehicle IDs.
     */
    private void enrichWithVehicleContext(AIRequestDTO request, String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            request.setServiceHistory(new ArrayList<>());
            return;
        }

        try {
            Long vehicleIdLong = Long.parseLong(vehicleId);

            // Fetch vehicle
            vehicleRepository.findById(vehicleIdLong).ifPresent(vehicle -> {
                request.setVehicleModel(vehicle.getModel());
                request.setVehicleMake(vehicle.getMake());
                request.setYearOfManufacture(vehicle.getYearOfManufacture());
                request.setBatteryCapacityKwh(vehicle.getBatteryCapacityKwh());
            });

            // Fetch service history
            List<ServiceHistory> histories = serviceHistoryRepository.findByVehicleId(vehicleIdLong);
            List<ServiceHistoryDTO> historyDTOs = new ArrayList<>();
            for (ServiceHistory sh : histories) {
                ServiceHistoryDTO dto = new ServiceHistoryDTO(
                        sh.getServiceDate() != null ? sh.getServiceDate().toString() : null,
                        sh.getServiceType() != null ? sh.getServiceType().name() : null,
                        sh.getDescription(),
                        sh.getProviderName()
                );
                historyDTOs.add(dto);
            }
            request.setServiceHistory(historyDTOs);

        } catch (NumberFormatException e) {
            System.out.println("vehicleId is not numeric, skipping vehicle context: " + vehicleId);
            request.setServiceHistory(new ArrayList<>());
        }
    }
}