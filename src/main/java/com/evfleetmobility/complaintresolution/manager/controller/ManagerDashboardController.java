package com.evfleetmobility.complaintresolution.manager.controller;

// All manager dashboard endpoints have been moved to:
//   ComplaintController @ /api/complaints
//
// Endpoint migration:
//   GET  /api/manager/dashboard/complaints      ->  GET /api/complaints          (RBAC: MANAGER gets escalated)
//   PUT  /api/manager/complaints/approve        ->  PUT /api/complaints/assign
//   PUT  /api/manager/complaints/reject         ->  PUT /api/complaints/reject
//
// Note: @CrossOrigin removed — CORS is handled globally in SecurityConfig via CorsConfigurationSource