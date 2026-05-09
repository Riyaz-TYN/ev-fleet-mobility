package com.evfleetmobility.complaintresolution.manager.dto;

import lombok.Data;

@Data
public class ManagerApproveRequestDTO {
    private Long complaintId;
    private Long vendorId;
}
