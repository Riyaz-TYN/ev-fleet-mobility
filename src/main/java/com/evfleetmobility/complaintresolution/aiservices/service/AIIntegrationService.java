package com.evfleetmobility.complaintresolution.aiservices.service;

import com.evfleetmobility.complaintresolution.aiservices.dto.AIRequestDTO;
import com.evfleetmobility.complaintresolution.aiservices.dto.AIResponseDTO;

public interface AIIntegrationService {

    AIResponseDTO callAI(AIRequestDTO request);
}
