package com.evfleetmobility.complaintresolution.manager.service;


public interface ManagerService {
    String managerDecision(Long complaintId, String decision, String remarks);
}
