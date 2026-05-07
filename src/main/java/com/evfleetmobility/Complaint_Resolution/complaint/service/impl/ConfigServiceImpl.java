package com.complaint_resolution.complaint.service.impl;


import com.complaint_resolution.complaint.service.ConfigService;
import com.complaint_resolution.complaint.dto.FieldConfigDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Service layer to read JSON config and convert it into DTO
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Reads fields.json and converts into list of DTOs
     */
    public List<FieldConfigDTO> getFields() {

        List<FieldConfigDTO> fieldList = new ArrayList<>();

        try {
            // Load JSON file from resources/config
            InputStream inputStream = getClass()
                    .getResourceAsStream("/config/fields.json");

            // Debug check (important)
            if (inputStream == null) {
                throw new RuntimeException("fields.json file not found in resources/config");
            }

            // Parse JSON
            JsonNode rootNode = objectMapper.readTree(inputStream);
            JsonNode fieldsArray = rootNode.get("fields");

            // Loop through each field
            for (JsonNode node : fieldsArray) {

                List<String> optionsList = new ArrayList<>();

                // Read options if present
                if (node.has("options")) {
                    node.get("options").forEach(opt -> optionsList.add(opt.asText()));
                }

                // Create DTO object
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
            // Print error clearly in console
            System.out.println("Error reading JSON config: " + e.getMessage());
            e.printStackTrace();

            throw new RuntimeException("Failed to load field configuration");
        }

        return fieldList;
    }
}