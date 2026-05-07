package com.complaint_resolution.vendor.service;

import com.complaint_resolution.complaint.entity.Complaint;
import com.complaint_resolution.complaint.repository.ComplaintRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
public interface VendorResolutionService {
    void execute(DelegateExecution execution);
}
