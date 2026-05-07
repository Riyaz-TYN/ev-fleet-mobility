package com.complaint_resolution.manager.service;

import com.complaint_resolution.complaint.entity.Complaint;
import com.complaint_resolution.complaint.repository.ComplaintRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
public interface ManagerDecisionService {
    void execute(DelegateExecution execution);
}
