package com.evfleetmobility.complaintresolution.complaint.service;

import com.evfleetmobility.complaintresolution.complaint.repository.ComplaintRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
public interface RepeatCheckService {
    void execute(DelegateExecution execution);
}
