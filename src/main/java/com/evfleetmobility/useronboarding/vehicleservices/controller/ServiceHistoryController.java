package com.evfleetmobility.useronboarding.vehicleservices.controller;

import com.evfleetmobility.common.response.ApiResponse;
import com.evfleetmobility.useronboarding.vehicleservices.dto.ServiceHistoryRequest;
import com.evfleetmobility.useronboarding.vehicleservices.dto.ServiceHistoryResponse;
import com.evfleetmobility.useronboarding.vehicleservices.service.ServiceHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/service-history")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ServiceHistoryController {

    private final ServiceHistoryService serviceHistoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<ServiceHistoryResponse>> addServiceEntry(@RequestBody ServiceHistoryRequest request) {
        return ResponseEntity.ok(new ApiResponse<>("Service entry added successfully", serviceHistoryService.addServiceEntry(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceHistoryResponse>> updateServiceEntry(@PathVariable Long id, @RequestBody ServiceHistoryRequest request) {
        return ResponseEntity.ok(new ApiResponse<>("Service entry updated successfully", serviceHistoryService.updateServiceEntry(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteServiceEntry(@PathVariable Long id) {
        serviceHistoryService.deleteServiceEntry(id);
        return ResponseEntity.ok(new ApiResponse<>("Service entry deleted successfully", null));
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<ServiceHistoryResponse>>> getHistoryByVehicleId(
            @PathVariable Long vehicleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new ApiResponse<>("Service history fetched successfully", serviceHistoryService.getHistoryByVehicleId(vehicleId, page, size)));
    }

    @GetMapping("/vehicle/{vehicleId}/cost")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalMaintenanceCost(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(new ApiResponse<>("Total cost fetched successfully", serviceHistoryService.getTotalMaintenanceCost(vehicleId)));
    }

    @GetMapping("/vehicle/{vehicleId}/odometer")
    public ResponseEntity<ApiResponse<Long>> getLatestOdometer(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(new ApiResponse<>("Latest odometer fetched successfully", serviceHistoryService.getLatestOdometer(vehicleId)));
    }
}
