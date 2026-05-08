package com.evfleetmobility.complaintresolution.manager.controller;

// All complaint-related endpoints previously here have been moved to:
//   ComplaintController @ /api/complaints
//
// Endpoint migration:
//   GET  /api/manager/complaints/escalated  ->  GET  /api/complaints          (RBAC: MANAGER gets escalated)
//   PUT  /api/manager/complaints/decision   ->  PUT  /api/complaints/decision
//   GET  /api/manager/complaints/history    ->  removed (use GET /api/complaints with ADMIN role)
//
// This controller is intentionally left minimal.
// If future manager-specific non-complaint operations are needed, add them here.

// Note: @CrossOrigin removed — CORS is handled globally in SecurityConfig via CorsConfigurationSource