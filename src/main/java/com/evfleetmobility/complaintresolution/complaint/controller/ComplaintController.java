package com.evfleetmobility.complaintresolution.complaint.controller;

import com.evfleetmobility.complaintresolution.auditlog.service.AuditLogService;
import com.evfleetmobility.common.security.AuthContextService;
import com.evfleetmobility.complaintresolution.complaint.dto.AuditLogRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.dto.ComplaintDetailsRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.dto.ComplaintRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.dto.ComplaintStatusRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.dto.VehicleComplaintRequestDTO;
import com.evfleetmobility.complaintresolution.complaint.service.ComplaintService;
import com.evfleetmobility.complaintresolution.manager.dto.ManagerDecisionRequestDTO;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorIdRequestDTO;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorResolveRequestDTO;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorStatusUpdateDTO;
import com.evfleetmobility.complaintresolution.complaint.dto.TechnicianAssignRequestDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

        private final ComplaintService complaintService;
        private final AuthContextService authContextService;
        private final AuditLogService auditLogService;

        public ComplaintController(
                        ComplaintService complaintService,
                        AuthContextService authContextService,
                        AuditLogService auditLogService) {
                this.complaintService = complaintService;
                this.authContextService = authContextService;
                this.auditLogService = auditLogService;
        }

        @PostMapping
        @PreAuthorize("hasRole('DRIVER')")
        public ResponseEntity<?> saveComplaint(
                        @RequestBody ComplaintRequestDTO request) {
                String customerId = authContextService.getCurrentUserId().toString();

                String result = complaintService.saveComplaint(request, customerId);

                return ResponseEntity.ok(
                                java.util.Map.of(
                                                "message", result,
                                                "payloadSentToAI", request));
        }

        @GetMapping
        @PreAuthorize("hasAnyRole('DRIVER','VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
        public ResponseEntity<?> getComplaints(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                return ResponseEntity.ok(complaintService.getComplaints(page, size));
        }

        @PostMapping("/details")
        @PreAuthorize("hasAnyRole('DRIVER','VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
        public ResponseEntity<?> getComplaintDetails(
                        @RequestBody ComplaintDetailsRequestDTO request) {
                return ResponseEntity.ok(
                                complaintService.getComplaintDetails(request.getComplaintId()));
        }

        @PostMapping("/ai-chat")
        @PreAuthorize("hasAnyRole('DRIVER','VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
        public ResponseEntity<?> getAIChatHistory(
                        @RequestBody ComplaintDetailsRequestDTO request) {
                return ResponseEntity.ok(
                                complaintService.getAIChatHistory(request.getComplaintId()));
        }

        @PostMapping("/filter/status")
        @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
        public ResponseEntity<?> getComplaintsByStatus(
                        @RequestBody ComplaintStatusRequestDTO request,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                return ResponseEntity.ok(
                                complaintService.getComplaintStatus(request.getStatus(), page, size));
        }

        @PostMapping("/filter/vehicle")
        @PreAuthorize("hasAnyRole('VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
        public ResponseEntity<?> getComplaintsByVehicle(
                        @RequestBody VehicleComplaintRequestDTO request,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                return ResponseEntity.ok(
                                complaintService.getComplaintsByVehicle(request.getVehicleId(), page, size));
        }

        @PostMapping("/audit-logs")
        @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
        public ResponseEntity<?> getAuditLogs(
                        @RequestBody AuditLogRequestDTO request) {
                return ResponseEntity.ok(
                                auditLogService.getLogsByComplaintId(request.getComplaintId()));
        }

        @PostMapping("/assigned")
        @PreAuthorize("hasAnyRole('VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
        public ResponseEntity<?> getAssignedComplaints(
                        @RequestBody com.evfleetmobility.complaintresolution.vendor.dto.VendorIdRequestDTO request) {
                return ResponseEntity.ok(
                                complaintService.getAssignedComplaintsByVendorId(request.getVendorId()));
        }

        @PutMapping("/status")
        @PreAuthorize("hasAnyRole('VENDOR_ADMIN','MANAGER')")
        public ResponseEntity<String> updateComplaintStatus(
                        @RequestBody VendorStatusUpdateDTO request) {
                return ResponseEntity.ok(
                                complaintService.updateComplaintStatus(
                                                request.getComplaintId(),
                                                request.getStatus()));
        }

        @PutMapping("/resolve")
        @PreAuthorize("hasAnyRole('VENDOR_ADMIN','MANAGER')")
        public ResponseEntity<String> resolveComplaint(
                        @RequestBody VendorResolveRequestDTO request) {
                return ResponseEntity.ok(
                                complaintService.resolveComplaint(
                                                request.getComplaintId(),
                                                request.getResolved(),
                                                request.getResolutionRemarks()));
        }

        @PutMapping("/assign")
        @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
        public ResponseEntity<?> approveAndAssign(
                        @RequestBody com.evfleetmobility.complaintresolution.manager.dto.ManagerApproveRequestDTO request) {
                return ResponseEntity.ok(
                                complaintService.approveAndAssignComplaint(
                                                request.getComplaintId(),
                                                request.getVendorId()));
        }

        @PutMapping("/reject")
        @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
        public ResponseEntity<?> rejectComplaint(
                        @RequestBody ComplaintDetailsRequestDTO request) {
                return ResponseEntity.ok(
                                complaintService.rejectComplaint(request.getComplaintId()));
        }

        @PutMapping("/decision")
        @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
        public ResponseEntity<String> managerDecision(
                        @RequestBody ManagerDecisionRequestDTO request) {
                return ResponseEntity.ok(
                                complaintService.managerDecision(
                                                request.getComplaintId(),
                                                request.getManagerDecision(),
                                                request.getRemarks()));
        }

        @GetMapping("/vendors")
        @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
        public ResponseEntity<?> getAvailableVendors() {
                return ResponseEntity.ok(
                                complaintService.getAvailableVendors());
        }

        @PostMapping("/vendors/details")
        @PreAuthorize("hasAnyRole('VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
        public ResponseEntity<?> getVendorDetails(
                        @RequestBody VendorIdRequestDTO request) {
                return ResponseEntity.ok(
                                complaintService.getVendorById(request.getVendorId()));
        }

        @PostMapping("/{complaintId}/nearby-vendors")
        @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
        public ResponseEntity<?> getNearbyVendors(@PathVariable Long complaintId) {
                return ResponseEntity.ok(complaintService.getNearbyVendors(complaintId));
        }

        @PutMapping("/reassign")
        @PreAuthorize("hasAnyRole('MANAGER','ADMIN','SUPER_ADMIN')")
        public ResponseEntity<?> reassignVendor(@RequestBody VendorIdRequestDTO request) {
                return ResponseEntity
                                .ok(complaintService.reassignVendor(request.getComplaintId(), request.getVendorId()));
        }

        @PutMapping("/assign-technician")
        @PreAuthorize("hasAnyRole('VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
        public ResponseEntity<?> assignTechnician(
                        @RequestBody TechnicianAssignRequestDTO request) {
                return ResponseEntity.ok(
                                complaintService.assignTechnician(
                                                request.getComplaintId(),
                                                request.getTechnicianId()));
        }
}
