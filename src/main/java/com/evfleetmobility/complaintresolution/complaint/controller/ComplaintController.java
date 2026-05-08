package com.evfleetmobility.complaintresolution.complaint.controller;

import com.evfleetmobility.complaintresolution.auditlog.service.AuditLogService;
import com.evfleetmobility.common.security.AuthContextService;
import com.evfleetmobility.complaintresolution.complaint.dto.AuditLogRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.dto.ComplaintActionRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.dto.ComplaintDetailsRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.dto.ComplaintRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.dto.ComplaintStatusRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.dto.VehicleComplaintRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.complaint.service.ComplaintService;



import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin("*")
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

    // CREATE COMPLAINT
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','DRIVER')")
    public String saveComplaint(
            @RequestBody ComplaintRequestDTO request
    ) {

        try {
            String customerId = authContextService.getCurrentUserId();

            return complaintService.saveComplaint(
                    request,
                    customerId
            );

        } catch (Exception e) {

            e.printStackTrace();

            return "Error: " + e.getMessage();
        }
    }

    // UNIFIED GET COMPLAINTS
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','DRIVER','VENDOR','VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> getComplaints() {

        return ResponseEntity.ok(
                complaintService.getComplaints()
        );
    }

    // COMPLAINT DETAILS
    @PostMapping("/details")
    @PreAuthorize("hasAnyRole('USER','DRIVER','VENDOR','VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> getComplaintDetails(
            @RequestBody ComplaintDetailsRequestDTO request
    ) {

        return ResponseEntity.ok(
                complaintService.getComplaintDetails(
                        request.getComplaintId()
                )
        );
    }

    // STATUS FILTER
    @PostMapping("/status")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> getComplaintStatus(
            @RequestBody ComplaintStatusRequestDTO request
    ) {

        return ResponseEntity.ok(
                complaintService.getComplaintStatus(
                        request.getStatus()
                )
        );
    }

    // VEHICLE FILTER
    @PostMapping("/vehicle")
    @PreAuthorize("hasAnyRole('VENDOR','VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> getComplaintsByVehicle(
            @RequestBody VehicleComplaintRequestDTO request
    ) {

        return ResponseEntity.ok(
                complaintService.getComplaintsByVehicle(
                        request.getVehicleId()
                )
        );
    }

    // CENTRALIZED ACTION API
    @PostMapping("/action")
    @PreAuthorize("hasAnyRole('USER','DRIVER','VENDOR','VENDOR_ADMIN','MANAGER')")
    public ResponseEntity<?> handleComplaintAction(
            @RequestBody ComplaintActionRequestDTO request
    ) {

        return ResponseEntity.ok(
                complaintService.handleComplaintAction(request)
        );
    }

    // AUDIT LOGS
    @PostMapping("/audit-logs")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> getAuditLogs(
            @RequestBody AuditLogRequestDTO request
    ) {

        return ResponseEntity.ok(
                auditLogService.getLogsByComplaintId(
                        request.getComplaintId()
                )
        );
    }

    // =========================
    // OLD APIs (TEMPORARY)
    // =========================

    // MY COMPLAINTS
    @Deprecated
    @GetMapping("/my-complaints")
    @PreAuthorize("hasAnyRole('USER','DRIVER')")
    public List<Complaint> getMyComplaints() {

        try {
            String customerId = authContextService.getCurrentUserId();

            return complaintService.getMyComplaints(
                    customerId
            );

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Error fetching complaints: "
                            + e.getMessage()
            );
        }
    }

    // GET VEHICLE COMPLAINTS
    @Deprecated
    @GetMapping("/vehicle/{vehicleId}")
    @PreAuthorize("hasAnyRole('VENDOR','VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
    public List<Complaint> getComplaintsByVehicleId(
            @PathVariable String vehicleId
    ) {

        return complaintService
                .getComplaintsByVehicleId(vehicleId);
    }

    // GET ALL COMPLAINTS
    @Deprecated
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public List<Complaint> getAllComplaints() {

        return complaintService.getAllComplaints();
    }

    // GET COMPLAINT BY ID
    @Deprecated
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','DRIVER','VENDOR','VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
    public Complaint getComplaintById(
            @PathVariable Long id
    ) {

        return complaintService.getComplaintById(id);
    }

    // STATUS FILTER
    @Deprecated
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public List<Complaint> getComplaintsByStatus(
            @PathVariable String status
    ) {

        return complaintService
                .getComplaintsByStatus(status);
    }

    // PRIORITY FILTER
    @Deprecated
    @GetMapping("/priority/{priority}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
    public List<Complaint> getComplaintsByPriority(
            @PathVariable String priority
    ) {

        return complaintService
                .getComplaintsByPriority(priority);
    }
}