package com.evfleetmobility.complaintresolution.complaint.service;

import org.camunda.bpm.engine.delegate.DelegateExecution;
public interface AIService {
    void execute(DelegateExecution execution);
}
