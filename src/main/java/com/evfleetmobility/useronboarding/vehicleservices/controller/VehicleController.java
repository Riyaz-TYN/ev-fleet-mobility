package com.evfleetmobility.useronboarding.vehicleservices.controller;

import com.evfleetmobility.common.response.ApiResponse;
import com.evfleetmobility.useronboarding.vehicleservices.dto.VehicleRequest;
import com.evfleetmobility.useronboarding.vehicleservices.dto.VehicleResponse;
import com.evfleetmobility.useronboarding.vehicleservices.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<VehicleResponse>> addVehicle(@RequestBody VehicleRequest request) {
        return ResponseEntity.ok(new ApiResponse<>("Vehicle added successfully", vehicleService.addVehicle(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<VehicleResponse>> updateVehicle(@PathVariable Long id,
            @RequestBody VehicleRequest request) {
        return ResponseEntity
                .ok(new ApiResponse<>("Vehicle updated successfully", vehicleService.updateVehicle(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.ok(new ApiResponse<>("Vehicle deleted successfully", null));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DRIVER', 'MANAGER', 'VENDOR_ADMIN')")
    public ResponseEntity<ApiResponse<VehicleResponse>> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>("Vehicle fetched successfully", vehicleService.getVehicleById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DRIVER', 'MANAGER', 'VENDOR_ADMIN')")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<VehicleResponse>>> getAllVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new ApiResponse<>("Vehicles fetched successfully", vehicleService.getAllVehicles(page, size)));
    }
    

}



