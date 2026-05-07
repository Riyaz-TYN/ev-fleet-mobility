package com.evfleetmobility.complaintresolution.manager.service.impl;


import com.evfleetmobility.complaintresolution.manager.service.ManagerDecisionService;
import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.complaint.repository.ComplaintRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("managerDecisionService")
public class ManagerDecisionServiceImpl implements ManagerDecisionService, JavaDelegate {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Override
    public void execute(DelegateExecution execution) {

        System.out.println("‍ Manager decision processing...");

        Long complaintId = (Long) execution.getVariable("complaintId");
        String decision = (String) execution.getVariable("managerDecision");

        if (complaintId == null) {
            System.out.println("⚠️ complaintId is null");
            return;
        }

        Complaint complaint = complaintRepository.findById(complaintId).orElse(null);

        if (complaint != null) {

            if ("RESOLVE".equalsIgnoreCase(decision)) {
                complaint.setStatus("RESOLVED");

            } else if ("REJECT".equalsIgnoreCase(decision)) {
                complaint.setStatus("REJECTED");

            } else if ("RETRY".equalsIgnoreCase(decision)) {
                complaint.setStatus("IN_PROGRESS"); // back to vendor

            } else {
                System.out.println("⚠️ Unknown decision: " + decision);
            }

            complaintRepository.save(complaint);

            System.out.println("✅ Manager decision applied: " + decision);

        } else {
            System.out.println("⚠️ Complaint not found");
        }
    }
}