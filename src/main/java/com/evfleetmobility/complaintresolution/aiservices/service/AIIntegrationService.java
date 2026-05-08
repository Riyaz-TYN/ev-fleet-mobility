package com.evfleetmobility.complaintresolution.aiservices.service;

import com.evfleetmobility.complaintresolution.aiservices.dto.AIRequestDTO;
import com.evfleetmobility.complaintresolution.aiservices.dto.AIResponseDTO;

public interface AIIntegrationService {

    /**
     * Sends complaint + vehicle + service history context to the external FastAPI AI service.
     * Returns AI suggestion, confidence, and predicted category.
     * On failure, returns fallback response so the workflow continues safely.
     */
    AIResponseDTO callAI(AIRequestDTO request);
}
