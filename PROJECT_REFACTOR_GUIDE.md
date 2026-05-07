This Spring Boot project already has a fully working Complaint Resolution workflow module.

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

Current structure looks like:

complaint_resolution
│
├── auditlog
├── auth
├── common
├── complaint
├── escalation
├── manager
├── notification
└── vendor

This structure is NOT aligned with the existing EV Fleet Mobility architecture.

The existing EV Fleet Mobility modules such as:

com.evfleetmobility.useronboarding

already follow a clean enterprise modular structure.

The complaint module must now be refactored to follow the SAME structure and standards as useronboarding.

====================================================
MAIN OBJECTIVE
====================================================

Convert the existing complaint_resolution standalone-style module into a proper enterprise EV Fleet Mobility module.

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
- all package names lowercase
- remove underscores
- use enterprise naming conventions

Base package:

com.evfleetmobility

====================================================
IMPORTANT STRUCTURE REQUIREMENT
====================================================

The current structure:

auditlog
auth
common
complaint
vendor
manager

must NOT remain as separate standalone-style modules.

The structure should become modular and aligned with:

useronboarding/authservices
useronboarding/profileservices
useronboarding/vehicleservices

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
    │   │
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
VERY IMPORTANT REFACTOR RULES
====================================================

1. DO NOT create separate:
   - auth module
   - common module
   - vendorservices module

2. Vendor functionality belongs INSIDE:
   complaintservices/vendor

3. complaintresolution must reuse:
   - common module
   - authservices
   - vehicle services
   - onboarding services

4. DO NOT duplicate:
   - JWT classes
   - security configs
   - auth entities
   - response wrappers
   - exception handlers

====================================================
DO NOT MODIFY WORKING BUSINESS LOGIC
====================================================

Keep existing:
- services
- repositories
- BPMN delegates
- workflow logic
- escalation logic
- entity relationships
- API functionality

ONLY:
- restructure
- standardize
- rename packages
- fix imports
- clean architecture

====================================================
AUTHENTICATION & AUTHORIZATION
====================================================

Reuse existing:

com.evfleetmobility.useronboarding.authservices

Reuse:
- JWT generation
- JWT validation
- login flow
- authorization flow
- RBAC handling
- token extraction

Also reuse:

com.evfleetmobility.common.security

DO NOT create duplicate:
- JwtUtil
- JwtFilter
- SecurityConfig
- auth controllers
- auth entities

====================================================
RBAC REQUIREMENTS
====================================================

Use centralized RBAC.

Roles:
- CUSTOMER
- VENDOR
- MANAGER
- ADMIN

Use:
- @PreAuthorize
- centralized JWT validation
- shared security filters
- role-based endpoint access

====================================================
VEHICLE DETAILS REUSE
====================================================

Reuse existing vehicle data from:

com.evfleetmobility.useronboarding.vehicleservices

DO NOT create:
- duplicate vehicle tables
- duplicate vehicle entities

Complaint module should use:
- existing vehicle entities
- vehicle IDs
- vehicle ownership mapping
- registration details
- EV fleet data

====================================================
USER DATA REUSE
====================================================

Reuse:
- authservices
- profileservices

Use existing:
- user entities
- profiles
- authentication data
- manager data
- vendor mappings
- RBAC roles

DO NOT duplicate:
- user tables
- auth tables
- profile entities

====================================================
COMMON MODULE REUSE
====================================================

Reuse everything from:

com.evfleetmobility.common

Use:
- configs
- security
- response wrappers
- exception handling
- utilities
- validations

DO NOT duplicate:
- configs
- JwtUtil
- JwtFilter
- SecurityConfig
- response classes
- exception handlers

====================================================
API STANDARDIZATION REQUIREMENT
====================================================

Read and analyze APIs inside:

com.evfleetmobility.useronboarding

Especially:
- controller patterns
- DTO naming
- request structure
- response structure
- validation style
- exception handling
- response wrappers
- service layering
- RBAC implementation

Then standardize ONLY complaint APIs to follow SAME standards.

IMPORTANT:
DO NOT rewrite business logic.

ONLY:
- standardize API structure
- standardize controller style
- standardize DTO naming
- standardize response handling
- standardize validations

Complaint APIs should look exactly like EV Fleet Mobility native APIs.

====================================================
CAMUNDA BPM REQUIREMENTS
====================================================

The existing BPMN workflow is already functional.

DO NOT rewrite workflow logic.

Workflow includes:
- AI suggestion
- AI retry logic
- Vendor assignment
- Vendor resolution
- SLA timer
- Escalation
- Manager review
- Final resolution

Ensure after refactor:
- delegateExpression works
- BPMN services autowire correctly
- workflow variables work correctly
- escalation works
- SLA timers work
- AI retry works

====================================================
CODE CLEANUP REQUIREMENTS
====================================================

Remove:
- duplicate auth module
- duplicate common module
- duplicate configs
- duplicate JWT/security logic
- standalone remnants

Also remove:
- nested src folders
- nested target folders
- duplicate application.properties
- duplicate SpringBootApplication classes
- unused configs/files

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
LAYERED ARCHITECTURE
====================================================

Maintain strict enterprise layering:

- controller
- dto
- entity
- repository
- service
- service.impl

Controllers:
- request/response only

Services:
- business logic only

Repositories:
- DB access only

DTOs:
- API communication only

Entities:
- JPA persistence models only

====================================================
IMPORTANT EXECUTION RULE
====================================================

Perform SAFE incremental refactoring.

After each refactor:
- fix imports
- ensure project compiles
- preserve workflow execution
- preserve existing APIs

DO NOT perform large uncontrolled rewrites.

====================================================
EXPECTED FINAL OUTCOME
====================================================

The complaintresolution module should become a proper native EV Fleet Mobility enterprise module.

Final result should include:
- clean modular architecture
- lowercase standardized packages
- centralized security
- centralized RBAC
- reuse onboarding services
- reuse vehicle services
- reuse authservices
- reuse common configs
- BPMN workflow fully integrated
- SLA escalation working
- AI retry flow working
- no duplicate configs
- no standalone remnants
- maintainable scalable architecture
- production-ready codebase
- APIs aligned with useronboarding standards
- existing workflow/business logic preserved completely