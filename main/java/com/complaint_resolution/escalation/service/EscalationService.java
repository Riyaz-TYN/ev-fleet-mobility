package com.complaint_resolution.escalation.service;

import org.camunda.bpm.engine.delegate.DelegateExecution;

public interface EscalationService {
    void execute(DelegateExecution execution);
}
