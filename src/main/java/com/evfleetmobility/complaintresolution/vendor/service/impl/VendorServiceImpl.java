package com.evfleetmobility.complaintresolution.vendor.service.impl;
import com.evfleetmobility.complaintresolution.auditlog.service.AuditLogService;


import com.evfleetmobility.complaintresolution.vendor.service.VendorService;
import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.vendor.entity.Vendor;
import com.evfleetmobility.complaintresolution.complaint.repository.ComplaintRepository;
import com.evfleetmobility.complaintresolution.vendor.repository.VendorRepository;

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
    private VendorRepository vendorRepository;

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

        // Mock location if missing
        if (complaintLatitude == null
                || complaintLongitude == null) {

            System.out.println(
                    "⚠️ Location missing. Using mock coordinates."
            );

            complaintLatitude = 11.0168;
            complaintLongitude = 76.9558;
        }

        final double finalComplaintLatitude =
                complaintLatitude;

        final double finalComplaintLongitude =
                complaintLongitude;

        // Fetch available vendors
        List<Vendor> availableVendors =
                vendorRepository.findByAvailabilityTrue();

        if (availableVendors.isEmpty()) {

            throw new RuntimeException(
                    "No available vendors found"
            );
        }

        // Find nearest vendor
        Vendor selectedVendor = availableVendors.stream()

                .filter(v ->
                        v.getLatitude() != null
                                && v.getLongitude() != null
                )

                .min(
                        Comparator.comparingDouble((Vendor v) ->
                                calculateDistance(
                                        finalComplaintLatitude,
                                        finalComplaintLongitude,
                                        v.getLatitude(),
                                        v.getLongitude()
                                )
                        ).thenComparing(
                                Comparator.comparingDouble(
                                        Vendor::getRating
                                ).reversed()
                        )
                )

                .orElseThrow(() ->
                        new RuntimeException(
                                "No vendor with valid location found"
                        )
                );

        double distanceKm =
                calculateDistance(
                        finalComplaintLatitude,
                        finalComplaintLongitude,
                        selectedVendor.getLatitude(),
                        selectedVendor.getLongitude()
                );

        // Workflow variables
        execution.setVariable(
                "vendorId",
                selectedVendor.getId()
        );

        execution.setVariable(
                "vendorName",
                selectedVendor.getName()
        );

        execution.setVariable(
                "vendorLocation",
                selectedVendor.getLocation()
        );

        execution.setVariable(
                "vendorRating",
                selectedVendor.getRating()
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
                "✅ Assigned Vendor: "
                        + selectedVendor.getName()
        );

        // Save vendor into complaint table
        Complaint complaint =
                complaintRepository.findById(complaintId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Complaint not found"
                                )
                        );

        complaint.setAssignedTeam(
                selectedVendor.getName()
        );

        complaint.setStatus("ASSIGNED_TO_VENDOR");

        complaintRepository.save(complaint);

        // Metadata
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
                selectedVendor.getName()
        );

        metadata.put(
                "vendorLocation",
                selectedVendor.getLocation()
        );

        metadata.put(
                "vendorRating",
                selectedVendor.getRating()
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

        // Save audit log
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

    // Vendor Dashboard Methods

    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    // ✅ Get vendor by ID
    public Vendor getVendorById(Long id) {
        return vendorRepository.findById(id)
                .orElse(null);
    }

    public List<Vendor> getAvailableVendors() {
        return vendorRepository.findByAvailabilityTrue();
    }

    // ✅ Get vendors by expertise
    public List<Vendor> getVendorsByExpertise(
            String expertise) {

        return vendorRepository
                .findByExpertiseIgnoreCase(expertise);
    }

    public List<Vendor> getVendorsByAvailability(
            Boolean availability) {

        return vendorRepository
                .findByAvailability(availability);
    }

    // ✅ Vendor dashboard assigned complaints
    public List<Complaint> getAssignedComplaints(
            String vendorName) {

        return complaintRepository
                .findByAssignedTeamOrderByCreatedAtDesc(
                        vendorName
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

        // Preserve assigned vendor
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

        // Find Camunda vendor task
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

        // Complete workflow task
        taskService.complete(
                task.getId(),
                java.util.Map.of(
                        "vendorResolved",
                        resolved
                )
        );

        // Preserve assigned vendor
        String assignedVendor =
                complaint.getAssignedTeam();

        if (Boolean.TRUE.equals(resolved)) {

            complaint.setStatus("RESOLVED");

        } else {

            complaint.setStatus(
                    "ESCALATED_TO_MANAGER"
            );
        }

        complaint.setAssignedTeam(
                assignedVendor
        );

        complaintRepository.save(complaint);

        // Save audit log
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

    // Helper Methods

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