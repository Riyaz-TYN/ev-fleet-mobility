package com.evfleetmobility.complaintresolution.vendor.service.impl;

import com.evfleetmobility.complaintresolution.auditlog.service.AuditLogService;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorDTO;
import com.evfleetmobility.complaintresolution.vendor.service.VendorService;
import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.useronboarding.profileservices.entity.OrganizationDetails;
import com.evfleetmobility.complaintresolution.complaint.repository.ComplaintRepository;
import com.evfleetmobility.useronboarding.profileservices.repository.OrganizationRepository;
import com.evfleetmobility.useronboarding.authservices.entity.ApprovalStatus;
import com.evfleetmobility.useronboarding.vehicleservices.entity.VehicleStatus;
import com.evfleetmobility.useronboarding.vehicleservices.repository.VehicleRepository;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("vendorService")
public class VendorServiceImpl implements VendorService, JavaDelegate {

    @Autowired
    private OrganizationRepository organizationRepo;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public void execute(DelegateExecution execution) {

        Boolean skipAuto = (Boolean) execution.getVariable("skipAutoAssignment");

        if (Boolean.TRUE.equals(skipAuto)) {
            System.out.println("Skipping auto-assignment due to manual override.");
            execution.removeVariable("skipAutoAssignment");
            return;
        }

        Long complaintId =
                (Long) execution.getVariable("complaintId");

        String vehicleId =
                (String) execution.getVariable("vehicleId");

        String issueCategory =
                (String) execution.getVariable("issueCategory");

        String location =
                (String) execution.getVariable("location");

        Double complaintLatitude =
                getDoubleVariable(execution, "complaintLatitude");

        Double complaintLongitude =
                getDoubleVariable(execution, "complaintLongitude");

        if (complaintLatitude == null
                || complaintLongitude == null) {

            complaintLatitude = 11.0168;
            complaintLongitude = 76.9558;
        }

        final double finalComplaintLatitude =
                complaintLatitude;

        final double finalComplaintLongitude =
                complaintLongitude;

        String predictedCategory =
                (String) execution.getVariable("predictedCategory");

        String searchCategory =
                (predictedCategory != null
                        && !predictedCategory.isBlank())
                        ? predictedCategory
                        : issueCategory;

        Complaint complaintForCheck =
                complaintRepository.findById(complaintId)
                        .orElse(null);

        Long previousVendorId =
                (complaintForCheck != null)
                        ? complaintForCheck.getVendorId()
                        : null;

        List<OrganizationDetails> availableVendors =
                organizationRepo
                        .findByApprovalStatusAndVendorAvailabilityTrue(
                                ApprovalStatus.APPROVED
                        );

        List<OrganizationDetails> validVendors =
                availableVendors.stream()
                        .filter(v ->
                                v.getLatitude() != null
                                        && v.getLongitude() != null
                        )
                        .filter(v ->
                                previousVendorId == null
                                        || !v.getId().equals(previousVendorId)
                        )
                        .toList();

        if (validVendors.isEmpty()) {

            Complaint complaint =
                    complaintRepository.findById(complaintId)
                            .orElseThrow(() ->
                                    new RuntimeException("Complaint not found")
                            );

            complaint.setStatus("ESCALATED_TO_MANAGER");

            complaint.setEscalationReason(
                    "No approved vendors available for assignment"
            );

            complaintRepository.save(complaint);

            auditLogService.saveLog(
                    complaintId,
                    vehicleId,
                    "VENDOR_UNRESOLVED",
                    "SYSTEM",
                    "AI_PROCESSED",
                    "ESCALATED_TO_MANAGER",
                    "No approved vendors available for assignment",
                    new HashMap<>()
            );

            throw new org.camunda.bpm.engine.delegate.BpmnError(
                    "NO_VENDOR",
                    "No approved vendors found"
            );
        }

        List<OrganizationDetails> expertVendors =
                validVendors.stream()
                        .filter(v ->
                                v.getExpertise() != null
                                        && searchCategory != null
                                        && (
                                        v.getExpertise()
                                                .toLowerCase()
                                                .contains(searchCategory.toLowerCase())
                                                ||
                                                searchCategory.toLowerCase()
                                                        .contains(
                                                                v.getExpertise()
                                                                        .toLowerCase()
                                                        )
                                )
                        )
                        .toList();

        List<OrganizationDetails> selectionPool =
                expertVendors.isEmpty()
                        ? validVendors
                        : expertVendors;

        OrganizationDetails selectedVendor =
                selectionPool.stream()
                        .min(
                                Comparator.comparingDouble(
                                        (OrganizationDetails v) ->
                                                calculateDistance(
                                                        finalComplaintLatitude,
                                                        finalComplaintLongitude,
                                                        v.getLatitude(),
                                                        v.getLongitude()
                                                )
                                ).thenComparing(
                                        Comparator.comparingDouble(
                                                OrganizationDetails::getVendorRating
                                        ).reversed()
                                )
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "No vendor found in selection pool"
                                )
                        );

        double distanceKm =
                calculateDistance(
                        finalComplaintLatitude,
                        finalComplaintLongitude,
                        selectedVendor.getLatitude(),
                        selectedVendor.getLongitude()
                );

        execution.setVariable(
                "vendorId",
                selectedVendor.getId()
        );

        execution.setVariable(
                "vendorName",
                selectedVendor.getCompanyName()
        );

        execution.setVariable(
                "vendorLocation",
                selectedVendor.getAddressLine1()
        );

        execution.setVariable(
                "vendorRating",
                selectedVendor.getVendorRating()
        );

        execution.setVariable(
                "vendorExpertise",
                selectedVendor.getExpertise()
        );

        execution.setVariable(
                "vendorDistanceKm",
                distanceKm
        );

        Complaint complaint =
                complaintRepository.findById(complaintId)
                        .orElseThrow(() ->
                                new RuntimeException("Complaint not found")
                        );

        complaint.setAssignedTeam(
                selectedVendor.getCompanyName()
        );

        complaint.setVendorId(selectedVendor.getId());

        complaint.setStatus("ASSIGNED_TO_VENDOR");

        if (complaint.getVehicleId() != null) {
            vehicleRepository.findById(Long.parseLong(complaint.getVehicleId())).ifPresent(vehicle -> {
                vehicle.setStatus(VehicleStatus.INACTIVE);
                vehicleRepository.save(vehicle);
            });
        }

        complaint.addWorkHistory(
                "Vendor Assigned",
                selectedVendor.getCompanyName()
                        + " (ID: "
                        + selectedVendor.getId()
                        + ")",
                null
        );

        complaintRepository.save(complaint);

        Map<String, Object> metadata =
                new HashMap<>();

        metadata.put("vehicleId", vehicleId);
        metadata.put("vendorId", selectedVendor.getId());
        metadata.put("vendorName", selectedVendor.getCompanyName());
        metadata.put("vendorLocation", selectedVendor.getAddressLine1());
        metadata.put("vendorRating", selectedVendor.getVendorRating());
        metadata.put("vendorExpertise", selectedVendor.getExpertise());
        metadata.put("distanceKm", distanceKm);
        metadata.put("issueCategory", issueCategory);
        metadata.put("complaintLocation", location);

        auditLogService.saveLog(
                complaintId,
                vehicleId,
                "VENDOR_ASSIGNED",
                "SYSTEM",
                "AI_SUPPORT",
                "ASSIGNED_TO_VENDOR",
                "Complaint assigned to nearest available vendor",
                metadata
        );
    }

    // =========================
    // DTO MAPPER
    // =========================

    private VendorDTO mapToDTO(OrganizationDetails org) {

        VendorDTO dto = new VendorDTO();

        dto.setVendorId(org.getId());

        dto.setCompanyName(org.getCompanyName());

        dto.setEmail(org.getEmail());

        dto.setPhoneNumber(org.getPhoneNumber());

        dto.setAddress(org.getAddressLine1());

        dto.setRating(org.getVendorRating());

        dto.setAvailability(org.getVendorAvailability());

        dto.setExpertise(org.getExpertise());

        dto.setLatitude(org.getLatitude());

        dto.setLongitude(org.getLongitude());

        dto.setApprovalStatus(
                org.getApprovalStatus().name()
        );

        return dto;
    }

    // =========================
    // VENDOR APIs
    // =========================

    @Override
    public Page<VendorDTO> getAllVendors(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return organizationRepo.findAll(pageable).map(this::mapToDTO);
    }

    @Override
    public Page<VendorDTO> getApprovedVendors(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return organizationRepo.findByApprovalStatus(ApprovalStatus.APPROVED, pageable).map(this::mapToDTO);
    }

    @Override
    public VendorDTO getVendorById(Long id) {

        OrganizationDetails vendor =
                organizationRepo.findById(id)
                        .orElse(null);

        if (vendor == null) {
            return null;
        }

        return mapToDTO(vendor);
    }

    @Override
    public Page<VendorDTO> getAvailableVendors(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return organizationRepo.findByApprovalStatusAndVendorAvailabilityTrue(ApprovalStatus.APPROVED, pageable).map(this::mapToDTO);
    }

    @Override
    public Page<VendorDTO> getVendorsByExpertise(String expertise, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return organizationRepo.findByExpertiseIgnoreCase(expertise, pageable).map(this::mapToDTO);
    }

    @Override
    public Page<VendorDTO> getVendorsByAvailability(Boolean availability, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return organizationRepo.findByVendorAvailability(availability, pageable).map(this::mapToDTO);
    }

    // =========================
    // COMPLAINT OPERATIONS
    // =========================

    @Override
    public Page<Complaint> getAssignedComplaints(Long vendorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return complaintRepository.findByVendorIdOrderByCreatedAtDesc(vendorId, pageable);
    }

    @Override
    public String updateComplaintStatus(
            Long complaintId,
            String status) {

        Complaint complaint =
                complaintRepository.findById(complaintId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Complaint not found"
                                )
                        );

        String previousStatus =
                complaint.getStatus();

        String assignedVendor =
                complaint.getAssignedTeam();

        complaint.setStatus(status);

        complaint.setAssignedTeam(assignedVendor);

        complaintRepository.save(complaint);

        auditLogService.saveLog(
                complaintId,
                complaint.getVehicleId(),
                "VENDOR_STATUS_UPDATED",
                "VENDOR",
                previousStatus,
                status,
                "Vendor updated complaint status",
                Map.of(
                        "status",
                        status
                )
        );

        return "Complaint status updated";
    }

    @Override
    public String resolveComplaint(
            Long complaintId,
            Boolean resolved,
            String remarks) {

        Complaint complaint =
                complaintRepository.findById(complaintId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Complaint not found"
                                )
                        );

        Task task =
                taskService.createTaskQuery()
                        .processVariableValueEquals(
                                "complaintId",
                                complaintId
                        )
                        .taskDefinitionKey("vendorTask")
                        .singleResult();

        if (task == null) {
            return "Vendor task not found";
        }

        String previousStatus =
                complaint.getStatus();

        taskService.complete(
                task.getId(),
                Map.of(
                        "vendorResolved",
                        resolved
                )
        );

        String assignedVendor =
                complaint.getAssignedTeam();

        if (Boolean.TRUE.equals(resolved)) {

            if (complaint.getVehicleId() != null) {
                vehicleRepository.findById(Long.parseLong(complaint.getVehicleId())).ifPresent(vehicle -> {
                    vehicle.setStatus(VehicleStatus.ACTIVE);
                    vehicleRepository.save(vehicle);
                });
            }

            complaint.setStatus("RESOLVED");

            complaint.addWorkHistory(
                    "Resolved by Vendor",
                    "Vendor: " + assignedVendor,
                    remarks != null && !remarks.isBlank()
                            ? remarks
                            : "Issue successfully resolved"
            );

        } else {

            complaint.setStatus("ESCALATED_TO_MANAGER");

            complaint.setEscalationReason(
                    "Vendor could not resolve: "
                            + (
                            remarks != null
                                    ? remarks
                                    : "No remarks provided"
                    )
            );

            complaint.addWorkHistory(
                    "Vendor Unresolved",
                    "Vendor: " + assignedVendor,
                    remarks != null && !remarks.isBlank()
                            ? remarks
                            : "Vendor could not resolve the issue"
            );
        }

        complaint.setAssignedTeam(assignedVendor);

        complaintRepository.save(complaint);

        auditLogService.saveLog(
                complaintId,
                complaint.getVehicleId(),
                Boolean.TRUE.equals(resolved)
                        ? "VENDOR_RESOLVED"
                        : "VENDOR_UNRESOLVED",
                "VENDOR",
                previousStatus,
                complaint.getStatus(),
                Boolean.TRUE.equals(resolved)
                        ? "Complaint resolved by vendor: "
                          + assignedVendor
                        : "Vendor "
                          + assignedVendor
                          + " could not resolve: "
                          + (
                        remarks != null
                        ? remarks
                        : ""
                ),
                Map.of(
                        "vendorName",
                        assignedVendor,
                        "vendorResolved",
                        resolved
                )
        );

        return Boolean.TRUE.equals(resolved)
                ? "Complaint resolved successfully"
                : "Complaint escalated to manager";
    }

    // =========================
    // HELPERS
    // =========================

    private Double getDoubleVariable(
            DelegateExecution execution,
            String name) {

        Object value =
                execution.getVariable(name);

        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        return Double.parseDouble(
                value.toString()
        );
    }

    private double calculateDistance(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {

        final int EARTH_RADIUS_KM = 6371;

        double latDistance =
                Math.toRadians(lat2 - lat1);

        double lonDistance =
                Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(latDistance / 2)
                        * Math.sin(latDistance / 2)
                        + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2)
                        * Math.sin(lonDistance / 2);

        double c =
                2 * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a)
                );

        return EARTH_RADIUS_KM * c;
    }
}