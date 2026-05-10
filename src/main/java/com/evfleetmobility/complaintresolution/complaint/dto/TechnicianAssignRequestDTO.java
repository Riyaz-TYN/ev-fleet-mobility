package com.evfleetmobility.complaintresolution.complaint.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TechnicianAssignRequestDTO {
    private Long complaintId;
    private Long technicianId;
}
