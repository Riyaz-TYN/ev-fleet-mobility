package com.evfleetmobility.complaintresolution.complaint.service;

import org.camunda.bpm.engine.delegate.DelegateExecution;
public interface UserResolutionService {
    void execute(DelegateExecution execution);
}
