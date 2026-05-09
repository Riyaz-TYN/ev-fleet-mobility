package com.evfleetmobility.complaintresolution.complaint.controller;

import com.evfleetmobility.complaintresolution.complaint.dto.FieldConfigDTO;
import com.evfleetmobility.complaintresolution.complaint.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/config")
@PreAuthorize("hasAnyRole('USER','DRIVER','VENDOR','VENDOR_ADMIN','MANAGER','ADMIN','SUPER_ADMIN')")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @GetMapping
    public List<FieldConfigDTO> getConfig() {
        return configService.getFields();
    }
}