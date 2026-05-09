package com.evfleetmobility.complaintresolution.aiservices.controller;

import com.evfleetmobility.complaintresolution.aiservices.dto.AIQuerySaveRequestDTO;
import com.evfleetmobility.complaintresolution.aiservices.dto.AIResponseSaveRequestDTO;
import com.evfleetmobility.complaintresolution.aiservices.entity.AIQuery;
import com.evfleetmobility.complaintresolution.aiservices.entity.AIResponse;
import com.evfleetmobility.complaintresolution.aiservices.repository.AIQueryRepository;
import com.evfleetmobility.complaintresolution.aiservices.repository.AIResponseRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AIGatewayController {

    private final AIQueryRepository aiQueryRepository;
    private final AIResponseRepository aiResponseRepository;

    public AIGatewayController(
            AIQueryRepository aiQueryRepository,
            AIResponseRepository aiResponseRepository
    ) {
        this.aiQueryRepository = aiQueryRepository;
        this.aiResponseRepository = aiResponseRepository;
    }

    @PostMapping("/queries")
    public ResponseEntity<AIQuery> saveQuery(@RequestBody AIQuerySaveRequestDTO request) {
        AIQuery query = new AIQuery();
        query.setUserId(request.getUserId());
        query.setVehicleId(request.getVehicleId());
        query.setVehicleModel(request.getVehicleModel());
        query.setQuestion(request.getQuestion());

        AIQuery saved = aiQueryRepository.save(query);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/responses")
    public ResponseEntity<AIResponse> saveResponse(@RequestBody AIResponseSaveRequestDTO request) {
        AIResponse response = new AIResponse();
        response.setQueryId(request.getQueryId());
        response.setUserId(request.getUserId());
        response.setVehicleId(request.getVehicleId());
        response.setIssueId(request.getIssueId());
        response.setAnswer(request.getAnswer());
        response.setConfidence(request.getConfidence());
        response.setStatus(request.getStatus());
        response.setTitle(request.getTitle());
        response.setDescription(request.getDescription());

        AIResponse saved = aiResponseRepository.save(response);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/queries/user/{userId}")
    public ResponseEntity<List<AIQuery>> getQueriesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(aiQueryRepository.findByUserIdOrderByCreatedAtDesc(userId));
    }

    @GetMapping("/responses/query/{queryId}")
    public ResponseEntity<List<AIResponse>> getResponsesByQuery(@PathVariable Long queryId) {
        return ResponseEntity.ok(aiResponseRepository.findByQueryIdOrderByCreatedAtDesc(queryId));
    }
}
