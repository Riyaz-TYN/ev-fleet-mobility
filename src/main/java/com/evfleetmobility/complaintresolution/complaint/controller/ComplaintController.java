package com.evfleetmobility.complaintresolution.complaint.controller;

import com.evfleetmobility.complaintresolution.auditlog.service.AuditLogService;
import com.evfleetmobility.common.security.AuthContextService;
import com.evfleetmobility.complaintresolution.complaint.dto.AuditLogRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.dto.ComplaintDetailsRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.dto.ComplaintRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.dto.ComplaintStatusRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.dto.VehicleComplaintRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.service.ComplaintService;
import com.evfleetmobility.complaintresolution.manager.dto.ManagerApproveRequestDTO;
import com.evfleetmobility.complaintresolution.manager.dto.ManagerDecisionRequestDTO;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorIdRequestDTO;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorNameRequestDTO;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorResolveRequestDTO;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorStatusUpdateDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// Note: @CrossOrigin removed — CORS is handled globally in SecurityConfig via CorsConfigurationSource
// Roles in this system: DRIVER, VENDOR_ADMIN, MANAGER, ADMIN, SUPER_ADMIN

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;
    private final AuthContextService authContextService;
    private final AuditLogService auditLogService;

    public ComplaintController(
            ComplaintService complaintService,
            AuthContextService authContextService,
            AuditLogService auditLogService
    ) {
        this.complaintService = complaintService;
        this.authContextService = authContextService;
        this.auditLogService = auditLogService;
    }

    // =========================================================
    // CREATE COMPLAINT
    // Role: DRIVER
    // =========================================================
    @PostMapping
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> saveComplaint(
            @RequestBody ComplaintRequestDTO request
    ) {
        String customerId = authContextService.getCurrentUserId().toString();

        String result = complaintService.saveComplaint(request, customerId);

        return ResponseEntity.ok(
                java.util.Map.of(
                        "message", result,
                        "payloadSentToAI", request
                )
        );
    }
    // =========================================================
    // GET COMPLAINTS (ROLE-BASED — single endpoint, smart filter)
    //   DRIVER       -> their own complaints (by customerId)
    //   VENDOR_ADMIN -> complaints assigned to their team
    //   MANAGER      -> escalated complaints (ESCALATED_TO_MANAGER)
    //   ADMIN        -> all complaints
    //   SUPER_ADMIN  -> all complaints
    // =========================================================
    @GetMapping
    @PreAuthorize("hasAnyRole('DRIVER','VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> getComplaints() {
        return ResponseEntity.ok(complaintService.getComplaints());
    }

    // =========================================================
    // GET COMPLAINT DETAILS
    // Roles: all roles
    // =========================================================
    @PostMapping("/details")
    @PreAuthorize("hasAnyRole('DRIVER','VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> getComplaintDetails(
            @RequestBody ComplaintDetailsRequestDTO request
    ) {
        return ResponseEntity.ok(
                complaintService.getComplaintDetails(request.getComplaintId())
        );
    }

    // =========================================================
    // FILTER BY STATUS
    // Roles: MANAGER, ADMIN, SUPER_ADMIN
    // =========================================================
    @PostMapping("/filter/status")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> getComplaintsByStatus(
            @RequestBody ComplaintStatusRequestDTO request
    ) {
        return ResponseEntity.ok(
                complaintService.getComplaintStatus(request.getStatus())
        );
    }

    // =========================================================
    // FILTER BY VEHICLE
    // Roles: VENDOR_ADMIN, MANAGER, ADMIN, SUPER_ADMIN
    // =========================================================
    @PostMapping("/filter/vehicle")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> getComplaintsByVehicle(
            @RequestBody VehicleComplaintRequestDTO request
    ) {
        return ResponseEntity.ok(
                complaintService.getComplaintsByVehicle(request.getVehicleId())
        );
    }

    // =========================================================
    // AUDIT LOGS
    // Roles: ADMIN, SUPER_ADMIN
    // =========================================================
    @PostMapping("/audit-logs")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> getAuditLogs(
            @RequestBody AuditLogRequestDTO request
    ) {
        return ResponseEntity.ok(
                auditLogService.getLogsByComplaintId(request.getComplaintId())
        );
    }

    // =========================================================
    // VENDOR_ADMIN: GET ASSIGNED COMPLAINTS BY TEAM NAME
    // Replaces: POST /api/vendors/complaints
    // Roles: VENDOR_ADMIN, MANAGER, ADMIN, SUPER_ADMIN
    // =========================================================
    @PostMapping("/assigned")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> getAssignedComplaints(
            @RequestBody VendorNameRequestDTO request
    ) {
        return ResponseEntity.ok(
                complaintService.getAssignedComplaintsByVendorName(request.getVendorName())
        );
    }

    // =========================================================
    // VENDOR_ADMIN: UPDATE COMPLAINT STATUS
    // Replaces: PUT /api/vendors/complaints/status
    // Roles: VENDOR_ADMIN, MANAGER
    // =========================================================
    @PutMapping("/status")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN','MANAGER')")
    public ResponseEntity<String> updateComplaintStatus(
            @RequestBody VendorStatusUpdateDTO request
    ) {
        return ResponseEntity.ok(
                complaintService.updateComplaintStatus(
                        request.getComplaintId(),
                        request.getStatus()
                )
        );
    }

    // =========================================================
    // VENDOR_ADMIN: RESOLVE COMPLAINT
    // Replaces: PUT /api/vendors/complaints/resolve
    // resolved=true  -> RESOLVED
    // resolved=false -> ESCALATED_TO_MANAGER
    // Roles: VENDOR_ADMIN, MANAGER
    // =========================================================
    @PutMapping("/resolve")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN','MANAGER')")
    public ResponseEntity<String> resolveComplaint(
            @RequestBody VendorResolveRequestDTO request
    ) {
        return ResponseEntity.ok(
                complaintService.resolveComplaint(
                        request.getComplaintId(),
                        request.getResolved(),
                        request.getResolutionRemarks()
                )
        );
    }

    // =========================================================
    // MANAGER: APPROVE AND ASSIGN COMPLAINT TO A TEAM
    // Replaces: PUT /api/manager/complaints/approve
    // Roles: MANAGER, ADMIN, SUPER_ADMIN
    // =========================================================
    @PutMapping("/assign")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> approveAndAssign(
            @RequestBody ManagerApproveRequestDTO request
    ) {
        return ResponseEntity.ok(
                complaintService.approveAndAssignComplaint(
                        request.getComplaintId(),
                        request.getTeamName()
                )
        );
    }

    // =========================================================
    // MANAGER: REJECT COMPLAINT
    // Replaces: PUT /api/manager/complaints/reject
    // Roles: MANAGER, ADMIN, SUPER_ADMIN
    // =========================================================
    @PutMapping("/reject")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> rejectComplaint(
            @RequestBody ComplaintDetailsRequestDTO request
    ) {
        return ResponseEntity.ok(
                complaintService.rejectComplaint(request.getComplaintId())
        );
    }

    // =========================================================
    // MANAGER: CAMUNDA WORKFLOW DECISION
    // Replaces: PUT /api/manager/complaints/decision
    // decision values: "RESOLVE" | "REJECT" | "RETRY"
    // Roles: MANAGER, ADMIN, SUPER_ADMIN
    // =========================================================
    @PutMapping("/decision")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<String> managerDecision(
            @RequestBody ManagerDecisionRequestDTO request
    ) {
        return ResponseEntity.ok(
                complaintService.managerDecision(
                        request.getComplaintId(),
                        request.getManagerDecision()
                )
        );
    }

    // =========================================================
    // MANAGER: VIEW AVAILABLE VENDORS (for assignment)
    // Replaces: GET /api/vendors (manager view)
    // Roles: MANAGER, ADMIN, SUPER_ADMIN
    // =========================================================
    @GetMapping("/vendors")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> getAvailableVendors() {
        return ResponseEntity.ok(
                complaintService.getAvailableVendors()
        );
    }

    // =========================================================
    // MANAGER: VIEW VENDOR DETAILS
    // Replaces: POST /api/vendors/details (manager context)
    // Roles: VENDOR_ADMIN, MANAGER, ADMIN, SUPER_ADMIN
    // =========================================================
    @PostMapping("/vendors/details")
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> getVendorDetails(
            @RequestBody VendorIdRequestDTO request
    ) {
        return ResponseEntity.ok(
                complaintService.getVendorById(request.getVendorId())
        );
    }
}
