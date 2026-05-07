package com.evfleetmobility.complaintresolution.manager.service;

import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.complaint.repository.ComplaintRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
public interface ManagerDecisionService {
    void execute(DelegateExecution execution);
}
