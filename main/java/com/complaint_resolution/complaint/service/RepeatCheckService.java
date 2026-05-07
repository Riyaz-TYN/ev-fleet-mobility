package com.complaint_resolution.complaint.service;

import com.complaint_resolution.complaint.repository.ComplaintRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
public interface RepeatCheckService {
    void execute(DelegateExecution execution);
}
