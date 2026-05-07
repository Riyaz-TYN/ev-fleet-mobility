package com.complaint_resolution.complaint.service;

import com.complaint_resolution.complaint.dto.FieldConfigDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public interface ConfigService {
    List<FieldConfigDTO> getFields();
}
