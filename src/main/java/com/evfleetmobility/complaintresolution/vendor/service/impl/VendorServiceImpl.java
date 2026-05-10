package com.evfleetmobility.complaintresolution.vendor.service.impl;
import com.evfleetmobility.complaintresolution.auditlog.service.AuditLogService;

import com.evfleetmobility.complaintresolution.vendor.service.VendorService;
import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.useronboarding.profileservices.entity.OrganizationDetails;
import com.evfleetmobility.complaintresolution.complaint.repository.ComplaintRepository;
import com.evfleetmobility.useronboarding.profileservices.repository.OrganizationRepository;
import com.evfleetmobility.useronboarding.authservices.entity.ApprovalStatus;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Override
    public void execute(DelegateExecution execution) {

        System.out.println(" Vendor assignment started...");

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

            System.out.println(
                    "âš ï¸  Location missing. Using mock coordinates."
            );

            complaintLatitude = 11.0168;
            complaintLongitude = 76.9558;
        }

        final double finalComplaintLatitude =
                complaintLatitude;

        final double finalComplaintLongitude =
                complaintLongitude;

        String predictedCategory =
                (String) execution.getVariable("predictedCategory");

        String searchCategory = (predictedCategory != null && !predictedCategory.isBlank()) 
                ? predictedCategory 
                : issueCategory;

        System.out.println(" Searching for vendors with expertise: " + searchCategory);

        List<OrganizationDetails> availableVendors =
                organizationRepo.findByApprovalStatusAndVendorAvailabilityTrue(ApprovalStatus.APPROVED);

        List<OrganizationDetails> validVendors = availableVendors.stream()
                .filter(v -> v.getLatitude() != null && v.getLongitude() != null)
                .toList();

        if (validVendors.isEmpty()) {

            System.out.println("âš ï¸  No approved and available vendors found. Escalating to Manager.");

            Complaint complaint = complaintRepository.findById(complaintId)
                    .orElseThrow(() -> new RuntimeException("Complaint not found"));

            complaint.setStatus("ESCALATED_TO_MANAGER");
            complaint.setEscalationReason("No approved vendors available for assignment");
            complaintRepository.save(complaint);

            auditLogService.saveLog(
                    complaintId, vehicleId, "VENDOR_UNRESOLVED", "SYSTEM", "AI_PROCESSED", "ESCALATED_TO_MANAGER",
                    "No approved vendors available for assignment", new HashMap<>()
            );

            throw new org.camunda.bpm.engine.delegate.BpmnError("NO_VENDOR", "No approved vendors found");
        }

        // Filter by expertise if possible
        List<OrganizationDetails> expertVendors = validVendors.stream()
                .filter(v -> v.getExpertise() != null && searchCategory != null &&
                        (v.getExpertise().toLowerCase().contains(searchCategory.toLowerCase()) ||
                         searchCategory.toLowerCase().contains(v.getExpertise().toLowerCase())))
                .toList();

        List<OrganizationDetails> selectionPool = expertVendors.isEmpty() ? validVendors : expertVendors;

        if (expertVendors.isEmpty()) {
            System.out.println("âš ï¸  No vendors with matching expertise found. Selecting nearest available.");
        }

        OrganizationDetails selectedVendor = selectionPool.stream()
                .min(
                        Comparator.comparingDouble((OrganizationDetails v) ->
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
                .orElseThrow(() -> new RuntimeException("No vendor found in selection pool"));

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

        System.out.println(
                "âœ… Assigned Vendor: "
                        + selectedVendor.getCompanyName()
        );

        Complaint complaint =
                complaintRepository.findById(complaintId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Complaint not found"
                                )
                        );

        complaint.setAssignedTeam(
                selectedVendor.getCompanyName()
        );

        complaint.setVendorId(selectedVendor.getId());
        complaint.setStatus("ASSIGNED_TO_VENDOR");

        complaint.addWorkHistory("Vendor Assigned", selectedVendor.getCompanyName() + " (ID: " + selectedVendor.getId() + ")", null);

        complaintRepository.save(complaint);

        Map<String, Object> metadata =
                new HashMap<>();

        metadata.put(
                "vehicleId",
                vehicleId
        );

        metadata.put(
                "vendorId",
                selectedVendor.getId()
        );

        metadata.put(
                "vendorName",
                selectedVendor.getCompanyName()
        );

        metadata.put(
                "vendorLocation",
                selectedVendor.getAddressLine1()
        );

        metadata.put(
                "vendorRating",
                selectedVendor.getVendorRating()
        );

        metadata.put(
                "vendorExpertise",
                selectedVendor.getExpertise()
        );

        metadata.put(
                "distanceKm",
                distanceKm
        );

        metadata.put(
                "issueCategory",
                issueCategory
        );

        metadata.put(
                "complaintLocation",
                location
        );

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

    public List<OrganizationDetails> getAllVendors() {
        return organizationRepo.findAll();
    }

    public OrganizationDetails getVendorById(Long id) {
        return organizationRepo.findById(id)
                .orElse(null);
    }

    public List<OrganizationDetails> getAvailableVendors() {
        return organizationRepo.findByApprovalStatusAndVendorAvailabilityTrue(ApprovalStatus.APPROVED);
    }

    public List<OrganizationDetails> getVendorsByExpertise(
            String expertise) {

        return organizationRepo
                .findByExpertiseIgnoreCase(expertise);
    }

    public List<OrganizationDetails> getVendorsByAvailability(
            Boolean availability) {

        return organizationRepo
                .findByVendorAvailability(availability);
    }

    public List<Complaint> getAssignedComplaints(
            Long vendorId) {

        return complaintRepository
                .findByVendorIdOrderByCreatedAtDesc(
                        vendorId
                );
    }

    public String updateComplaintStatus(
            Long complaintId,
            String status) {

        Complaint complaint =
                complaintRepository.findById(complaintId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Complaint not found"
                                )
                        );

        String previousStatus =
                complaint.getStatus();

        String assignedVendor =
                complaint.getAssignedTeam();

        complaint.setStatus(status);

        complaint.setAssignedTeam(
                assignedVendor
        );

        complaintRepository.save(complaint);

        auditLogService.saveLog(
                complaintId,
                complaint.getVehicleId(),
                "VENDOR_STATUS_UPDATED",
                "VENDOR",
                previousStatus,
                status,
                "Vendor updated complaint status",
                java.util.Map.of(
                        "status", status
                )
        );

        return "Complaint status updated";
    }

    public String resolveComplaint(
            Long complaintId,
            Boolean resolved,
            String remarks) {

        Complaint complaint =
                complaintRepository.findById(complaintId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Complaint not found"
                                )
                        );

        Task task = taskService.createTaskQuery()
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
                java.util.Map.of(
                        "vendorResolved",
                        resolved
                )
        );

        String assignedVendor =
                complaint.getAssignedTeam();

        // --- Smart Appending Logic ---
        complaint.addWorkHistory(
            "Vendor Review", 
            "Status: " + (Boolean.TRUE.equals(resolved) ? "RESOLVED" : "UNRESOLVED"), 
            remarks
        );
        // -----------------------------

        if (Boolean.TRUE.equals(resolved)) {

            complaint.setStatus("RESOLVED");

        } else {

            complaint.setStatus(
                    "ESCALATED_TO_MANAGER"
            );
            complaint.setEscalationReason("Vendor could not resolve: " + (remarks != null ? remarks : "No remarks provided"));
        }

        complaint.setAssignedTeam(
                assignedVendor
        );

        complaintRepository.save(complaint);

        auditLogService.saveLog(
                complaintId,
                complaint.getVehicleId(),
                Boolean.TRUE.equals(resolved)
                        ? "VENDOR_RESOLVED"
                        : "VENDOR_ESCALATED",
                "VENDOR",
                previousStatus,
                complaint.getStatus(),
                remarks,
                java.util.Map.of(
                        "vendorResolved",
                        resolved
                )
        );

        return Boolean.TRUE.equals(resolved)
                ? "Complaint resolved successfully"
                : "Complaint escalated to manager";
    }

    private Double getDoubleVariable(
            DelegateExecution execution,
            String name
    ) {

        Object value =
                execution.getVariable(name);

        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value)
                    .doubleValue();
        }

        return Double.parseDouble(
                value.toString()
        );
    }

    private double calculateDistance(
            double lat1,
            double lon1,
            double lat2,
            double lon2
    ) {

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
