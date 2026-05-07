package com.complaint_resolution.complaint.controller;

import com.complaint_resolution.complaint.dto.FieldConfigDTO;
import com.complaint_resolution.complaint.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller to expose API for frontend
 */
@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    /**
     * GET API to fetch form fields
     * URL: http://localhost:8080/api/config
     */
    @GetMapping
    public List<FieldConfigDTO> getConfig() {
        return configService.getFields();
    }
}