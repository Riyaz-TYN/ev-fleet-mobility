# EV Fleet Mobility System

## Overview
EV Fleet Mobility is an enterprise-grade backend system designed to manage electric vehicle fleets, vendor operations, driver onboarding, and automated complaint resolution. The application is built using a **Modular Monolith** architecture on **Spring Boot** with **Java 21**, ensuring clear boundaries between domains while maintaining the operational simplicity of a single deployment unit.

## Architecture

The system embraces a **Domain-Driven Design (DDD)** approach within a modular monolith structure. The codebase is primarily divided into three main modules:

1. **Common Module (`com.evfleetmobility.common`)**
2. **User Onboarding Module (`com.evfleetmobility.useronboarding`)**
3. **Complaint Resolution Module (`com.evfleetmobility.complaintresolution`)**

### 1. Common Module
This module acts as the shared foundation for all other modules. It enforces application-wide standards:
- **Security:** Stateless JWT authentication and authorization (`JwtFilter`, `JwtUtil`, `SecurityConfig`).
- **Standardized Responses:** Generic API wrapper `ApiResponse<T>` and `ErrorResponse`.
- **Exception Handling:** A centralized `@RestControllerAdvice` (`GlobalExceptionHandler`) to translate custom exceptions (e.g., `UserNotFoundException`, `TokenException`) into uniform HTTP responses.
- **External Integrations:** AWS S3 configuration (`S3Config`) for document storage.

### 2. User Onboarding Module
Handles authentication, user hierarchy, vendor management, and vehicle assignments. It is subdivided into bounded contexts:
- **Auth Services (`authservices`)**: User registration, login, and JWT generation. Differentiates between `INDIVIDUAL` (drivers/employees) and `ORGANIZATION` (vendors/companies).
- **Profile Services (`profileservices`)**: Manages `IndividualDetails` and `OrganizationDetails`. Handles profile completion.
- **Document Services (`documentservices`)**: Integrates with AWS S3 for secure document upload and retrieval using presigned URLs.
- **Admin Services (`adminservices`)**: Provides unified APIs for `SUPER_ADMIN`, `ADMIN`, and `VENDOR_ADMIN` to list users and handle multi-tiered approval workflows.
- **Vehicle Services (`vehicleservices`)**: Manages the EV fleet (`Vehicle`) and service logs (`ServiceHistory`).

### 3. Complaint Resolution Module
A robust, workflow-driven module handling the end-to-end lifecycle of complaints, integrated with **Camunda Workflow Engine** and **AI Services**.
- **Complaint Management (`complaint`)**: Core CRUD operations, filtering, and assignment of complaints.
- **AI Services (`aiservices`)**: Automates initial complaint assessment, categorizing issues, and generating automated resolution recommendations.
- **Vendor Management (`vendor`)**: Manages vendor expertise, availability, and assignment of complaints to specific vendors.
- **Manager Dashboard (`manager`)**: Provides high-level overviews, manual override capabilities, and escalation handling.
- **Audit & Notifications (`auditlog`, `notification`)**: Maintains an immutable trail of actions (`AuditLog`) and triggers system notifications via Camunda delegates.

## Technology Stack (from `pom.xml`)
- **Core Framework:** Java 21, Spring Boot 3.4.0 (Web, Actuator, Validation, Test)
- **Persistence:** Spring Data JPA, Hibernate, PostgreSQL
- **Security:** Spring Security, Spring Dotenv (4.0.0), JJWT (0.12.5) for JSON Web Tokens
- **Workflow Engine:** Camunda BPM Engine & Webapp (7.21.0)
- **Cloud/Storage:** AWS S3 SDK (2.25.16)
- **AI & OCR Integration:** Spring WebFlux (WebClient) for AI APIs, Google Cloud Vision (3.53.0) for OCR
- **Utilities:** Lombok, Jackson Databind/JSR310

## Environment Configuration
The application requires an `.env` file at the root directory. Below are the required keys (values omitted for security):

```properties
DB_URL=
DB_USERNAME=
DB_PASSWORD=

JWT_SECRET=
JWT_ACCESS_EXPIRY=
JWT_REFRESH_EXPIRY=

AWS_ACCESS_KEY_ID=
AWS_SECRET_ACCESS_KEY=
AWS_REGION=
AWS_S3_BUCKET_NAME=
```

## System Flow & Roles

### User Roles & Hierarchy
- **SUPER_ADMIN / ADMIN:** Platform administrators with global visibility. They approve Vendor Organizations.
- **VENDOR_ADMIN:** Organization administrators. They manage their own fleet and approve their drivers/employees.
- **INDIVIDUAL (Drivers/Employees):** Standard users who operate the vehicles.

### Typical Flows

**1. Onboarding & Approval Flow**
1. An `ORGANIZATION` signs up. Their status is `PENDING`.
2. Platform `ADMIN` reviews organization documents via `AdminServices` and approves them.
3. An `INDIVIDUAL` (driver) signs up and links to the approved Organization.
4. The `VENDOR_ADMIN` of that organization approves the driver.
5. The `ADMIN` or `VENDOR_ADMIN` assigns a `Vehicle` to the approved driver.

**2. Complaint Resolution Workflow**
1. A driver submits a `ComplaintRequestDTO` through the Complaint API.
2. The **AI Service** analyzes the complaint data, categorizes the issue, and may suggest immediate resolution steps.
3. If human intervention is required, the workflow engine (Camunda) routes the task.
4. Based on the issue category, a suitable **Vendor** is selected (matching location and expertise).
5. The Vendor receives the complaint, works on it, and marks it resolved.
6. **Managers** monitor the dashboard for escalated tasks or SLA breaches.
7. Every state transition (e.g., `OPEN` -> `ASSIGNED` -> `RESOLVED`) is recorded in the **AuditLog**.

## Package Naming Conventions
The project strictly enforces layering within each bounded context:
- `.controller` - REST endpoints and API definition
- `.dto` - Request/Response objects (No entities exposed)
- `.entity` - JPA Models and Enums
- `.repository` - Spring Data JPA Interfaces
- `.service` - Interfaces defining business logic
- `.service.impl` - Concrete implementations with `@Service` and `@Transactional`

---

## Enumerators

### User Onboarding Module

#### `UserType` — `authservices.entity`
Differentiates between the two account registration paths.

| Value | Description |
|---|---|
| `INDIVIDUAL` | A driver or employee. Links to `IndividualDetails`. |
| `ORGANIZATION` | A vendor/company account. Links to `OrganizationDetails`. |

---

#### `ApprovalStatus` — `authservices.entity`
Used on both the `User` entity (platform-level approval) and `IndividualDetails.companyApprovalStatus` (organization-level approval). Also applied to `OrganizationDetails`.

| Value | Description |
|---|---|
| `PENDING` | Default state after signup. Access is restricted. |
| `APPROVED` | Admin/Vendor Admin has approved the account. |
| `REJECTED` | Account has been rejected and cannot proceed. |

---

#### `DocumentType` — `documentservices.entity`
Categorizes the type of identity/compliance document uploaded by the user to AWS S3.

| Value | Description |
|---|---|
| `PAN` | Permanent Account Number card (individual tax ID). |
| `GSTIN` | Goods and Services Tax Identification Number (organization). |
| `AADHAR` | National identity document for individuals. |
| `OTHER` | Any other supporting document. |

---

#### `DocumentStatus` — `documentservices.entity`
Tracks the verification state of an uploaded document.

| Value | Description |
|---|---|
| `PENDING` | Document uploaded, awaiting review. |
| `APPROVED` | Document verified and accepted. |
| `REJECTED` | Document rejected; re-upload required. |

---

#### `DocumentStage` — `documentservices.entity`
Represents the processing pipeline stage of a document.

| Value | Description |
|---|---|
| `UPLOADED` | Document successfully stored in S3. |
| `UNDER_REVIEW` | Document is being reviewed by an admin. |
| `VERIFIED` | Document has passed verification checks. |
| `REJECTED` | Document failed verification. |

---

#### `VehicleStatus` — `vehicleservices.entity`
Tracks the operational lifecycle of a vehicle in the fleet.

| Value | Description |
|---|---|
| `AVAILABLE` | Vehicle is registered and ready to be assigned. |
| `ACTIVE` | Vehicle is currently assigned to a driver and in use. |
| `INACTIVE` | Vehicle is not in use but not decommissioned. |
| `UNDER_MAINTENANCE` | Vehicle is undergoing service (see `ServiceType`). |
| `DECOMMISSIONED` | Vehicle has been permanently retired from the fleet. |

---

#### `ServiceType` — `vehicleservices.entity`
Categorizes the type of maintenance or service work logged in `ServiceHistory`.

| Value | Description |
|---|---|
| `ROUTINE_MAINTENANCE` | Scheduled standard upkeep. |
| `BATTERY_SERVICE` | Battery replacement or full service. |
| `BATTERY_CHECK` | Diagnostic check of the battery health. |
| `TIRE_REPLACEMENT` | One or more tires replaced. |
| `BRAKE_SERVICE` | Brake inspection or repair. |
| `SOFTWARE_UPDATE` | Firmware or software update applied to vehicle. |
| `INSPECTION` | General vehicle inspection. |
| `REPAIR` | Ad-hoc repair work. |
| `EMERGENCY_SERVICE` | Urgent, unplanned intervention. |
| `OTHER` | Any other type of service. |

---

### Complaint Resolution Module

#### Complaint `status` — `complaintresolution.complaint.entity`
The `Complaint` entity uses a `String` field for status, with values set by the service layer throughout the Camunda workflow lifecycle.

| Value | Set By | Description |
|---|---|---|
| `OPEN` | System (constructor) | Initial state when a complaint is created. |
| `IN_PROGRESS` | Camunda Workflow Engine | Complaint has been picked up by the workflow process. |
| `AI_PROCESSED` | AI Service | AI has completed its initial assessment. |
| `ASSIGNED_TO_VENDOR` | `VendorServiceImpl` / Manager | Complaint has been assigned to a specific vendor organization. |
| `ESCALATED_TO_MANAGER` | `VendorServiceImpl` | Vendor could not resolve OR no vendor was available. |
| `RESOLVED` | `VendorServiceImpl` / `ManagerServiceImpl` | Complaint has been successfully resolved. |
| `REJECTED` | `ManagerDashboardServiceImpl` / `ManagerServiceImpl` | Manager rejected the complaint as invalid. |
| `RETRY_VENDOR` | `ManagerServiceImpl` | Manager decided to retry with a different vendor. |

---

#### Manager Decision Values — `ManagerServiceImpl`
When a manager acts on an escalated complaint, the `managerDecision` variable routes the Camunda workflow.

| Decision Value | Resulting Status | Description |
|---|---|---|
| `RESOLVE` | `RESOLVED` | Manager directly closes the complaint as resolved. |
| `REJECT` | `REJECTED` | Manager invalidates the complaint. |
| `RETRY` | `RETRY_VENDOR` | Manager re-routes complaint to a vendor (auto or manually assigned). |

---

## State Transition Diagrams

### 1. User / Organization Approval State Machine
```
[Signup] ──► PENDING ──► APPROVED
                    └──► REJECTED
```
- **PENDING** is the initial state for all new `User` and `OrganizationDetails` records.
- **APPROVED** is set by `ADMIN`/`SUPER_ADMIN` for organizations, or `VENDOR_ADMIN` for individuals.
- **REJECTED** can be set by `ADMIN`/`SUPER_ADMIN`/`VENDOR_ADMIN` at any time.
- Note: `IndividualDetails.companyApprovalStatus` tracks a second, separate approval by the employing organization's `VENDOR_ADMIN`.

---

### 2. Document Processing State Machine
```
[Upload] ──► UPLOADED ──► UNDER_REVIEW ──► VERIFIED
                                      └──► REJECTED ──► [Re-upload]
```
- **UPLOADED** is set immediately after a successful S3 upload.
- **UNDER_REVIEW** is set when an admin begins reviewing the document.
- **VERIFIED** is the final positive state; maps to `DocumentStatus.APPROVED`.
- **REJECTED** triggers the user to upload a new, corrected document.

---

### 3. Vehicle Status State Machine
```
[Registered] ──► AVAILABLE ──► ACTIVE ──► UNDER_MAINTENANCE ──► ACTIVE
                    │                └──► INACTIVE ──────────────► AVAILABLE
                    └──────────────────────────────────────────► DECOMMISSIONED
```

---

### 4. Complaint Lifecycle State Machine (Camunda Workflow)
```
[Driver Submits] ──► OPEN ──► IN_PROGRESS (Camunda started)
                                    │
                                    ▼
                              AI Assessment
                                    │
                         ┌──────────┴──────────┐
                    AI Resolved?             AI Unresolved
                         │                       │
                         ▼                       ▼
                      RESOLVED           AI_PROCESSED
                                               │
                                     Auto Vendor Assignment
                                        (by location + expertise + rating)
                                               │
                                    ┌──────────┴──────────┐
                               No Vendor Available    Vendor Found
                                    │                     │
                                    ▼                     ▼
                          ESCALATED_TO_MANAGER   ASSIGNED_TO_VENDOR
                                    ▲                     │
                                    │             Vendor Works on It
                                    │                     │
                                    │         ┌───────────┴───────────┐
                                    │    Vendor Resolved?       Vendor Unresolved
                                    │         │                       │
                                    │         ▼                       ▼
                                    │      RESOLVED          ESCALATED_TO_MANAGER
                                    │                                │
                                    └────────────────────────────────┘
                                                                     │
                                                          Manager Reviews
                                                                     │
                                           ┌─────────────────────────┼──────────────────────┐
                                      RESOLVE                      RETRY                  REJECT
                                           │                         │                      │
                                           ▼                         ▼                      ▼
                                       RESOLVED              RETRY_VENDOR ──► ASSIGNED_TO_VENDOR
                                                                                    (restart vendor flow)
```
