package com.evfleetmobility.complaintresolution.manager.service.impl;

import com.evfleetmobility.complaintresolution.auditlog.service.AuditLogService;
import com.evfleetmobility.complaintresolution.manager.service.ManagerService;
import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.complaint.repository.ComplaintRepository;
import com.evfleetmobility.useronboarding.vehicleservices.entity.VehicleStatus;
import com.evfleetmobility.useronboarding.vehicleservices.repository.VehicleRepository;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public String managerDecision(Long complaintId, String decision, String remarks) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        Task task = taskService.createTaskQuery()
                .processVariableValueEquals("complaintId", complaintId)
                .taskDefinitionKey("managerTask")
                .singleResult();

        if (task == null) {
            return "Manager task not found";
        }

        taskService.complete(task.getId(), Map.of("managerDecision", decision));

        String previousStatus = complaint.getStatus();

        if ("RESOLVE".equalsIgnoreCase(decision)) {
            complaint.setStatus("RESOLVED");
            if (complaint.getVehicleId() != null) {
                vehicleRepository.findById(Long.parseLong(complaint.getVehicleId())).ifPresent(vehicle -> {
                    vehicle.setStatus(VehicleStatus.ACTIVE);
                    vehicleRepository.save(vehicle);
                });
            }
        } else if ("REJECT".equalsIgnoreCase(decision)) {
            complaint.setStatus("REJECTED");
            if (complaint.getVehicleId() != null) {
                vehicleRepository.findById(Long.parseLong(complaint.getVehicleId())).ifPresent(vehicle -> {
                    vehicle.setStatus(VehicleStatus.ACTIVE);
                    vehicleRepository.save(vehicle);
                });
            }
        } else if ("RETRY".equalsIgnoreCase(decision)) {
            complaint.setStatus("RETRY_VENDOR");
        }

        complaint.addWorkHistory("Manager Decision", "Decision: " + decision, remarks);

        complaintRepository.save(complaint);

        auditLogService.saveLog(
                complaintId,
                complaint.getVehicleId(),
                "MANAGER_DECISION",
                "MANAGER",
                previousStatus,
                complaint.getStatus(),
                "Manager decision completed: " + decision,
                Map.of("decision", decision, "remarks", remarks != null ? remarks : "")
        );

        return "Manager decision updated";
    }
}