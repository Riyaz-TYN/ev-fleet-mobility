# EV Fleet Mobility — Complaint Resolution Enterprise Refactor Guide

This Spring Boot project already has a fully working Complaint Resolution workflow module. :contentReference[oaicite:0]{index=0}

IMPORTANT:
DO NOT rewrite business logic.
DO NOT rewrite workflow logic.
DO NOT break existing functionality.

Everything below is already working:
- Camunda BPM workflow
- AI suggestion flow
- Vendor assignment flow
- Escalation flow
- SLA timers
- Manager review flow
- APIs
- Services
- Repositories
- BPMN delegates
- Workflow execution

The goal is ONLY:
- refactor
- standardize
- restructure
- align architecture with EV Fleet Mobility standards
- reuse existing modules
- standardize APIs
- clean enterprise structure

====================================================
CURRENT PROBLEM
====================================================

The current complaint module structure was originally built like a standalone project.

The complaint module must now be refactored to follow the SAME enterprise standards as:
- useronboarding
- common
- centralized security architecture

====================================================
MAIN OBJECTIVE
====================================================

Convert the existing complaintresolution module into a proper enterprise EV Fleet Mobility module.

IMPORTANT:
Keep all existing working services and workflow logic unchanged.

ONLY:
- restructure packages
- rename packages
- standardize APIs
- reuse shared modules
- clean architecture
- align folder structure with useronboarding standards

====================================================
PACKAGE STANDARDIZATION
====================================================

Rename:
Complaint_Resolution
→ complaintresolution

Requirements:
- lowercase package names
- remove underscores
- enterprise naming conventions

Base package:
com.evfleetmobility

====================================================
FINAL EXPECTED STRUCTURE
====================================================

com.evfleetmobility
│
├── common
│   ├── config
│   ├── exception
│   ├── response
│   ├── security
│   └── util
│
├── useronboarding
│   ├── authservices
│   ├── profileservices
│   ├── vehicleservices
│   ├── documentservices
│   └── adminservices
│
└── complaintresolution
│
├── complaintservices
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   ├── service
│   │    └── impl
│   ├── workflow
│   ├── ai
│   ├── vendor
│   ├── escalation
│   ├── notification
│   └── audit
│
└── managerservices
├── controller
├── dto
└── service
└── impl

====================================================
REUSE REQUIREMENTS
====================================================

Reuse:
- common module
- authservices
- profileservices
- vehicleservices
- centralized security
- centralized RBAC
- onboarding entities

DO NOT duplicate:
- JwtUtil
- JwtFilter
- SecurityConfig
- auth entities
- vehicle entities
- profile entities
- exception handlers
- response wrappers

====================================================
WORKFLOW REQUIREMENTS
====================================================

DO NOT modify:
- BPMN workflow
- delegate logic
- workflow variables
- escalation logic
- AI retry logic
- manager review logic
- vendor assignment logic

Ensure:
- delegateExpression works
- BPMN services autowire correctly
- workflow execution remains unchanged
- escalation works
- retries work

====================================================
ENTERPRISE API SECURITY STANDARDIZATION
====================================================

## Objective

The complaintresolution module is already functionally working.

ONLY standardize APIs to align with EV Fleet Mobility enterprise security standards.

====================================================
SECURITY REQUIREMENT
====================================================

Sensitive identifiers must NOT be exposed in URL path variables wherever possible.

Do NOT expose:
- complaintId
- vehicleId
- vendorId
- customerId
- userId
- taskId
- workflowId
- escalationId
- status
- priority

inside URL paths.

Instead:
- move them into secure request body DTOs.

====================================================
CONTROLLERS TO STANDARDIZE
====================================================

Apply secure API restructuring for:
- ComplaintController
- VendorController
- ManagerController
- ManagerDashboardController
- WorkflowController
- AuditLogController

====================================================
API STANDARDIZATION RULES
====================================================

Convert APIs like:

OLD:
GET /complaints/{id}
GET /vehicle/{vehicleId}
PUT /complaints/{complaintId}/approve
POST /workflow/user-response/{taskId}

NEW:
POST /complaints/details
POST /complaints/vehicle
PUT /complaints/approve
POST /workflow/user-response

using DTO request bodies.

====================================================
DTO RULES
====================================================

Create or reuse DTOs ONLY.

DTOs may include:
- complaintId
- vehicleId
- vendorId
- taskId
- status
- priority
- teamName
- managerDecision
- workflow actions

Follow same DTO standards used in:
- useronboarding
- enterprise APIs

====================================================
BACKWARD COMPATIBILITY
====================================================

DO NOT remove old APIs immediately.

Instead:
- mark old APIs as @Deprecated
- keep them temporarily
- introduce secure DTO APIs alongside them

====================================================
CENTRALIZED AUTHENTICATION REQUIREMENT
====================================================

ComplaintController must NOT manually parse JWT.

Remove:
- JwtUtil usage in controller
- HttpServletRequest token parsing
- jwtUtil.extractCustomerId(token)

Reuse:
- AuthContextService
- SecurityContextHolder

Use:
authContextService.getCurrentUserId()

internally as:
- customerId
- driverId
- authenticated identity

Preserve:
- DB schema
- workflow variables
- service signatures
- complaint flow behavior

DO NOT modify:
- JWT structure
- JwtFilter
- authservices token generation

====================================================
RBAC REQUIREMENTS
====================================================

Use centralized RBAC with:
- @PreAuthorize
- centralized JWT validation
- shared security filters

Compatibility mappings:
- DRIVER + USER
- VENDOR + VENDOR_ADMIN
- ADMIN + SUPER_ADMIN

Apply RBAC across:
- ComplaintController
- VendorController
- ManagerController
- ManagerDashboardController
- WorkflowController
- AuditLogController
- ConfigController

====================================================
SPRING BOOT REQUIREMENTS
====================================================

Ensure:
- component scanning works
- repository scanning works
- dependency injection works
- no bean conflicts
- no circular dependencies
- services autowire properly

====================================================
FINAL CLEANUP REQUIREMENTS
====================================================

Verify/remove only if safe:
- ComplaintResolutionApplication
- duplicate standalone remnants
- duplicate security configs
- duplicate application.properties
- nested src folders
- nested target folders

Ensure:
- single enterprise startup entrypoint
- centralized component scanning
- no startup ambiguity

====================================================
FINAL VALIDATION
====================================================

Ensure:
- Maven compile succeeds
- application startup succeeds
- workflow execution unchanged
- no BPMN behavior changes
- no Camunda bean issues
- no runtime RBAC conflicts
- no runtime auth conflicts

====================================================
EXPECTED FINAL OUTCOME
====================================================

The complaintresolution module should become:
- enterprise structured
- production-ready
- secure
- standardized
- modular
- scalable

while preserving:
- existing APIs
- workflow execution
- BPMN behavior
- escalation logic
- AI retry logic
- business functionality