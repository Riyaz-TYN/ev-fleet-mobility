package com.evfleetmobility.complaintresolution.manager.service.impl;

import com.evfleetmobility.complaintresolution.auditlog.service.AuditLogService;
import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.complaint.repository.ComplaintRepository;
import com.evfleetmobility.complaintresolution.manager.service.ManagerDashboardService;
import com.evfleetmobility.complaintresolution.vendor.dto.VendorDTO;
import com.evfleetmobility.useronboarding.authservices.entity.ApprovalStatus;
import com.evfleetmobility.useronboarding.profileservices.entity.OrganizationDetails;
import com.evfleetmobility.useronboarding.profileservices.repository.OrganizationRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ManagerDashboardServiceImpl implements ManagerDashboardService {

    private final ComplaintRepository complaintRepository;
    private final ObjectMapper objectMapper;
    private final OrganizationRepository organizationRepo;
    private final AuditLogService auditLogService;
    private final TaskService taskService;

    public ManagerDashboardServiceImpl(
            ComplaintRepository complaintRepository,
            ObjectMapper objectMapper,
            OrganizationRepository organizationRepo,
            AuditLogService auditLogService,
            TaskService taskService
    ) {
        this.complaintRepository = complaintRepository;
        this.objectMapper = objectMapper;
        this.organizationRepo = organizationRepo;
        this.auditLogService = auditLogService;
        this.taskService = taskService;
    }

    public List<Map<String, Object>> getAllComplaintsForManager() {
        return convertToMapList(complaintRepository.findAll());
    }

    public List<Map<String, Object>> getEscalatedComplaintsForManager() {
        return convertToMapList(complaintRepository.findByStatusOrderByCreatedAtDesc("ESCALATED_TO_MANAGER"));
    }

    private List<Map<String, Object>> convertToMapList(List<Complaint> complaints) {
        List<Map<String, Object>> managerList = new ArrayList<>();
        for (Complaint complaint : complaints) {
            try {
                Map<String, Object> data = new HashMap<>();
                if (complaint.getData() != null && !complaint.getData().isEmpty()) {
                    data = objectMapper.readValue(complaint.getData(), new TypeReference<Map<String, Object>>() {});
                }
                data.put("complaintId", complaint.getId());

                data.put("status", complaint.getStatus());
                data.put("issueCategory", complaint.getIssueCategory());
                data.put("priority", complaint.getPriority());
                data.put("assignedTeam", complaint.getAssignedTeam());
                data.put("vendorId", complaint.getVendorId());
                data.put("createdAt", complaint.getCreatedAt());
                data.put("latitude", complaint.getLatitude());
                data.put("longitude", complaint.getLongitude());
                data.put("escalationReason", complaint.getEscalationReason());
                managerList.add(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return managerList;
    }

    @Override
    public Complaint approveAndAssignComplaint(Long complaintId, Long vendorId) {
        OrganizationDetails vendor = organizationRepo.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found with ID: " + vendorId));

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        String previousStatus = complaint.getStatus();
        
        complaint.setStatus("ASSIGNED_TO_VENDOR");
        complaint.setAssignedTeam(vendor.getCompanyName());
        complaint.setVendorId(vendor.getId());
        complaint.setEscalationReason(null);

        complaint.addWorkHistory("Vendor Assigned", vendor.getCompanyName() + " (ID: " + vendorId + ")", null);

        Complaint savedComplaint = complaintRepository.save(complaint);

        // Complete manager task if it exists
        Task task = taskService.createTaskQuery()
                .processVariableValueEquals("complaintId", complaintId)
                .taskDefinitionKey("managerTask")
                .singleResult();

        if (task != null) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("managerDecision", "RETRY");
            variables.put("vendorId", vendor.getId());
            variables.put("vendorName", vendor.getCompanyName());
            variables.put("vendorLocation", vendor.getAddressLine1());
            variables.put("vendorRating", vendor.getVendorRating());
            variables.put("vendorExpertise", vendor.getExpertise());
            variables.put("skipAutoAssignment", true);
            taskService.complete(task.getId(), variables);
        }

        auditLogService.saveLog(
                complaintId,
                complaint.getVehicleId(),
                "MANAGER_ASSIGNED",
                "MANAGER",
                previousStatus,
                "ASSIGNED_TO_VENDOR",
                "Manager approved and assigned to: " + vendor.getCompanyName(),
                Map.of("vendorId", vendorId, "vendorName", vendor.getCompanyName())
        );

        return savedComplaint;
    }

    public Complaint rejectComplaint(Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        complaint.setStatus("REJECTED");
        complaint.setAssignedTeam(null);
        complaint.setVendorId(null);
        
        complaint.addWorkHistory("Complaint Rejected", "Decision by Manager", null);
        
        return complaintRepository.save(complaint);
    }

    public List<Map<String, Object>> getManagerHistory() {
        List<Complaint> complaints = complaintRepository.findAll().stream()
                .filter(c -> List.of("RESOLVED", "REJECTED", "RETRY_VENDOR", "ASSIGNED_TO_VENDOR").contains(c.getStatus()))
                .collect(Collectors.toList());
        return convertToMapList(complaints);
    }

    @Override
    public List<VendorDTO> getNearbyVendorsForComplaint(Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        Double complaintLat = complaint.getLatitude();
        Double complaintLon = complaint.getLongitude();

        if (complaintLat == null || complaintLon == null) {
            complaintLat = 11.0168;
            complaintLon = 76.9558;
        }

        final Double finalLat = complaintLat;
        final Double finalLon = complaintLon;

        List<OrganizationDetails> approvedVendors = organizationRepo.findByApprovalStatusAndVendorAvailabilityTrue(ApprovalStatus.APPROVED);

        return approvedVendors.stream()
                .filter(v -> v.getLatitude() != null && v.getLongitude() != null)
                .map(v -> {
                    VendorDTO dto = new VendorDTO();
                    dto.setVendorId(v.getId());
                    dto.setCompanyName(v.getCompanyName());
                    dto.setEmail(v.getEmail());
                    dto.setPhoneNumber(v.getPhoneNumber());
                    dto.setAddress(v.getAddressLine1());
                    dto.setRating(v.getVendorRating());
                    dto.setAvailability(v.getVendorAvailability());
                    dto.setExpertise(v.getExpertise());
                    dto.setDistanceKm(calculateDistance(finalLat, finalLon, v.getLatitude(), v.getLongitude()));
                    return dto;
                })
                .sorted(Comparator.comparingDouble(VendorDTO::getDistanceKm))
                .collect(Collectors.toList());
    }

    @Override
    public Complaint reassignVendor(Long complaintId, Long vendorId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        OrganizationDetails vendor = organizationRepo.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        String previousStatus = complaint.getStatus();
        
        complaint.setVendorId(vendor.getId());
        complaint.setAssignedTeam(vendor.getCompanyName());
        complaint.setStatus("ASSIGNED_TO_VENDOR");
        complaint.setEscalationReason(null);

        complaint.addWorkHistory("Vendor Reassigned", vendor.getCompanyName() + " (ID: " + vendorId + ")", null);

        Complaint savedComplaint = complaintRepository.save(complaint);

        Task task = taskService.createTaskQuery()
                .processVariableValueEquals("complaintId", complaintId)
                .taskDefinitionKey("managerTask")
                .singleResult();

        if (task != null) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("managerDecision", "RETRY");
            variables.put("vendorId", vendor.getId());
            variables.put("vendorName", vendor.getCompanyName());
            variables.put("vendorLocation", vendor.getAddressLine1());
            variables.put("vendorRating", vendor.getVendorRating());
            variables.put("vendorExpertise", vendor.getExpertise());
            variables.put("skipAutoAssignment", true);
            taskService.complete(task.getId(), variables);
        }

        auditLogService.saveLog(
                complaintId,
                complaint.getVehicleId(),
                "VENDOR_REASSIGNED",
                "MANAGER",
                previousStatus,
                "ASSIGNED_TO_VENDOR",
                "Manager manually reassigned vendor to: " + vendor.getCompanyName(),
                Map.of("vendorId", vendorId, "vendorName", vendor.getCompanyName())
        );

        return savedComplaint;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}