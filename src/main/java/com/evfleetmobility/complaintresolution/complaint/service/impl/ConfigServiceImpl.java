package com.evfleetmobility.complaintresolution.complaint.service.impl;

import com.evfleetmobility.complaintresolution.complaint.service.ConfigService;
import com.evfleetmobility.complaintresolution.complaint.dto.FieldConfigDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConfigServiceImpl implements ConfigService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<FieldConfigDTO> getFields() {

        List<FieldConfigDTO> fieldList = new ArrayList<>();

        try {

            InputStream inputStream = getClass()
                    .getResourceAsStream("/config/fields.json");

            if (inputStream == null) {
                throw new RuntimeException("fields.json file not found in resources/config");
            }

            JsonNode rootNode = objectMapper.readTree(inputStream);
            JsonNode fieldsArray = rootNode.get("fields");

            for (JsonNode node : fieldsArray) {

                List<String> optionsList = new ArrayList<>();

                if (node.has("options")) {
                    node.get("options").forEach(opt -> optionsList.add(opt.asText()));
                }

                FieldConfigDTO dto = new FieldConfigDTO(
                        node.get("fieldName").asText(),
                        node.get("label").asText(),
                        node.get("type").asText(),
                        node.get("required").asBoolean(),
                        optionsList
                );

                fieldList.add(dto);
            }

        } catch (Exception e) {

            System.out.println("Error reading JSON config: " + e.getMessage());
            e.printStackTrace();

            throw new RuntimeException("Failed to load field configuration");
        }

        return fieldList;
    }
}