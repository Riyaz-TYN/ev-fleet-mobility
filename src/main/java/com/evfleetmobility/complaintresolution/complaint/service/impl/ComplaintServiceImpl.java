package com.evfleetmobility.complaintresolution.complaint.service.impl;

import com.evfleetmobility.complaintresolution.auditlog.service.AuditLogService;
import com.evfleetmobility.common.security.AuthContextService;
import com.evfleetmobility.complaintresolution.complaint.dto.ComplaintActionRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.dto.ComplaintRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.complaint.repository.ComplaintRepository;
import com.evfleetmobility.complaintresolution.complaint.service.ComplaintService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ObjectMapper objectMapper;
    private final AuditLogService auditLogService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private AuthContextService authContextService;

    public ComplaintServiceImpl(
            ComplaintRepository complaintRepository,
            ObjectMapper objectMapper,
            AuditLogService auditLogService
    ) {
        this.complaintRepository = complaintRepository;
        this.objectMapper = objectMapper;
        this.auditLogService = auditLogService;
    }

    // SAVE COMPLAINT
    @Override
    public String saveComplaint(
            ComplaintRequestDTO request,
            String customerId
    ) {

        try {

            if (request.getComplaintData() == null) {
                return "Complaint data is missing";
            }

            Map<String, Object> data =
                    request.getComplaintData();

            String issueCategory =
                    data.get("issueCategory") != null
                            ? data.get("issueCategory").toString()
                            : "UNKNOWN";

            String issueDescription =
                    data.get("issueDescription") != null
                            ? data.get("issueDescription").toString()
                            : "";

            String location =
                    data.get("location") != null
                            ? data.get("location").toString()
                            : "UNKNOWN";

            String vehicleId =
                    data.get("vehicleId") != null
                            ? data.get("vehicleId").toString()
                            : "";

            String jsonData =
                    objectMapper.writeValueAsString(data);

            Complaint complaint = new Complaint();

            complaint.setIssueCategory(issueCategory);
            complaint.setData(jsonData);
            complaint.setCustomerId(customerId);
            complaint.setVehicleId(vehicleId);

            Complaint savedComplaint =
                    complaintRepository.save(complaint);

            auditLogService.saveLog(
                    savedComplaint.getId(),
                    vehicleId,
                    "CREATED",
                    "USER",
                    null,
                    "OPEN",
                    "Complaint created by user",
                    Map.of(
                            "customerId", customerId,
                            "issueCategory", issueCategory,
                            "issueDescription", issueDescription,
                            "location", location,
                            "vehicleId", vehicleId
                    )
            );

            Map<String, Object> variables =
                    new HashMap<>();

            variables.put(
                    "complaintId",
                    savedComplaint.getId()
            );

            variables.put(
                    "customerId",
                    customerId
            );

            variables.put(
                    "issueDescription",
                    issueDescription
            );

            variables.put(
                    "issueCategory",
                    issueCategory
            );

            variables.put(
                    "location",
                    location
            );

            variables.put(
                    "vehicleId",
                    vehicleId
            );

            variables.put("resolved", false);
            variables.put("continueAi", false);
            variables.put("aiAttemptCount", 0);
            variables.put("vendorResolved", false);
            variables.put("managerDecision", "RETRY");
            variables.put("status", "IN_PROGRESS");
            variables.put("priority", "LOW");

            runtimeService.startProcessInstanceByKey(
                    "complaintWorkflow",
                    variables
            );

            auditLogService.saveLog(
                    savedComplaint.getId(),
                    vehicleId,
                    "WORKFLOW_STARTED",
                    "SYSTEM",
                    "OPEN",
                    "IN_PROGRESS",
                    "Camunda complaint workflow started",
                    Map.of(
                            "processKey", "complaintWorkflow",
                            "priority", "LOW",
                            "vehicleId", vehicleId
                    )
            );

            return "Complaint saved & workflow started";

        } catch (Exception e) {

            e.printStackTrace();

            return "Error: " + e.getMessage();
        }
    }

    // USER COMPLAINTS
    @Override
    public List<Complaint> getMyComplaints(
            String customerId
    ) {

        return complaintRepository
                .findByCustomerIdOrderByCreatedAtDesc(
                        customerId
                );
    }

    // VEHICLE FILTER
    @Override
    public List<Complaint> getComplaintsByVehicleId(
            String vehicleId
    ) {

        return complaintRepository
                .findByVehicleIdOrderByCreatedAtDesc(
                        vehicleId
                );
    }

    // ALL COMPLAINTS
    @Override
    public List<Complaint> getAllComplaints() {

        return complaintRepository.findAll();
    }

    // GET COMPLAINT BY ID
    @Override
    public Complaint getComplaintById(Long id) {

        return complaintRepository.findById(id)
                .orElse(null);
    }

    // STATUS FILTER
    @Override
    public List<Complaint> getComplaintsByStatus(
            String status
    ) {

        return complaintRepository
                .findByStatusOrderByCreatedAtDesc(
                        status
                );
    }

    // PRIORITY FILTER
    @Override
    public List<Complaint> getComplaintsByPriority(
            String priority
    ) {

        return complaintRepository
                .findByPriorityOrderByCreatedAtDesc(
                        priority
                );
    }

    // USER AI DECISION
    @Override
    public String userAiDecision(
            Long complaintId,
            Boolean resolved,
            Boolean continueAi
    ) {

        Task task = taskService.createTaskQuery()
                .processVariableValueEquals(
                        "complaintId",
                        complaintId
                )
                .taskDefinitionKey("userTask")
                .singleResult();

        if (task == null) {
            return "User task not found";
        }

        taskService.complete(
                task.getId(),
                Map.of(
                        "resolved", resolved,
                        "continueAi", continueAi
                )
        );

        Complaint complaint =
                complaintRepository.findById(
                        complaintId
                ).orElseThrow();

        String newStatus;

        if (Boolean.TRUE.equals(resolved)) {

            newStatus = "RESOLVED_BY_AI";

        } else if (Boolean.TRUE.equals(continueAi)) {

            newStatus = "AI_RETRY";

        } else {

            newStatus = "ASSIGNED_TO_VENDOR";
        }

        complaint.setStatus(newStatus);

        complaintRepository.save(complaint);

        auditLogService.saveLog(
                complaintId,
                complaint.getVehicleId(),
                "USER_AI_DECISION",
                "USER",
                null,
                newStatus,
                "User completed AI review",
                Map.of(
                        "resolved", resolved,
                        "continueAi", continueAi
                )
        );

        return "User decision updated successfully";
    }

    // RBAC COMPLAINT FETCHING
    @Override
    public List<Complaint> getComplaints() {

        String role =
                authContextService.getCurrentRole();

        String subject =
                authContextService.getCurrentSubject();

        if ("USER".equalsIgnoreCase(role)) {

            return complaintRepository
                    .findByCustomerIdOrderByCreatedAtDesc(
                            subject
                    );
        }

        if ("VENDOR_ADMIN".equalsIgnoreCase(role)) {

            return complaintRepository
                    .findByAssignedTeamOrderByCreatedAtDesc(
                            subject
                    );
        }

        if ("MANAGER".equalsIgnoreCase(role)) {

            return complaintRepository
                    .findByStatusOrderByCreatedAtDesc(
                            "ESCALATED_TO_MANAGER"
                    );
        }

        return complaintRepository.findAll();
    }

    // COMPLAINT DETAILS
    @Override
    public Complaint getComplaintDetails(
            Long complaintId
    ) {

        return complaintRepository.findById(
                complaintId
        ).orElseThrow(() ->
                new RuntimeException(
                        "Complaint not found"
                ));
    }

    // UNIFIED VEHICLE FILTER
    @Override
    public List<Complaint> getComplaintsByVehicle(
            String vehicleId
    ) {

        return complaintRepository
                .findByVehicleIdOrderByCreatedAtDesc(
                        vehicleId
                );
    }

    // UNIFIED STATUS FILTER
    @Override
    public List<Complaint> getComplaintStatus(
            String status
    ) {

        return complaintRepository
                .findByStatusOrderByCreatedAtDesc(
                        status
                );
    }

    // CENTRALIZED ACTION HANDLER
    @Override
    public Object handleComplaintAction(
            ComplaintActionRequestDTO request
    ) {

        String role =
                authContextService.getCurrentRole();

        Complaint complaint =
                complaintRepository.findById(
                        request.getComplaintId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Complaint not found"
                        ));

        switch (request.getAction()) {

            case "USER_AI_DECISION":

                if (!"USER".equalsIgnoreCase(role)) {

                    throw new RuntimeException(
                            "Unauthorized"
                    );
                }

                return userAiDecision(
                        request.getComplaintId(),
                        request.getResolved(),
                        request.getContinueAi()
                );

            case "VENDOR_STATUS_UPDATE":

                if (!"VENDOR_ADMIN".equalsIgnoreCase(role)) {

                    throw new RuntimeException(
                            "Unauthorized"
                    );
                }

                complaint.setStatus(
                        request.getStatus()
                );

                complaintRepository.save(complaint);

                return "Vendor status updated";

            case "VENDOR_RESOLVE":

                if (!"VENDOR_ADMIN".equalsIgnoreCase(role)) {

                    throw new RuntimeException(
                            "Unauthorized"
                    );
                }

                if (Boolean.TRUE.equals(
                        request.getResolved()
                )) {

                    complaint.setStatus("RESOLVED");

                } else {

                    complaint.setStatus(
                            "ESCALATED_TO_MANAGER"
                    );
                }

                complaintRepository.save(complaint);

                return "Vendor action completed";

            case "MANAGER_DECISION":

                if (!"MANAGER".equalsIgnoreCase(role)) {

                    throw new RuntimeException(
                            "Unauthorized"
                    );
                }

                complaint.setStatus(
                        request.getManagerDecision()
                );

                complaintRepository.save(complaint);

                return "Manager decision completed";

            default:

                throw new RuntimeException(
                        "Invalid action"
                );
        }
    }
}