package com.evfleetmobility.complaintresolution.complaint.service;

import com.evfleetmobility.complaintresolution.complaint.dto.FieldConfigDTO;
import java.util.List;

public interface ConfigService {
    List<FieldConfigDTO> getFields();
}
