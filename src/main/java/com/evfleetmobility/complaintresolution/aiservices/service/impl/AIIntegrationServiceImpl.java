package com.evfleetmobility.complaintresolution.aiservices.service.impl;

import com.evfleetmobility.complaintresolution.aiservices.dto.AIRequestDTO;
import com.evfleetmobility.complaintresolution.aiservices.dto.AIResponseDTO;
import com.evfleetmobility.complaintresolution.aiservices.service.AIIntegrationService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

@Service
public class AIIntegrationServiceImpl implements AIIntegrationService {

    private final WebClient aiWebClient;
    private final int timeoutSeconds;

    public AIIntegrationServiceImpl(
            @Qualifier("aiWebClient") WebClient aiWebClient,
            @Value("${ai.service.timeout:30}") int timeoutSeconds
    ) {
        this.aiWebClient = aiWebClient;
        this.timeoutSeconds = timeoutSeconds;
    }

    @Override
    public AIResponseDTO callAI(AIRequestDTO request) {
        try {
            System.out.println("Calling external AI service for complaint: " + request.getComplaintId()
                    + " | attempt: " + request.getAiAttemptCount());

            System.out.println("===== AI PAYLOAD =====");

            System.out.println("Complaint ID: " + request.getComplaintId());
            System.out.println("Title: " + request.getTitle());
            System.out.println("Description: " + request.getDescription());
            System.out.println("Issue Type: " + request.getIssueType());
            System.out.println("Priority: " + request.getPriority());

            System.out.println("Vehicle ID: " + request.getVehicleId());
            System.out.println("Vehicle Model: " + request.getVehicleModel());
            System.out.println("Vehicle Make: " + request.getVehicleMake());

            System.out.println("Year: " + request.getYearOfManufacture());
            System.out.println("Battery Capacity: " + request.getBatteryCapacityKwh());

            System.out.println("AI Attempt Count: " + request.getAiAttemptCount());

            System.out.println("User ID: " + request.getUserId());

            System.out.println("Service History: " + request.getServiceHistory());

            AIResponseDTO response = aiWebClient
                    .post()
                    .uri("/api/ai/analyze")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(AIResponseDTO.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();

            if (response != null) {
                System.out.println("AI response received â€” suggestion: " + response.getSuggestion()
                        + " | confidence: " + response.getConfidence());
                return response;
            }

            System.out.println("AI service returned null response, using fallback");
            return buildFallbackResponse(request);

        } catch (WebClientResponseException e) {
            System.out.println("AI service HTTP error: " + e.getStatusCode() + " â€” " + e.getMessage());
            return buildFallbackResponse(request);

        } catch (Exception e) {
            System.out.println("AI service call failed: " + e.getMessage());
            return buildFallbackResponse(request);
        }
    }

    private AIResponseDTO buildFallbackResponse(AIRequestDTO request) {
        AIResponseDTO fallback = new AIResponseDTO();
        fallback.setSuggestion("AI service is currently unavailable. Please try again or escalate to support.");
        fallback.setConfidence(0.0);
        fallback.setPredictedCategory(request.getIssueType() != null ? request.getIssueType() : "UNKNOWN");
        fallback.setStatus("FALLBACK");
        return fallback;
    }
}
