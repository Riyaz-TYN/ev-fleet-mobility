package com.evfleetmobility.complaintresolution.vendor.service;

import com.evfleetmobility.complaintresolution.complaint.entity.Complaint;
import com.evfleetmobility.complaintresolution.complaint.repository.ComplaintRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
public interface VendorResolutionService {
    void execute(DelegateExecution execution);
}
