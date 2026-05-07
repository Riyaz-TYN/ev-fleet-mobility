package com.evfleetmobility.complaintresolution.complaint.service;

import com.evfleetmobility.complaintresolution.complaint.dto.FieldConfigDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public interface ConfigService {
    List<FieldConfigDTO> getFields();
}
