package com.evfleetmobility.complaintresolution.complaint.service.impl;

import com.evfleetmobility.complaintresolution.auditlog.service.AuditLogService;
import com.evfleetmobility.common.security.AuthContextService;
import com.evfleetmobility.complaintresolution.complaint.dto.ComplaintRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.complaint.repository.ComplaintRepository;
import com.evfleetmobility.complaintresolution.complaint.service.ComplaintService;
import com.evfleetmobility.complaintresolution.vendor.service.VendorService;
import com.evfleetmobility.complaintresolution.manager.service.ManagerDashboardService;
import com.evfleetmobility.complaintresolution.manager.service.ManagerService;
import com.evfleetmobility.useronboarding.profileservices.entity.OrganizationDetails;
import com.evfleetmobility.useronboarding.profileservices.repository.OrganizationRepository;
import com.evfleetmobility.useronboarding.vehicleservices.repository.VehicleRepository;
import com.evfleetmobility.useronboarding.authservices.repository.UserRepository;
import com.evfleetmobility.useronboarding.authservices.entity.UserType;
import com.evfleetmobility.useronboarding.authservices.entity.ApprovalStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.camunda.bpm.engine.RuntimeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ObjectMapper objectMapper;
    private final AuditLogService auditLogService;

    private final VendorService vendorService;

    private final OrganizationRepository organizationRepo;

    private final ManagerService managerService;
    private final ManagerDashboardService managerDashboardService;

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    @Autowired
    private RuntimeService runtimeService;


    @Autowired
    private AuthContextService authContextService;

    public ComplaintServiceImpl(
            ComplaintRepository complaintRepository,
            ObjectMapper objectMapper,
            AuditLogService auditLogService,
            VendorService vendorService,
            OrganizationRepository organizationRepo,
            ManagerService managerService,
            ManagerDashboardService managerDashboardService,
            VehicleRepository vehicleRepository,
            UserRepository userRepository
    ) {
        this.complaintRepository = complaintRepository;
        this.objectMapper = objectMapper;
        this.auditLogService = auditLogService;
        this.vendorService = vendorService;
        this.organizationRepo = organizationRepo;
        this.managerService = managerService;
        this.managerDashboardService = managerDashboardService;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public String saveComplaint(ComplaintRequestDTO request, String callerUserIdStr) {
        try {
            if (request.getComplaintData() == null) {
                return "Complaint data is missing";
            }

            Map<String, Object> data = request.getComplaintData();

            String issueCategory = data.get("issueCategory") != null
                    ? data.get("issueCategory").toString()
                    : "UNKNOWN";

            String issueDescription = data.get("issueDescription") != null
                    ? data.get("issueDescription").toString()
                    : "";

            String location = data.get("location") != null
                    ? data.get("location").toString()
                    : "UNKNOWN";

            Long userId = Long.parseLong(callerUserIdStr);

            if (request.getLatitude() != null && request.getLongitude() != null) {
                userRepository.findById(userId).ifPresent(user -> {
                    if (user.getUserType() == UserType.INDIVIDUAL && user.getIndividualDetails() != null) {
                        user.getIndividualDetails().setLatitude(request.getLatitude());
                        user.getIndividualDetails().setLongitude(request.getLongitude());
                        userRepository.save(user);
                    } else if (user.getUserType() == UserType.ORGANIZATION && user.getOrganizationDetails() != null) {
                        user.getOrganizationDetails().setLatitude(request.getLatitude());
                        user.getOrganizationDetails().setLongitude(request.getLongitude());
                        userRepository.save(user);
                    }
                });
            }

            var vehicles = vehicleRepository.findByUserId(userId);
            if (vehicles.isEmpty()) {
                throw new RuntimeException("No vehicle found for this user");
            }
            var vehicle = vehicles.get(0);

            String vehicleId = vehicle.getId().toString();

            String jsonData = objectMapper.writeValueAsString(data);

            Complaint complaint = new Complaint();
            complaint.setIssueCategory(issueCategory);
            complaint.setData(jsonData);
            complaint.setCustomerId(callerUserIdStr);  
            complaint.setVehicleId(vehicleId);
            complaint.setLatitude(request.getLatitude());
            complaint.setLongitude(request.getLongitude());
            
            complaint.addWorkHistory("Complaint Raised", "Driver (ID: " + callerUserIdStr + ")", issueDescription);

            Complaint savedComplaint = complaintRepository.save(complaint);

            auditLogService.saveLog(
                    savedComplaint.getId(),
                    vehicleId,
                    "CREATED",
                    "USER",
                    null,
                    "OPEN",
                    "Complaint created by user",
                    Map.of(
                            "userId", callerUserIdStr,
                            "issueCategory", issueCategory,
                            "issueDescription", issueDescription,
                            "location", location,
                            "vehicleId", vehicleId
                    )
            );

            Map<String, Object> variables = new HashMap<>();
            variables.put("complaintId", savedComplaint.getId());
            variables.put("customerId", callerUserIdStr);
            variables.put("issueDescription", issueDescription);
            variables.put("issueCategory", issueCategory);
            variables.put("location", location);
            variables.put("vehicleId", vehicleId);
            variables.put("resolved", false);
            variables.put("continueAi", false);
            variables.put("aiAttemptCount", 0);
            variables.put("vendorResolved", false);
            variables.put("managerDecision", "RETRY");
            variables.put("status", "IN_PROGRESS");
            variables.put("priority", "LOW");

            if (request.getLatitude() != null) {
                variables.put("complaintLatitude", request.getLatitude());
            }
            if (request.getLongitude() != null) {
                variables.put("complaintLongitude", request.getLongitude());
            }

            runtimeService.startProcessInstanceByKey("complaintWorkflow", variables);

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

    @Override
    public Page<Complaint> getComplaints(int page, int size) {
        String role = authContextService.getCurrentRole();
        Long currentUserId = authContextService.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);

        if ("DRIVER".equalsIgnoreCase(role)) {
            return complaintRepository.findByCustomerIdOrderByCreatedAtDesc(String.valueOf(currentUserId), pageable);
        }

        if ("VENDOR_ADMIN".equalsIgnoreCase(role) || "VENDOR".equalsIgnoreCase(role)) {
            return userRepository.findById(currentUserId)
                    .filter(u -> u.getOrganizationDetails() != null)
                    .map(u -> complaintRepository.findByVendorIdOrderByCreatedAtDesc(
                            u.getOrganizationDetails().getId(), pageable))
                    .orElse(Page.empty(pageable));
        }

        if ("MANAGER".equalsIgnoreCase(role)) {
            return complaintRepository.findByStatusOrderByCreatedAtDesc("ESCALATED_TO_MANAGER", pageable);
        }

        return complaintRepository.findAll(pageable);
    }

    @Override
    public Complaint getComplaintDetails(Long complaintId) {
        return complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found with ID: " + complaintId));
    }

    @Override
    public Page<Complaint> getComplaintsByVehicle(String vehicleId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return complaintRepository.findByVehicleIdOrderByCreatedAtDesc(vehicleId, pageable);
    }

    @Override
    public Page<Complaint> getComplaintStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return complaintRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
    }

    @Override
    public List<Complaint> getAssignedComplaintsByVendorId(Long vendorId) {
        return vendorService.getAssignedComplaints(vendorId, 0, Integer.MAX_VALUE).getContent();
    }

    @Override
    public String updateComplaintStatus(Long complaintId, String status) {
        return vendorService.updateComplaintStatus(complaintId, status);
    }

    @Override
    public String resolveComplaint(Long complaintId, Boolean resolved, String remarks) {
        return vendorService.resolveComplaint(complaintId, resolved, remarks);
    }

    @Override
    public Complaint approveAndAssignComplaint(Long complaintId, Long vendorId) {
        Complaint complaint = managerDashboardService.approveAndAssignComplaint(complaintId, vendorId);
        
        OrganizationDetails vendor = organizationRepo.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        
        complaint.addWorkHistory("Vendor Assigned", vendor.getCompanyName() + " (ID: " + vendorId + ")", null);
        
        return complaintRepository.save(complaint);
    }

    @Override
    public Complaint rejectComplaint(Long complaintId) {
        return managerDashboardService.rejectComplaint(complaintId);
    }

    @Override
    public String managerDecision(Long complaintId, String decision, String remarks) {
        return managerService.managerDecision(complaintId, decision, remarks);
    }

    @Override
    public List<OrganizationDetails> getAvailableVendors() {
        return organizationRepo.findByApprovalStatusAndVendorAvailabilityTrue(ApprovalStatus.APPROVED);
    }

    @Override
    public OrganizationDetails getVendorById(Long vendorId) {
        return organizationRepo.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found with ID: " + vendorId));
    }

    @Override
    public Page<Complaint> getMyComplaints(String customerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return complaintRepository.findByCustomerIdOrderByCreatedAtDesc(customerId, pageable);
    }

    @Override
    public Page<Complaint> getComplaintsByVehicleId(String vehicleId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return complaintRepository.findByVehicleIdOrderByCreatedAtDesc(vehicleId, pageable);
    }

    @Override
    public Page<Complaint> getAllComplaints(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return complaintRepository.findAll(pageable);
    }

    @Override
    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Complaint> getComplaintsByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return complaintRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
    }

    @Override
    public Page<Complaint> getComplaintsByPriority(String priority, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return complaintRepository.findByPriorityOrderByCreatedAtDesc(priority, pageable);
    }

    @Override
    public Complaint reassignVendor(Long complaintId, Long vendorId) {
        return managerDashboardService.reassignVendor(complaintId, vendorId);
    }

    @Override
    public List<com.evfleetmobility.complaintresolution.vendor.dto.VendorDTO> getNearbyVendors(Long complaintId) {
        return managerDashboardService.getNearbyVendorsForComplaint(complaintId);
    }

    @Override
    public Complaint assignTechnician(Long complaintId, Long technicianId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        String role = authContextService.getCurrentRole();
        Long callerId = authContextService.getCurrentUserId();

        if ("VENDOR_ADMIN".equalsIgnoreCase(role)) {
            com.evfleetmobility.useronboarding.authservices.entity.User vendorUser = userRepository.findById(callerId)
                    .orElseThrow(() -> new RuntimeException("Vendor user not found"));

            if (vendorUser.getOrganizationDetails() == null) {
                throw new RuntimeException("Vendor admin is not linked to an organization");
            }

            Long vendorOrgId = vendorUser.getOrganizationDetails().getId();

            if (!vendorOrgId.equals(complaint.getVendorId())) {
                throw new RuntimeException("You cannot assign a technician to a complaint not assigned to your organization");
            }

            com.evfleetmobility.useronboarding.authservices.entity.User techUser = userRepository.findById(technicianId)
                    .orElseThrow(() -> new RuntimeException("Technician not found"));

            if (techUser.getIndividualDetails() == null || techUser.getIndividualDetails().getOrganizationDetails() == null ||
                !techUser.getIndividualDetails().getOrganizationDetails().getId().equals(vendorOrgId)) {
                throw new RuntimeException("Technician does not belong to your organization");
            }

            if (techUser.getApprovalStatus() != ApprovalStatus.APPROVED) {
                throw new RuntimeException("Technician is not yet approved by the organization");
            }
        }

        userRepository.findById(technicianId).ifPresent(tech -> {
            complaint.setTechnicianId(technicianId);
            complaint.addWorkHistory("Technician Assigned", tech.getFullName() + " (ID: " + technicianId + ")", null);
        });

        return complaintRepository.save(complaint);
    }

    @Override
    public List<com.evfleetmobility.complaintresolution.complaint.dto.AIReplyDTO> getAIChatHistory(Long complaintId) {
        List<com.evfleetmobility.complaintresolution.auditlog.entity.AuditLog> logs = auditLogService.getLogsByComplaintId(complaintId);
        List<com.evfleetmobility.complaintresolution.complaint.dto.AIReplyDTO> chatHistory = new java.util.ArrayList<>();
        
        for (com.evfleetmobility.complaintresolution.auditlog.entity.AuditLog log : logs) {
            try {
                String timestamp = log.getCreatedAt() != null ? log.getCreatedAt().toString() : "";

                if ("USER_FOLLOWUP".equals(log.getAction())) {
                    Map<String, Object> metadata = objectMapper.readValue(log.getMetadata(), new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
                    String message = metadata.containsKey("userMessage") ? metadata.get("userMessage").toString() : log.getRemarks();
                    Integer attemptCount = metadata.containsKey("aiAttemptCount") ? Integer.valueOf(metadata.get("aiAttemptCount").toString()) : null;
                    
                    chatHistory.add(new com.evfleetmobility.complaintresolution.complaint.dto.AIReplyDTO("USER", message, null, timestamp, attemptCount));
                } 
                else if ("AI_ANALYZED".equals(log.getAction())) {
                    Map<String, Object> metadata = objectMapper.readValue(log.getMetadata(), new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
                    String message = metadata.containsKey("suggestion") ? metadata.get("suggestion").toString() : "";
                    Double confidence = metadata.containsKey("confidence") ? Double.valueOf(metadata.get("confidence").toString()) : 0.0;
                    Integer attemptCount = metadata.containsKey("aiAttemptCount") ? Integer.valueOf(metadata.get("aiAttemptCount").toString()) : 0;
                    
                    chatHistory.add(new com.evfleetmobility.complaintresolution.complaint.dto.AIReplyDTO("AI", message, confidence, timestamp, attemptCount));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return chatHistory;
    }
}
