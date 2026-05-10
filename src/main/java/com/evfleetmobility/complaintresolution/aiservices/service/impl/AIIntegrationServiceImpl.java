package com.evfleetmobility.complaintresolution.aiservices.service.impl;

import com.evfleetmobility.complaintresolution.aiservices.dto.AIRequestDTO;
import com.evfleetmobility.complaintresolution.aiservices.dto.AIResponseDTO;
import com.evfleetmobility.complaintresolution.aiservices.entity.AIQuery;
import com.evfleetmobility.complaintresolution.aiservices.entity.AIResponse;
import com.evfleetmobility.complaintresolution.aiservices.repository.AIQueryRepository;
import com.evfleetmobility.complaintresolution.aiservices.repository.AIResponseRepository;
import com.evfleetmobility.complaintresolution.aiservices.service.AIIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AIIntegrationServiceImpl implements AIIntegrationService {

    @Qualifier("aiWebClient")
    private final WebClient aiWebClient;

    private final AIQueryRepository queryRepository;
    private final AIResponseRepository responseRepository;

    @Value("${ai.service.timeout:5}")
    private int timeoutSeconds;

    @Override
    @Transactional
    public AIResponseDTO callAI(AIRequestDTO request) {
        // 1. Create and save AIQuery
        AIQuery query = new AIQuery();
        query.setUserId(request.getUserId());
        query.setVehicleId(request.getVehicleId());
        query.setVehicleModel(request.getVehicleModel());
        query.setQuestion(request.getUserFollowUp() != null ? request.getUserFollowUp() : request.getDescription());
        query = queryRepository.save(query);

        AIResponseDTO responseDTO;
        try {
            // 2. Call AI Service
            responseDTO = aiWebClient
                    .post()
                    .uri("/api/ai/analyze")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(AIResponseDTO.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();

            if (responseDTO == null) {
                responseDTO = buildFallbackResponse(request);
            }

        } catch (WebClientResponseException e) {
            responseDTO = buildFallbackResponse(request);
        } catch (Exception e) {
            responseDTO = buildFallbackResponse(request);
        }

        // 3. Create and save AIResponse
        AIResponse aiResponse = new AIResponse();
        aiResponse.setQueryId(query.getId());
        aiResponse.setUserId(request.getUserId());
        aiResponse.setVehicleId(request.getVehicleId());
        aiResponse.setIssueId(String.valueOf(request.getComplaintId()));
        aiResponse.setTitle(request.getTitle());
        aiResponse.setDescription(request.getDescription());
        aiResponse.setAnswer(responseDTO.getSuggestion());
        aiResponse.setConfidence(responseDTO.getConfidence());
        aiResponse.setStatus(responseDTO.getStatus() != null ? responseDTO.getStatus() : "PROCESSED");
        responseRepository.save(aiResponse);

        return responseDTO;
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
