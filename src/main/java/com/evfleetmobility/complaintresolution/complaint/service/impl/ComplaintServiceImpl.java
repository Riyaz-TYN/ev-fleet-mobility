package com.evfleetmobility.complaintresolution.complaint.service.impl;

import com.evfleetmobility.complaintresolution.auditlog.service.AuditLogService;
import com.evfleetmobility.common.security.AuthContextService;
import com.evfleetmobility.complaintresolution.complaint.dto.ComplaintRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.complaint.repository.ComplaintRepository;
import com.evfleetmobility.complaintresolution.complaint.service.ComplaintService;
import com.evfleetmobility.complaintresolution.complaintservices.vendor.entity.Vendor;
import com.evfleetmobility.complaintresolution.complaintservices.vendor.repository.VendorRepository;
import com.evfleetmobility.complaintresolution.complaintservices.vendor.service.VendorService;
import com.evfleetmobility.complaintresolution.manager.service.ManagerDashboardService;
import com.evfleetmobility.complaintresolution.manager.service.ManagerService;
import com.evfleetmobility.useronboarding.vehicleservices.repository.VehicleRepository;
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

    // Vendor service — handles vendor-related complaint operations
    private final VendorService vendorService;

    // Vendor repository — used by manager to look up available vendors for assignment
    private final VendorRepository vendorRepository;

    // Manager services — decision-making and dashboard operations
    private final ManagerService managerService;
    private final ManagerDashboardService managerDashboardService;

    // Vehicle repository from useronboarding — used to validate vehicle existence on complaint creation
    private final VehicleRepository vehicleRepository;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private AuthContextService authContextService;

    public ComplaintServiceImpl(
            ComplaintRepository complaintRepository,
            ObjectMapper objectMapper,
            AuditLogService auditLogService,
            VendorService vendorService,
            VendorRepository vendorRepository,
            ManagerService managerService,
            ManagerDashboardService managerDashboardService,
            VehicleRepository vehicleRepository
    ) {
        this.complaintRepository = complaintRepository;
        this.objectMapper = objectMapper;
        this.auditLogService = auditLogService;
        this.vendorService = vendorService;
        this.vendorRepository = vendorRepository;
        this.managerService = managerService;
        this.managerDashboardService = managerDashboardService;
        this.vehicleRepository = vehicleRepository;
    }

    // =========================================================
    // SAVE COMPLAINT (USER/DRIVER)
    // Validates vehicle exists via useronboarding VehicleRepository
    // =========================================================
    @Override
    public String saveComplaint(ComplaintRequestDTO request, String customerId) {
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

            String vehicleId = data.get("vehicleId") != null
                    ? data.get("vehicleId").toString()
                    : "";

            // ---- Validate vehicle ownership via useronboarding VehicleRepository ----
            if (!vehicleId.isBlank()) {
                try {
                    Long vehicleIdLong = Long.parseLong(vehicleId);
                    boolean vehicleExists = vehicleRepository.existsById(vehicleIdLong);
                    if (!vehicleExists) {
                        return "Error: Vehicle with ID " + vehicleId + " does not exist in the system.";
                    }
                } catch (NumberFormatException e) {
                    return "Error: vehicleId must be a valid numeric ID.";
                }
            }

            String jsonData = objectMapper.writeValueAsString(data);

            Complaint complaint = new Complaint();
            complaint.setIssueCategory(issueCategory);
            complaint.setData(jsonData);
            complaint.setCustomerId(customerId);
            complaint.setVehicleId(vehicleId);

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
                            "customerId", customerId,
                            "issueCategory", issueCategory,
                            "issueDescription", issueDescription,
                            "location", location,
                            "vehicleId", vehicleId
                    )
            );

            Map<String, Object> variables = new HashMap<>();
            variables.put("complaintId", savedComplaint.getId());
            variables.put("customerId", customerId);
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

    // =========================================================
    // GET COMPLAINTS — RBAC driven
    // Roles in system: DRIVER, VENDOR_ADMIN, MANAGER, ADMIN, SUPER_ADMIN
    //   DRIVER       -> own complaints (by customerId = JWT subject)
    //   VENDOR_ADMIN -> complaints assigned to their team (subject = vendor team name)
    //   MANAGER      -> escalated complaints only (status = ESCALATED_TO_MANAGER)
    //   ADMIN / SUPER_ADMIN -> all complaints
    // =========================================================
    @Override
    public List<Complaint> getComplaints() {
        String role = authContextService.getCurrentRole();
        String subject = authContextService.getCurrentSubject();

        if ("DRIVER".equalsIgnoreCase(role)) {
            return complaintRepository.findByCustomerIdOrderByCreatedAtDesc(subject);
        }

        if ("VENDOR_ADMIN".equalsIgnoreCase(role)) {
            return complaintRepository.findByAssignedTeamOrderByCreatedAtDesc(subject);
        }

        if ("MANAGER".equalsIgnoreCase(role)) {
            return complaintRepository.findByStatusOrderByCreatedAtDesc("ESCALATED_TO_MANAGER");
        }

        // ADMIN, SUPER_ADMIN — return all
        return complaintRepository.findAll();
    }

    // =========================================================
    // COMPLAINT DETAILS
    // =========================================================
    @Override
    public Complaint getComplaintDetails(Long complaintId) {
        return complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found with ID: " + complaintId));
    }

    // =========================================================
    // FILTER: VEHICLE
    // =========================================================
    @Override
    public List<Complaint> getComplaintsByVehicle(String vehicleId) {
        return complaintRepository.findByVehicleIdOrderByCreatedAtDesc(vehicleId);
    }

    // =========================================================
    // FILTER: STATUS
    // =========================================================
    @Override
    public List<Complaint> getComplaintStatus(String status) {
        return complaintRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    // =========================================================
    // VENDOR: GET ASSIGNED COMPLAINTS BY VENDOR NAME
    // =========================================================
    @Override
    public List<Complaint> getAssignedComplaintsByVendorName(String vendorName) {
        return vendorService.getAssignedComplaints(vendorName);
    }

    // =========================================================
    // VENDOR: UPDATE COMPLAINT STATUS
    // =========================================================
    @Override
    public String updateComplaintStatus(Long complaintId, String status) {
        return vendorService.updateComplaintStatus(complaintId, status);
    }

    // =========================================================
    // VENDOR: RESOLVE COMPLAINT
    // resolved=true  -> sets RESOLVED
    // resolved=false -> sets ESCALATED_TO_MANAGER
    // =========================================================
    @Override
    public String resolveComplaint(Long complaintId, Boolean resolved, String remarks) {
        return vendorService.resolveComplaint(complaintId, resolved, remarks);
    }

    // =========================================================
    // MANAGER: APPROVE AND ASSIGN COMPLAINT TO A TEAM
    // Uses ManagerDashboardService for team validation + Camunda
    // =========================================================
    @Override
    public Complaint approveAndAssignComplaint(Long complaintId, String teamName) {
        return managerDashboardService.approveAndAssignComplaint(complaintId, teamName);
    }

    // =========================================================
    // MANAGER: REJECT COMPLAINT
    // =========================================================
    @Override
    public Complaint rejectComplaint(Long complaintId) {
        return managerDashboardService.rejectComplaint(complaintId);
    }

    // =========================================================
    // MANAGER: CAMUNDA WORKFLOW DECISION
    // decision: "RESOLVE" | "REJECT" | "RETRY"
    // =========================================================
    @Override
    public String managerDecision(Long complaintId, String decision) {
        return managerService.managerDecision(complaintId, decision);
    }

    // =========================================================
    // MANAGER: VENDOR LISTING (for assignment/review UI)
    // Returns all available vendors via VendorRepository
    // =========================================================
    @Override
    public List<Vendor> getAvailableVendors() {
        return vendorRepository.findByAvailabilityTrue();
    }

    @Override
    public Vendor getVendorById(Long vendorId) {
        return vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found with ID: " + vendorId));
    }

    // =========================================================
    // DEPRECATED METHODS (backward compat only)
    // =========================================================

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
}