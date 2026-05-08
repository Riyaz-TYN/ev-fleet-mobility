PROJECT CONTEXT — EV Fleet Mobility Complaint Resolution System

Read PROJECT_REFACTOR_GUIDE.md first before making any changes.

==================================================
PROJECT OVERVIEW
================

This is an enterprise EV Fleet Mobility platform built using:

* Spring Boot
* Camunda BPM
* JWT Authentication
* RBAC Security
* Modular Monolith Architecture

Main modules:

* useronboarding
* complaintresolution
* vehicleservices
* authservices

Complaint Resolution workflow is already fully integrated and working.

==================================================
CURRENT WORKING FLOW
====================

Current BPMN:
complaint-workflow.bpmn

Existing workflow stages:

1. Driver submits complaint
2. Complaint saved
3. Camunda workflow starts
4. AIServiceImpl delegate executes
5. AI retry flow
6. Vendor assignment flow
7. Vendor resolution flow
8. SLA escalation flow
9. Manager review flow
10. Final resolution/retry flow

IMPORTANT:
DO NOT CHANGE:

* BPMN flow
* workflow variable names
* escalation logic
* retry logic
* delegateExpression names
* vendor flow
* manager flow

==================================================
CURRENT SECURITY ARCHITECTURE
=============================

Already implemented:

* JWT authentication
* centralized AuthContextService
* RBAC
* DRIVER / USER roles
* VENDOR / VENDOR_ADMIN roles
* ADMIN / SUPER_ADMIN roles

ComplaintController already uses:
authContextService.getCurrentUserId()

DO NOT rewrite security architecture.

==================================================
CURRENT VEHICLE MODULE
======================

Vehicle + Service History already exist inside:

com.evfleetmobility.useronboarding.vehicleservices

Already available:

* Vehicle entity
* ServiceHistory entity
* VehicleRepository
* ServiceHistoryRepository

Vehicle belongs to authenticated user.

IMPORTANT:
Frontend should NOT manually send:

* customerId
* vehicleId
* serviceHistory

Backend must automatically fetch them using:

* JWT auth
* AuthContextService
* VehicleRepository
* ServiceHistoryRepository

==================================================
GOAL
====

We now need REAL AI integration.

Currently:
AIServiceImpl uses MOCK AI response.

We must replace ONLY the mock AI generation with external FastAPI integration.

==================================================
EXTERNAL AI SERVICE
===================

AI service is developed separately by another team using FastAPI.

Our complaintresolution module acts as:
ENTERPRISE ORCHESTRATION LAYER / GATEWAY

We are responsible for:

* collecting complaint context
* collecting vehicle context
* collecting service history
* sending AI request
* receiving AI response
* managing retries
* managing escalation

We are NOT building AI logic ourselves.

==================================================
EXPECTED AI REQUEST FLOW
========================

When driver submits complaint:

1. Complaint saved
2. Workflow starts
3. AIServiceImpl executes
4. Backend fetches authenticated user
5. Backend fetches user's vehicle
6. Backend fetches service history
7. Backend builds AI payload
8. Backend sends request to FastAPI AI service
9. AI response received
10. Existing workflow continues unchanged

==================================================
EXPECTED AI REQUEST PAYLOAD
===========================

Payload structure can evolve.

Current example:

{
"vehicleId": "EV-7789",
"vehicleModel": "Tata Nexon EV",
"title": "Vehicle not starting",
"description": "The vehicle does not start after charging overnight",
"issueType": "STARTING_ISSUE",
"priority": "HIGH",
"attachments": ["image1.png"],
"serviceHistory": [
{
"serviceDate": "2024-01-15",
"serviceType": "REPAIR",
"description": "Replaced charging port"
}
]
}

IMPORTANT:
Frontend will NOT send vehicleId/serviceHistory manually.

Backend auto-builds payload.

AI team can later change payload keys internally.

==================================================
AI RETRY FLOW
=============

Current retry logic already exists in BPMN.

DO NOT CHANGE IT.

Current behavior:

* AI attempts up to 3 times
* User may continue AI retry
* If unresolved after retry limit:
  → vendor assignment

This flow must remain unchanged.

==================================================
CONVERSATIONAL AI CONTEXT
=========================

On AI retry:
system should also send:

* previous AI suggestion
* previous user feedback
* complaint context
* vehicle context
* service history
* aiAttemptCount

This creates enterprise conversational AI support behavior.

==================================================
NEW AI MODULE REQUIRED
======================

Create new package:

complaintresolution/aiservices

Inside it create:

* dto
* service
* service/impl

Expected components:

* AIRequestDTO
* AIResponseDTO
* AIIntegrationService
* AIIntegrationServiceImpl

==================================================
INTEGRATION REQUIREMENTS
========================

Use:

* WebClient
  NOT:
* RestTemplate

Use:
ai.service.url

from application.properties

DO NOT hardcode URLs.

==================================================
DATABASE CONTEXT FROM AI TEAM
=============================

AI team may separately maintain:

queries table
responses table

inside AI service.

Our service does NOT manage those tables.

We only send request payload and receive AI response.

==================================================
REPOSITORY CHANGE REQUIRED
==========================

VehicleRepository currently only supports:
findById()

Add:

Optional<Vehicle> findByUserId(Long userId);

ONLY this repository change is needed.

==================================================
IMPORTANT RESTRICTIONS
======================

DO NOT rewrite:

* BPMN XML
* workflow variables
* retry logic
* escalation logic
* controllers
* repositories unrelated to AI
* DB schema
* RBAC
* vendor logic
* manager logic
* Camunda gateways

==================================================
FINAL EXPECTED RESULT
=====================

Driver submits complaint
↓
Complaint saved
↓
Workflow starts
↓
AIServiceImpl executes
↓
Fetch authenticated user
↓
Fetch vehicle
↓
Fetch service history
↓
Build AI payload
↓
Send request to FastAPI AI service
↓
Receive AI response
↓
Continue retry/escalation workflow unchanged

==================================================
WHAT IS EXPECTED FROM YOU
=========================

1. Analyze existing codebase
2. Analyze complaint-workflow.bpmn
3. Reuse existing modules properly
4. Implement enterprise AI integration layer
5. Preserve workflow behavior completely
6. Ensure Maven compile succeeds
7. Avoid bean conflicts/circular dependencies

Report only:

* files created
* files modified
* AI integration flow implemented
* repository updates
* remaining risks if any
