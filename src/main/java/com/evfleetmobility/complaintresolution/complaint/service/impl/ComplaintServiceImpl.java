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

            var vehicle = vehicleRepository
                    .findByUserId(userId)
                    .orElseThrow(() ->
                            new RuntimeException("No vehicle found for this user")
                    );

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
    public List<Complaint> getComplaints() {
        String role = authContextService.getCurrentRole();

        Long currentUserId = authContextService.getCurrentUserId();

        if ("DRIVER".equalsIgnoreCase(role)) {

            return complaintRepository.findByCustomerIdOrderByCreatedAtDesc(String.valueOf(currentUserId));
        }

        if ("VENDOR_ADMIN".equalsIgnoreCase(role) || "VENDOR".equalsIgnoreCase(role)) {
            return userRepository.findById(currentUserId)
                    .filter(u -> u.getOrganizationDetails() != null)
                    .map(u -> complaintRepository.findByVendorIdOrderByCreatedAtDesc(
                            u.getOrganizationDetails().getId()))
                    .orElse(java.util.List.of());
        }

        if ("MANAGER".equalsIgnoreCase(role)) {
            return complaintRepository.findByStatusOrderByCreatedAtDesc("ESCALATED_TO_MANAGER");
        }

        return complaintRepository.findAll();
    }

    @Override
    public Complaint getComplaintDetails(Long complaintId) {
        return complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found with ID: " + complaintId));
    }

    @Override
    public List<Complaint> getComplaintsByVehicle(String vehicleId) {
        return complaintRepository.findByVehicleIdOrderByCreatedAtDesc(vehicleId);
    }

    @Override
    public List<Complaint> getComplaintStatus(String status) {
        return complaintRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    @Override
    public List<Complaint> getAssignedComplaintsByVendorId(Long vendorId) {
        return vendorService.getAssignedComplaints(vendorId);
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
    public List<Complaint> getMyComplaints(String customerId) {
        return complaintRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    @Override
    public List<Complaint> getComplaintsByVehicleId(String vehicleId) {
        return complaintRepository.findByVehicleIdOrderByCreatedAtDesc(vehicleId);
    }

    @Override
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    @Override
    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id).orElse(null);
    }

    @Override
    public List<Complaint> getComplaintsByStatus(String status) {
        return complaintRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    @Override
    public List<Complaint> getComplaintsByPriority(String priority) {
        return complaintRepository.findByPriorityOrderByCreatedAtDesc(priority);
    }

    @Override
    public Complaint reassignVendor(Long complaintId, Long vendorId) {
        return managerDashboardService.reassignVendor(complaintId, vendorId);
    }

    @Override
    public List<com.evfleetmobility.complaintresolution.vendor.dto.VendorDTO> getNearbyVendors(Long complaintId) {
        return managerDashboardService.getNearbyVendorsForComplaint(complaintId);
    }
}
