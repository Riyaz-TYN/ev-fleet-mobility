package com.complaint_resolution.notification.service;

import org.camunda.bpm.engine.delegate.DelegateExecution;
public interface NotificationService {
    void execute(DelegateExecution execution);
}
