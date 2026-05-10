package com.evfleetmobility.complaintresolution.manager.service;

import org.camunda.bpm.engine.delegate.DelegateExecution;
public interface ManagerDecisionService {
    void execute(DelegateExecution execution);
}
