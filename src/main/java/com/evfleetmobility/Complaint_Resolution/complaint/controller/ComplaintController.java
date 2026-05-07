package com.complaint_resolution.complaint.controller;

import com.complaint_resolution.auditlog.service.AuditLogService;
import com.evfleetmobility.common.security.JwtUtil;
import com.complaint_resolution.complaint.dto.AuditLogRequestDTO;
import com.complaint_resolution.complaint.dto.ComplaintActionRequestDTO;
import com.complaint_resolution.complaint.dto.ComplaintDetailsRequestDTO;
import com.complaint_resolution.complaint.dto.ComplaintRequestDTO;
import com.complaint_resolution.complaint.dto.ComplaintStatusRequestDTO;
import com.complaint_resolution.complaint.dto.VehicleComplaintRequestDTO;
import com.complaint_resolution.complaint.entity.Complaint;
import com.complaint_resolution.complaint.service.ComplaintService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;
    private final JwtUtil jwtUtil;
    private final AuditLogService auditLogService;

    public ComplaintController(
            ComplaintService complaintService,
            JwtUtil jwtUtil,
            AuditLogService auditLogService
    ) {
        this.complaintService = complaintService;
        this.jwtUtil = jwtUtil;
        this.auditLogService = auditLogService;
    }

    // CREATE COMPLAINT
    @PostMapping
    public String saveComplaint(
            @RequestBody ComplaintRequestDTO request,
            HttpServletRequest httpRequest
    ) {

        try {

            String authHeader =
                    httpRequest.getHeader("Authorization");

            if (authHeader == null ||
                    !authHeader.startsWith("Bearer ")) {

                return "Error: Missing or invalid Authorization header";
            }

            String token = authHeader.substring(7);

            String customerId =
                    jwtUtil.extractCustomerId(token);

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
    @PreAuthorize("hasAnyRole('USER','VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> getComplaints() {

        return ResponseEntity.ok(
                complaintService.getComplaints()
        );
    }

    // COMPLAINT DETAILS
    @PostMapping("/details")
    @PreAuthorize("hasAnyRole('DRIVER','VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
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
    @PreAuthorize("hasAnyRole('VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
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
    @PreAuthorize("hasAnyRole('USER','VENDOR_ADMIN','MANAGER')")
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
    public List<Complaint> getMyComplaints(
            HttpServletRequest request
    ) {

        try {

            String authHeader =
                    request.getHeader("Authorization");

            if (authHeader == null ||
                    !authHeader.startsWith("Bearer ")) {

                throw new RuntimeException(
                        "Missing or invalid Authorization header"
                );
            }

            String token = authHeader.substring(7);

            String customerId =
                    jwtUtil.extractCustomerId(token);

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
    public List<Complaint> getComplaintsByVehicleId(
            @PathVariable String vehicleId
    ) {

        return complaintService
                .getComplaintsByVehicleId(vehicleId);
    }

    // GET ALL COMPLAINTS
    @Deprecated
    @GetMapping("/all")
    public List<Complaint> getAllComplaints() {

        return complaintService.getAllComplaints();
    }

    // GET COMPLAINT BY ID
    @Deprecated
    @GetMapping("/{id}")
    public Complaint getComplaintById(
            @PathVariable Long id
    ) {

        return complaintService.getComplaintById(id);
    }

    // STATUS FILTER
    @Deprecated
    @GetMapping("/status/{status}")
    public List<Complaint> getComplaintsByStatus(
            @PathVariable String status
    ) {

        return complaintService
                .getComplaintsByStatus(status);
    }

    // PRIORITY FILTER
    @Deprecated
    @GetMapping("/priority/{priority}")
    public List<Complaint> getComplaintsByPriority(
            @PathVariable String priority
    ) {

        return complaintService
                .getComplaintsByPriority(priority);
    }
}