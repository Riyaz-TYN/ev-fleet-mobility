package com.evfleetmobility.complaintresolution.vendor.service;

import org.camunda.bpm.engine.delegate.DelegateExecution;
public interface VendorResolutionService {
    void execute(DelegateExecution execution);
}
