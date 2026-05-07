package com.complaint_resolution.complaint.service;

import org.camunda.bpm.engine.delegate.DelegateExecution;
public interface AIService {
    void execute(DelegateExecution execution);
}
