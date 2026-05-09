package com.evfleetmobility.complaintresolution.complaint.service.impl;

import com.evfleetmobility.complaintresolution.complaint.service.UserResolutionService;
import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.complaint.repository.ComplaintRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("userResolutionService")  
public class UserResolutionServiceImpl implements UserResolutionService, JavaDelegate {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Override
    public void execute(DelegateExecution execution) {

        System.out.println(" User resolution service running...");

        Long complaintId = (Long) execution.getVariable("complaintId");

        if (complaintId == null) {
            System.out.println("âš ï¸ complaintId is null");
            return;
        }

        Complaint complaint = complaintRepository.findById(complaintId).orElse(null);

        if (complaint != null) {
            complaint.setStatus("RESOLVED");
            complaintRepository.save(complaint);

            System.out.println("âœ… User resolved â†’ DB updated");
        } else {
            System.out.println("âš ï¸ Complaint not found");
        }
    }
}