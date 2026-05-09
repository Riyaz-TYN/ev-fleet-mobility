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

        System.out.println("â€ Manager decision processing...");

        Long complaintId = (Long) execution.getVariable("complaintId");
        String decision = (String) execution.getVariable("managerDecision");

        if (complaintId == null) {
            System.out.println("âš ï¸ complaintId is null");
            return;
        }

        Complaint complaint = complaintRepository.findById(complaintId).orElse(null);

        if (complaint != null) {

            if ("RESOLVE".equalsIgnoreCase(decision)) {
                complaint.setStatus("RESOLVED");

            } else if ("REJECT".equalsIgnoreCase(decision)) {
                complaint.setStatus("REJECTED");

            } else if ("RETRY".equalsIgnoreCase(decision)) {
                complaint.setStatus("IN_PROGRESS"); 

            } else {
                System.out.println("âš ï¸ Unknown decision: " + decision);
            }

            complaintRepository.save(complaint);

            System.out.println("âœ… Manager decision applied: " + decision);

        } else {
            System.out.println("âš ï¸ Complaint not found");
        }
    }
}