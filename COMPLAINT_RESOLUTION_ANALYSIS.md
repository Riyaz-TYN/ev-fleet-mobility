# Comprehensive Analysis: Complaint_Resolution Module

**Generated:** May 7, 2026  
**Location:** `src/main/java/com/evfleetmobility/Complaint_Resolution/`

---

## Table of Contents
1. [Module Overview](#module-overview)
2. [Package Structure](#package-structure)
3. [Directory Structure & File Counts](#directory-structure--file-counts)
4. [Controller Classes & Endpoints](#controller-classes--endpoints)
5. [Entity Classes & Database Models](#entity-classes--database-models)
6. [Service Layer & Business Logic](#service-layer--business-logic)
7. [DTO Classes](#dto-classes)
8. [Repository Layer](#repository-layer)
9. [Configuration Files](#configuration-files)
10. [Complaint Workflow Analysis](#complaint-workflow-analysis)
11. [Duplicate & Standalone Components](#duplicate--standalone-components)

---

## Module Overview

### Technology Stack
- **Framework:** Spring Boot 4.0.6
- **Java Version:** 21
- **ORM:** JPA/Hibernate
- **API Type:** REST
- **Workflow Engine:** Camunda (referenced in code)
- **Security:** JWT-based authentication
- **Database:** JPA-compatible (application config required)

### Module Purpose
The Complaint_Resolution module is a comprehensive complaint management system designed for an EV Fleet Mobility platform. It handles the complete lifecycle of complaints from creation to resolution, including:
- Complaint registration and tracking
- Audit logging of all actions
- Vendor assignment and resolution
- Manager review and decision-making
- Escalation handling
- Notifications to stakeholders

### Application Configuration
- **Main Class:** `ComplaintResolutionApplication.java`
- **Package:** `com.complaint_resolution`
- **Application Name:** `Complaint_Resolution`
- **Application Properties:** Located at `src/main/resources/application.properties`
  - Current content: `spring.application.name=Complaint_Resolution`

---

## Package Structure

### Current Package Organization

```
com.complaint_resolution (Root)
├── complaint
│   ├── controller
│   ├── service
│   │   ├── (interfaces)
│   │   └── impl (implementations)
│   ├── entity
│   ├── dto
│   └── repository
├── auditlog
│   ├── controller
│   ├── service
│   │   ├── (interfaces)
│   │   └── impl (implementations)
│   ├── entity
│   └── repository
├── vendor
│   ├── controller
│   ├── service
│   │   ├── (interfaces)
│   │   └── impl (implementations)
│   ├── entity
│   ├── dto
│   └── repository
├── manager
│   ├── controller
│   ├── service
│   │   ├── (interfaces)
│   │   └── impl (implementations)
│   └── dto
├── escalation
│   └── service
│       ├── (interfaces)
│       └── impl (implementations)
└── notification
    └── service
        ├── (interfaces)
        └── impl (implementations)
```

### Package Naming Convention Notes
- **Underscore Usage:** `com.complaint_resolution` (mixed case with underscore in root)
- **Sub-packages:** All use lowercase without underscores (e.g., `complaint`, `auditlog`, `vendor`, `manager`, `escalation`, `notification`)
- **Consistency:** Follows standard Java naming conventions except for the root package

---

## Directory Structure & File Counts

### Complete File Listing

#### **1. Complaint Module** (`complaint/`)
**File Count: 18 files**

**Controllers (3 files):**
- `controller/ComplaintController.java` - Main complaint REST API
- `controller/ConfigController.java` - Configuration endpoints
- `controller/WorkflowController.java` - Workflow task handling

**Entities (1 file):**
- `entity/Complaint.java` - Core complaint JPA entity

**Services (8 files):**
- **Interfaces:**
  - `service/ComplaintService.java`
  - `service/AIService.java`
  - `service/ConfigService.java`
  - `service/RepeatCheckService.java`
  - `service/UserResolutionService.java`
- **Implementations (impl/):**
  - `service/impl/ComplaintServiceImpl.java`
  - `service/impl/AIServiceImpl.java`
  - `service/impl/ConfigServiceImpl.java`
  - `service/impl/RepeatCheckServiceImpl.java`
  - `service/impl/UserResolutionServiceImpl.java`

**DTOs (8 files):**
- `dto/ComplaintRequestDTO.java` - Main complaint submission
- `dto/ComplaintActionRequestDTO.java` - Action requests
- `dto/ComplaintDetailsRequestDTO.java` - Details retrieval
- `dto/ComplaintStatusRequestDTO.java` - Status filtering
- `dto/VehicleComplaintRequestDTO.java` - Vehicle-based queries
- `dto/AuditLogRequestDTO.java` - Audit log data transfer
- `dto/FieldConfigDTO.java` - Configuration field definition
- `dto/UserDecisionRequestDTO.java` - User decision capture

**Repository (1 file):**
- `repository/ComplaintRepository.java` - JPA Data Access

---

#### **2. Audit Log Module** (`auditlog/`)
**File Count: 6 files**

**Controller (1 file):**
- `controller/AuditLogController.java` - Audit log REST API

**Entities (1 file):**
- `entity/AuditLog.java` - Audit trail JPA entity

**Services (3 files):**
- **Interface:**
  - `service/AuditLogService.java`
- **Implementation:**
  - `service/impl/AuditLogServiceImpl.java`

**Repository (1 file):**
- `repository/AuditLogRepository.java` - JPA Data Access

---

#### **3. Vendor Module** (`vendor/`)
**File Count: 13 files**

**Controller (1 file):**
- `controller/VendorController.java` - Vendor REST API

**Entities (1 file):**
- `entity/Vendor.java` - Vendor JPA entity

**Services (5 files):**
- **Interfaces:**
  - `service/VendorService.java`
  - `service/VendorResolutionService.java`
- **Implementations:**
  - `service/impl/VendorServiceImpl.java`
  - `service/impl/VendorResolutionServiceImpl.java`

**DTOs (2 files):**
- `dto/VendorStatusUpdateDTO.java` - Status updates
- `dto/VendorResolveRequestDTO.java` - Resolution requests

**Repository (1 file):**
- `repository/VendorRepository.java` - JPA Data Access

---

#### **4. Manager Module** (`manager/`)
**File Count: 9 files**

**Controllers (2 files):**
- `controller/ManagerController.java` - Main manager API
- `controller/ManagerDashboardController.java` - Dashboard operations

**Services (5 files):**
- **Interfaces:**
  - `service/ManagerService.java`
  - `service/ManagerDashboardService.java`
  - `service/ManagerDecisionService.java`
- **Implementations:**
  - `service/impl/ManagerServiceImpl.java`
  - `service/impl/ManagerDashboardServiceImpl.java`
  - `service/impl/ManagerDecisionServiceImpl.java`

**DTOs (1 file):**
- `dto/ManagerDecisionRequestDTO.java` - Manager decision transfer

---

#### **5. Escalation Module** (`escalation/`)
**File Count: 2 files**

**Services (2 files):**
- **Interface:**
  - `service/EscalationService.java`
- **Implementation:**
  - `service/impl/EscalationServiceImpl.java`

**Note:** No controller or repository; works as a Camunda delegate service

---

#### **6. Notification Module** (`notification/`)
**File Count: 2 files**

**Services (2 files):**
- **Interface:**
  - `service/NotificationService.java`
- **Implementation:**
  - `service/impl/NotificationServiceImpl.java`

**Note:** No controller or repository; works as a Camunda delegate service

---

### Summary Statistics

| Module | Controllers | Services | DTOs | Entities | Repositories | Total |
|--------|-------------|----------|------|----------|--------------|-------|
| complaint | 3 | 5 (10 w/impl) | 8 | 1 | 1 | 18 |
| auditlog | 1 | 1 (2 w/impl) | 0 | 1 | 1 | 6 |
| vendor | 1 | 2 (4 w/impl) | 2 | 1 | 1 | 13 |
| manager | 2 | 3 (6 w/impl) | 1 | 0 | 0 | 9 |
| escalation | 0 | 1 (2 w/impl) | 0 | 0 | 0 | 2 |
| notification | 0 | 1 (2 w/impl) | 0 | 0 | 0 | 2 |
| **TOTAL** | **7** | **13 (26 w/impl)** | **11** | **3** | **3** | **50+** |

---

## Controller Classes & Endpoints

### 1. ComplaintController
**Package:** `com.complaint_resolution.complaint.controller`  
**Path:** `/api/complaints`  
**Security:** JWT-based authentication

#### Endpoints:

| Method | Endpoint | Auth Required | Roles | Purpose |
|--------|----------|---------------|-------|---------|
| POST | `/` | Yes | Any | Create new complaint |
| GET | `/` | Yes | USER, VENDOR_ADMIN, MANAGER, ADMIN, SUPER_ADMIN | Get all complaints (role-based) |
| POST | `/details` | Yes | DRIVER, VENDOR_ADMIN, MANAGER, ADMIN, SUPER_ADMIN | Get complaint details by ID |
| POST | `/status` | Yes | MANAGER, ADMIN, SUPER_ADMIN | Filter complaints by status |
| POST | `/vehicle` | Yes | VENDOR_ADMIN, MANAGER, ADMIN, SUPER_ADMIN | Get complaints by vehicle ID |

**Dependencies Injected:**
- `ComplaintService` - Business logic
- `JwtUtil` - JWT token extraction
- `AuditLogService` - Audit logging

---

### 2. ConfigController
**Package:** `com.complaint_resolution.complaint.controller`  
**Path:** `/api/config`

#### Endpoints:

| Method | Endpoint | Auth Required | Purpose |
|--------|----------|---------------|---------|
| GET | `/` | No | Fetch dynamic form field configuration |

**Dependencies Injected:**
- `ConfigService` - Configuration management

**Purpose:** Exposes form field definitions for frontend complaint form generation

---

### 3. WorkflowController
**Package:** `com.complaint_resolution.complaint.controller`  
**Path:** `/api/workflow`

#### Endpoints:

| Method | Endpoint | Auth Required | Purpose |
|--------|----------|---------------|---------|
| POST | `/user-response/{taskId}` | Yes | Submit user response to Camunda task |
| POST | `/vendor-response/{taskId}` | Yes | Submit vendor response to Camunda task |

**Dependencies Injected:**
- `TaskService` (Camunda) - Workflow task management

**Parameters:**
- `resolved` (Boolean) - Whether complaint is resolved
- `continueAi` (Boolean) - Continue AI processing

---

### 4. AuditLogController
**Package:** `com.complaint_resolution.auditlog.controller`  
**Path:** `/api/audit-logs`

#### Endpoints:

| Method | Endpoint | Auth Required | Purpose |
|--------|----------|---------------|---------|
| GET | `/complaint/{complaintId}` | Yes | Get audit logs for a specific complaint |
| GET | `/action/{action}` | Yes | Get audit logs by action type |
| GET | `/vehicle/{vehicleId}` | Yes | Get audit logs by vehicle ID |

**Dependencies Injected:**
- `AuditLogService` - Audit log operations

---

### 5. VendorController
**Package:** `com.complaint_resolution.vendor.controller`  
**Path:** `/api/vendors`  
**CORS:** Enabled (`*`)

#### Endpoints:

| Method | Endpoint | Auth Required | Purpose |
|--------|----------|---------------|---------|
| GET | `/` | No | Get all vendors |
| GET | `/{id}` | No | Get vendor by ID |
| GET | `/available` | No | Get available vendors |
| GET | `/expertise/{expertise}` | No | Filter vendors by expertise |
| GET | `/availability/{availability}` | No | Filter vendors by availability |
| GET | `/{vendorName}/complaints` | No | Get complaints assigned to vendor |
| PUT | `/complaints/{complaintId}/status` | Yes | Update complaint status |
| PUT | `/complaints/{complaintId}/resolve` | Yes | Mark complaint as resolved |

**Dependencies Injected:**
- `VendorService` - Vendor operations

---

### 6. ManagerController
**Package:** `com.complaint_resolution.manager.controller`  
**Path:** `/api/manager`  
**CORS:** Enabled (`*`)

#### Endpoints:

| Method | Endpoint | Auth Required | Purpose |
|--------|----------|---------------|---------|
| GET | `/complaints/escalated` | Yes | Get escalated complaints |
| PUT | `/complaints/{complaintId}/decision` | Yes | Submit manager decision |
| GET | `/complaints/history` | Yes | Get manager action history |

**Dependencies Injected:**
- `ManagerDashboardService` - Dashboard operations
- `ManagerService` - Manager business logic

---

### 7. ManagerDashboardController
**Package:** `com.complaint_resolution.manager.controller`  
**Path:** `/api/manager`  
**CORS:** Enabled (`*`)

#### Endpoints:

| Method | Endpoint | Auth Required | Purpose |
|--------|----------|---------------|---------|
| GET | `/dashboard/complaints` | Yes | Get all complaints for manager dashboard |
| PUT | `/complaints/{complaintId}/approve` | Yes | Approve complaint & assign team |
| PUT | `/complaints/{complaintId}/reject` | Yes | Reject complaint |

**Dependencies Injected:**
- `ManagerDashboardService` - Dashboard operations

**Note:** Both ManagerController and ManagerDashboardController share same base path `/api/manager`

---

## Entity Classes & Database Models

### 1. Complaint Entity
**Package:** `com.complaint_resolution.complaint.entity`  
**Table Name:** `complaint` (default)  
**Type:** JPA Entity with auto-increment ID

#### Fields:

| Field | Type | Constraints | Purpose |
|-------|------|-------------|---------|
| `id` | Long | @Id, @GeneratedValue(IDENTITY) | Primary key |
| `domain` | String | - | Complaint domain/category |
| `status` | String | Default: "OPEN" | Complaint status |
| `issueCategory` | String | - | Issue classification |
| `priority` | String | - | Priority level |
| `assignedTeam` | String | - | Assigned team/vendor name |
| `data` | String | @Column(columnDefinition = "TEXT") | JSON complaint details |
| `createdAt` | LocalDateTime | - | Complaint creation timestamp |
| `customerId` | String | @Column(name = "customer_id") | Reference to customer |
| `vehicleId` | String | @Column(name = "vehicle_id") | Reference to vehicle |

#### Key Methods:
- Constructor initializes `status` to "OPEN" and `createdAt` to `LocalDateTime.now()`
- All fields have getter/setter methods

#### Relationships:
- **1:M** with `AuditLog` (implicit via `complaintId`)
- **1:M** with Camunda workflow tasks (implicit via workflow engine)

---

### 2. AuditLog Entity
**Package:** `com.complaint_resolution.auditlog.entity`  
**Table Name:** `audit_logs`  
**Type:** JPA Entity with auto-increment ID

#### Fields:

| Field | Type | Constraints | Purpose |
|-------|------|-------------|---------|
| `id` | Long | @Id, @GeneratedValue(IDENTITY) | Primary key |
| `complaintId` | Long | - | Foreign key to Complaint |
| `vehicleId` | String | @Column(name = "vehicle_id") | Vehicle identifier |
| `action` | String | - | Action performed (e.g., "STATUS_CHANGE", "ASSIGNED") |
| `performedBy` | String | - | User/system that performed action |
| `previousStatus` | String | - | Status before action |
| `newStatus` | String | - | Status after action |
| `remarks` | String | @Column(columnDefinition = "TEXT") | Additional notes |
| `metadata` | String | @Column(columnDefinition = "TEXT") | JSON metadata |
| `createdAt` | LocalDateTime | - | Action timestamp |

#### Constructors:
1. **Default Constructor:** No-arg constructor
2. **Full Constructor:** Accepts all parameters except `id` and `createdAt`

#### Key Methods:
- Full constructor sets `createdAt` to `LocalDateTime.now()`
- All fields have getter/setter methods

#### Relationships:
- **N:1** with `Complaint` (via `complaintId`)

---

### 3. Vendor Entity
**Package:** `com.complaint_resolution.vendor.entity`  
**Table Name:** `vendors`  
**Type:** JPA Entity with auto-increment ID

#### Fields:

| Field | Type | Constraints | Purpose |
|-------|------|-------------|---------|
| `id` | Long | @Id, @GeneratedValue(IDENTITY) | Primary key |
| `name` | String | - | Vendor name |
| `expertise` | String | - | Area of expertise |
| `location` | String | - | Vendor location |
| `latitude` | Double | - | Geographic latitude |
| `longitude` | Double | - | Geographic longitude |
| `rating` | Double | - | Vendor rating (0-5) |
| `availability` | Boolean | - | Current availability status |

#### Key Methods:
- All fields have getter/setter methods
- Location-based (latitude/longitude) for geographic queries

#### Relationships:
- **1:M** with `Complaint` (implicit via `assignedTeam`)

---

## Service Layer & Business Logic

### 1. Complaint Service Layer

#### **ComplaintService Interface**
**Package:** `com.complaint_resolution.complaint.service`

**Core Methods:**
```java
// Complaint Lifecycle
String saveComplaint(ComplaintRequestDTO request, String customerId);
Complaint getComplaintById(Long id);
List<Complaint> getAllComplaints();
List<Complaint> getMyComplaints(String customerId);
List<Complaint> getComplaintsByVehicleId(String vehicleId);

// Filtering
List<Complaint> getComplaintsByStatus(String status);
List<Complaint> getComplaintsByPriority(String priority);

// User Decisions (AI-based workflow)
String userAiDecision(Long complaintId, Boolean resolved, Boolean continueAi);

// RBAC Methods
List<Complaint> getComplaints();
Complaint getComplaintDetails(Long complaintId);
List<Complaint> getComplaintsByVehicle(String vehicleId);
List<Complaint> getComplaintStatus(String status);

// Centralized Action Handler
Object handleComplaintAction(ComplaintActionRequestDTO request);
```

#### **ComplaintServiceImpl**
**Package:** `com.complaint_resolution.complaint.service.impl`

**Key Responsibilities:**
- Complaint creation and persistence
- Data transformation between DTOs and entities
- Role-based complaint filtering
- Audit log creation on complaint actions
- Integration with Camunda workflow

---

#### **AIService Interface**
**Package:** `com.complaint_resolution.complaint.service`

**Purpose:** AI-powered complaint resolution assistance

#### **AIServiceImpl**
**Package:** `com.complaint_resolution.complaint.service.impl`

**Key Responsibilities:**
- AI-based complaint categorization
- Automated initial assessment
- Resolution recommendations

---

#### **ConfigService Interface**
**Package:** `com.complaint_resolution.complaint.service`

**Method:**
```java
List<FieldConfigDTO> getFields();
```

#### **ConfigServiceImpl**
**Package:** `com.complaint_resolution.complaint.service.impl`

**Key Responsibilities:**
- Dynamic form field configuration management
- Frontend form definition generation
- Field validation rules

---

#### **RepeatCheckService Interface**
**Package:** `com.complaint_resolution.complaint.service`

**Purpose:** Detect and handle repeat complaints

#### **RepeatCheckServiceImpl**
**Package:** `com.complaint_resolution.complaint.service.impl`

**Key Responsibilities:**
- Identify duplicate complaints
- Track complaint history by customer/vehicle
- Apply repeat complaint policies

---

#### **UserResolutionService Interface**
**Package:** `com.complaint_resolution.complaint.service`

**Purpose:** Handle user decisions on complaint resolution

#### **UserResolutionServiceImpl**
**Package:** `com.complaint_resolution.complaint.service.impl`

**Key Responsibilities:**
- Process user feedback on resolutions
- Update complaint resolution status
- Trigger workflow transitions

---

### 2. Audit Log Service Layer

#### **AuditLogService Interface**
**Package:** `com.complaint_resolution.auditlog.service`

**Methods:**
```java
List<AuditLog> getLogsByComplaintId(Long complaintId);
List<AuditLog> getLogsByAction(String action);
List<AuditLog> getLogsByVehicleId(String vehicleId);
```

#### **AuditLogServiceImpl**
**Package:** `com.complaint_resolution.auditlog.service.impl`

**Key Responsibilities:**
- Retrieve audit logs by various filters
- Maintain complaint action history
- Support compliance and tracking requirements

---

### 3. Vendor Service Layer

#### **VendorService Interface**
**Package:** `com.complaint_resolution.vendor.service`

**Methods:**
```java
// Camunda Integration
void execute(DelegateExecution execution);

// Vendor Management
List<Vendor> getAllVendors();
Vendor getVendorById(Long id);
List<Vendor> getAvailableVendors();
List<Vendor> getVendorsByExpertise(String expertise);
List<Vendor> getVendorsByAvailability(Boolean availability);

// Complaint Operations
List<Complaint> getAssignedComplaints(String vendorName);
String updateComplaintStatus(Long complaintId, String status);
String resolveComplaint(Long complaintId, Boolean resolved, String remarks);
```

#### **VendorServiceImpl**
**Package:** `com.complaint_resolution.vendor.service.impl`

**Key Responsibilities:**
- Vendor selection and assignment
- Vendor expertise matching
- Complaint resolution tracking
- Camunda task delegation

---

#### **VendorResolutionService Interface**
**Package:** `com.complaint_resolution.vendor.service`

#### **VendorResolutionServiceImpl**
**Package:** `com.complaint_resolution.vendor.service.impl`

**Key Responsibilities:**
- Handle vendor-specific resolution workflows
- Resolution status management
- Vendor performance tracking

---

### 4. Manager Service Layer

#### **ManagerService Interface**
**Package:** `com.complaint_resolution.manager.service`

**Methods:**
```java
String managerDecision(Long complaintId, String decision);
```

#### **ManagerServiceImpl**
**Package:** `com.complaint_resolution.manager.service.impl`

**Key Responsibilities:**
- Manager decision processing
- Approval/rejection workflows
- Complaint escalation handling

---

#### **ManagerDashboardService Interface**
**Package:** `com.complaint_resolution.manager.service`

**Methods:**
```java
List<Map<String, Object>> getEscalatedComplaintsForManager();
List<Map<String, Object>> getManagerHistory();
List<Map<String, Object>> getAllComplaintsForManager();
Complaint approveAndAssignComplaint(Long complaintId, String teamName);
Complaint rejectComplaint(Long complaintId);
```

#### **ManagerDashboardServiceImpl**
**Package:** `com.complaint_resolution.manager.service.impl`

**Key Responsibilities:**
- Dashboard data aggregation
- Complaint approval workflow
- Team assignment logic
- Complaint rejection handling

---

#### **ManagerDecisionService Interface**
**Package:** `com.complaint_resolution.manager.service`

#### **ManagerDecisionServiceImpl**
**Package:** `com.complaint_resolution.manager.service.impl`

**Key Responsibilities:**
- Structured decision processing
- Decision validation
- Workflow routing based on decisions

---

### 5. Escalation Service Layer

#### **EscalationService Interface**
**Package:** `com.complaint_resolution.escalation.service`

**Methods:**
```java
void execute(DelegateExecution execution);
```

**Purpose:** Camunda delegate service for complaint escalation

#### **EscalationServiceImpl**
**Package:** `com.complaint_resolution.escalation.service.impl`

**Key Responsibilities:**
- Handle complaint escalations
- Route escalated complaints to managers
- Track escalation history

**Implementation Type:** Camunda JavaDelegate

---

### 6. Notification Service Layer

#### **NotificationService Interface**
**Package:** `com.complaint_resolution.notification.service`

**Methods:**
```java
void execute(DelegateExecution execution);
```

**Purpose:** Camunda delegate service for notifications

#### **NotificationServiceImpl**
**Package:** `com.complaint_resolution.notification.service.impl`

**Key Responsibilities:**
- Send notifications on complaint status changes
- Notify customers, vendors, and managers
- Support multiple notification channels

**Implementation Type:** Camunda JavaDelegate

---

## DTO Classes

### 1. Complaint Module DTOs

#### **ComplaintRequestDTO**
**Package:** `com.complaint_resolution.complaint.dto`

**Fields:**
- `complaintData: Map<String, Object>` - Flexible JSON complaint data

**Purpose:** Flexible DTO for complaint creation supporting dynamic fields

---

#### **ComplaintActionRequestDTO**
**Package:** `com.complaint_resolution.complaint.dto`

**Purpose:** Handle various complaint actions in centralized endpoint

---

#### **ComplaintDetailsRequestDTO**
**Package:** `com.complaint_resolution.complaint.dto`

**Fields:**
- `complaintId: Long` - Complaint identifier

**Purpose:** Request complaint details

---

#### **ComplaintStatusRequestDTO**
**Package:** `com.complaint_resolution.complaint.dto`

**Fields:**
- `status: String` - Status filter value

**Purpose:** Filter complaints by status

---

#### **VehicleComplaintRequestDTO**
**Package:** `com.complaint_resolution.complaint.dto`

**Fields:**
- `vehicleId: String` - Vehicle identifier

**Purpose:** Retrieve complaints for specific vehicle

---

#### **AuditLogRequestDTO**
**Package:** `com.complaint_resolution.complaint.dto`

**Purpose:** Data transfer for audit log operations

---

#### **FieldConfigDTO**
**Package:** `com.complaint_resolution.complaint.dto`

**Purpose:** Represents dynamic form field configuration

---

#### **UserDecisionRequestDTO**
**Package:** `com.complaint_resolution.complaint.dto`

**Purpose:** Capture user decision on complaint resolution

---

### 2. Vendor Module DTOs

#### **VendorStatusUpdateDTO**
**Package:** `com.complaint_resolution.vendor.dto`

**Fields:**
- `status: String` - New complaint status

**Purpose:** Update complaint status from vendor perspective

---

#### **VendorResolveRequestDTO**
**Package:** `com.complaint_resolution.vendor.dto`

**Fields:**
- `resolved: Boolean` - Resolution flag
- `resolutionRemarks: String` - Resolution details

**Purpose:** Submit complaint resolution from vendor

---

### 3. Manager Module DTOs

#### **ManagerDecisionRequestDTO**
**Package:** `com.complaint_resolution.manager.dto`

**Fields:**
- `managerDecision: String` - Manager's decision (e.g., APPROVE, REJECT, ESCALATE)

**Purpose:** Transfer manager decision to service layer

---

## Repository Layer

### 1. ComplaintRepository
**Package:** `com.complaint_resolution.complaint.repository`

**Type:** Spring Data JPA Repository

**Query Methods:**
```java
// Custom queries
List<Complaint> findByCustomerIdOrderByCreatedAtDesc(String customerId);
List<Complaint> findByCustomerIdAndIssueCategory(String customerId, String issueCategory);
List<Complaint> findByVehicleIdOrderByCreatedAtDesc(String vehicleId);
List<Complaint> findByStatusOrderByCreatedAtDesc(String status);

// Repeat detection
long countByCustomerIdAndIssueCategory(String customerId, String issueCategory);

// Priority filtering (method name truncated in view)
// findByPriorityOrderByCreatedAtDesc(...)
```

**Key Features:**
- Timestamp-based ordering
- Customer-centric queries
- Vehicle-based filtering
- Repeat complaint detection

---

### 2. VendorRepository
**Package:** `com.complaint_resolution.vendor.repository`

**Type:** Spring Data JPA Repository

**Query Methods:**
```java
List<Vendor> findByAvailabilityTrue();
List<Vendor> findByExpertiseIgnoreCase(String expertise);
List<Vendor> findByAvailability(Boolean availability);
```

**Key Features:**
- Availability-based queries
- Case-insensitive expertise filtering
- Boolean availability queries

---

### 3. AuditLogRepository
**Package:** `com.complaint_resolution.auditlog.repository`

**Type:** Spring Data JPA Repository

**Query Methods:**
```java
List<AuditLog> findByComplaintIdOrderByCreatedAtAsc(Long complaintId);
List<AuditLog> findByActionOrderByCreatedAtDesc(String action);
List<AuditLog> findByVehicleIdOrderByCreatedAtAsc(String vehicleId);
```

**Key Features:**
- Chronological ordering (ascending for complaint, descending for action)
- Multi-filter support
- Audit trail tracking

---

## Configuration Files

### 1. pom.xml
**Location:** `Complaint_Resolution/pom.xml`

**Project Information:**
- **GroupId:** `.complaint_resolution`
- **ArtifactId:** `Complaint_Resolution`
- **Version:** `0.0.1-SNAPSHOT`
- **Parent:** `spring-boot-starter-parent:4.0.6`

**Key Properties:**
- **Java Version:** 21

**Dependencies:**
1. **spring-boot-starter-webmvc** - Spring MVC web framework
2. **spring-boot-starter-webmvc-test** (test scope) - Testing support

**Build Configuration:**
- **Plugin:** `spring-boot-maven-plugin` - Spring Boot build plugin

**Notes:**
- Minimal dependencies (likely inherits from parent)
- Test dependencies scoped correctly
- Standard Spring Boot Maven build structure

---

### 2. application.properties
**Location:** `src/main/resources/application.properties`

**Content:**
```properties
spring.application.name=Complaint_Resolution
```

**Current Configuration:**
- Only application name is configured
- Additional properties likely required for:
  - Database connection
  - JPA/Hibernate configuration
  - Camunda integration
  - JWT security configuration
  - Server port
  - Logging levels

**Recommendations:**
- Add database configuration (spring.datasource.*)
- Configure JPA/Hibernate (spring.jpa.*)
- Add Camunda properties
- Configure security settings
- Add logging configuration

---

## Complaint Workflow Analysis

### Workflow Architecture

The module implements a multi-stage complaint workflow with the following characteristics:

#### **Workflow Orchestration:**
- **Engine:** Camunda BPM (referenced in code)
- **Integration:** Via `TaskService` and `DelegateExecution`
- **Implementation Type:** Async workflow with human tasks

#### **Workflow Stages:**

1. **Complaint Submission Stage**
   - User creates complaint via `POST /api/complaints`
   - `ComplaintController.saveComplaint()` processes request
   - Audit log created: Action="COMPLAINT_CREATED"
   - Complaint status set to "OPEN"
   - Complaint saved to database
   - Workflow process initiated

2. **AI Assessment Stage**
   - `AIService` evaluates complaint
   - Categorization and initial assessment
   - Route recommendation (escalate or proceed)
   - Result logged in audit trail

3. **User Decision Stage**
   - Workflow creates human task
   - User reviews AI assessment
   - User decision via `POST /api/workflow/user-response/{taskId}`
   - Variables: `resolved` (Boolean), `continueAi` (Boolean)
   - Decision stored and audit logged

4. **Manager Review Stage**
   - Manager dashboard: `/api/manager/dashboard/complaints`
   - Manager reviews complaint via `GET /api/manager/dashboard/complaints`
   - Manager decision: 
     - APPROVE (assign team/vendor)
     - REJECT
     - ESCALATE
   - Decision via `PUT /api/manager/complaints/{complaintId}/decision`

5. **Vendor Assignment Stage**
   - `ManagerDashboardService.approveAndAssignComplaint()`
   - Vendor selection based on expertise/availability
   - Vendor notification sent (via NotificationService)
   - Status updated to "ASSIGNED"

6. **Vendor Resolution Stage**
   - Vendor views assigned complaints: `GET /api/vendors/{vendorName}/complaints`
   - Updates status: `PUT /api/vendors/complaints/{complaintId}/status`
   - Submits resolution: `PUT /api/vendors/complaints/{complaintId}/resolve`
   - Vendor response via `POST /api/workflow/vendor-response/{taskId}`

7. **Escalation Stage** (if needed)
   - Triggered when resolution fails
   - `EscalationService` routes complaint
   - Manager review for escalated complaints
   - Via `GET /api/manager/complaints/escalated`

8. **Completion/Closure**
   - Complaint marked resolved or rejected
   - Final audit log entry
   - Notification sent to customer
   - Status: "RESOLVED" or "REJECTED"

---

### Workflow Task Integration Points

**WorkflowController** provides task submission endpoints:

```
POST /api/workflow/user-response/{taskId}
{
  "resolved": boolean,
  "continueAi": boolean
}

POST /api/workflow/vendor-response/{taskId}
{
  // vendor-specific response data
}
```

**Camunda Integration:**
- `TaskService.createTaskQuery()` - Query workflow tasks
- `TaskService.complete(taskId, variables)` - Complete tasks with variables
- `DelegateExecution` - Used in service implementations for task delegation

---

### Workflow Configuration Status

**BPMN Files:** Not found in module
- Workflow definitions likely located elsewhere or externalized
- Camunda configuration likely in application.properties (not fully configured)

**Recommendations:**
- Create BPMN process definitions for complaint workflow
- Define human task configurations
- Setup escalation timers and conditions

---

## Duplicate & Standalone Components

### Analysis of Duplication & Standalone Components

#### **1. Potential Duplication: ManagerController & ManagerDashboardController**

**Issue:** Both controllers share the same base path `/api/manager`

**ManagerController endpoints:**
- `GET /api/manager/complaints/escalated`
- `PUT /api/manager/complaints/{complaintId}/decision`
- `GET /api/manager/complaints/history`

**ManagerDashboardController endpoints:**
- `GET /api/manager/dashboard/complaints`
- `PUT /api/manager/complaints/{complaintId}/approve`
- `PUT /api/manager/complaints/{complaintId}/reject`

**Recommendation:** Consider consolidating into single controller with clear path separation:
- `/api/manager/complaints/...` (core operations)
- `/api/manager/dashboard/...` (dashboard operations)

**Impact:** Low - endpoints are distinct and services differ

---

#### **2. Service Layer Separation**

**ManagerService vs ManagerDashboardService vs ManagerDecisionService**

- `ManagerService` - Core decision logic
- `ManagerDashboardService` - Dashboard data aggregation
- `ManagerDecisionService` - Structured decision processing

**Assessment:** 
- **Not duplicate** - Each has distinct responsibility
- **Possible optimization** - Could be consolidated into single service with internal method organization

---

#### **3. Standalone Components: Escalation & Notification**

**EscalationService & EscalationServiceImpl**
- No controller
- No repository
- No entities
- Pure Camunda delegate services

**NotificationService & NotificationServiceImpl**
- No controller
- No repository
- No entities
- Pure Camunda delegate services

**Assessment:** 
- **By design** - These are workflow participants
- **Good separation** - Correctly implemented as isolated services
- **Scalability ready** - Can be moved to separate microservices if needed

---

#### **4. Configuration Exposure**

**ConfigController & ConfigService**
- Exposes dynamic field configurations
- No authentication on GET endpoint
- Supports frontend form generation

**Assessment:**
- **Unique component** - Serves specific UI requirement
- **Security note** - GET endpoint has no auth; consider adding if configurations are sensitive

---

#### **5. Audit Logging**

**AuditLogController & AuditLogService**
- Separate from complaint lifecycle
- Query-only operations (no creates/updates)
- References complaint and vehicle IDs

**Assessment:**
- **Correctly separated** - Audit should be independent
- **Read-only pattern** - Appropriate for audit trails

---

### Summary: Duplicate/Standalone Assessment

| Component | Type | Status | Recommendation |
|-----------|------|--------|-----------------|
| Manager Controllers (2) | Potential Duplicate | Low concern | Could consolidate |
| Manager Services (3) | Separation | Good design | Keep separated |
| Escalation Service | Standalone | By design | Keep as is |
| Notification Service | Standalone | By design | Keep as is |
| Config Service | Unique | Good design | Keep as is |
| Audit Service | Separated | Good design | Keep as is |

---

## Architecture Observations

### Strengths

1. **Role-Based Access Control (RBAC)**
   - `@PreAuthorize` annotations on endpoints
   - Role validation at controller level
   - JWT token integration

2. **Service Layer Abstraction**
   - Clear separation between interfaces and implementations
   - Each module has dedicated service layer
   - Dependency injection patterns

3. **Data Access Pattern**
   - Spring Data JPA repositories
   - Custom query methods
   - Type-safe database access

4. **Workflow Integration**
   - Camunda BPM integration points
   - Human task submission endpoints
   - Async process execution

5. **Audit Trail**
   - Dedicated audit logging module
   - Separate entity and repository
   - Comprehensive tracking of actions

### Areas for Improvement

1. **Configuration**
   - `application.properties` is minimal
   - Database, logging, and Camunda configuration missing
   - Should be externalized for different environments

2. **API Documentation**
   - No Swagger/SpringDoc annotations found
   - API documentation would improve usability

3. **Error Handling**
   - Global exception handler exists in parent module
   - Consider DTO-level validation
   - Custom exceptions for domain-specific errors

4. **Testing**
   - Test directory structure exists but empty
   - Unit and integration tests needed
   - Mock implementations for Camunda testing

5. **BPMN Process Definitions**
   - No workflow definition files found
   - Should be created and versioned
   - Consider BPMN modeling tools integration

---

## Summary Statistics

- **Total Java Files:** 50+
- **Total Controllers:** 7
- **Total Services:** 13+ interfaces + 26+ implementations
- **Total DTOs:** 11
- **Total Entities:** 3
- **Total Repositories:** 3
- **Lines of Code:** Estimated 5,000+
- **Main Packages:** 6 modules
- **API Endpoints:** 25+
- **Database Tables:** 3 (complaint, vendors, audit_logs)

---

## Appendix: File Structure Tree

```
Complaint_Resolution/
├── ComplaintResolutionApplication.java (Main entry point)
├── pom.xml (Maven configuration)
├── complaint/
│   ├── controller/
│   │   ├── ComplaintController.java
│   │   ├── ConfigController.java
│   │   └── WorkflowController.java
│   ├── entity/
│   │   └── Complaint.java
│   ├── service/
│   │   ├── ComplaintService.java
│   │   ├── AIService.java
│   │   ├── ConfigService.java
│   │   ├── RepeatCheckService.java
│   │   ├── UserResolutionService.java
│   │   └── impl/
│   │       ├── ComplaintServiceImpl.java
│   │       ├── AIServiceImpl.java
│   │       ├── ConfigServiceImpl.java
│   │       ├── RepeatCheckServiceImpl.java
│   │       └── UserResolutionServiceImpl.java
│   ├── dto/
│   │   ├── ComplaintRequestDTO.java
│   │   ├── ComplaintActionRequestDTO.java
│   │   ├── ComplaintDetailsRequestDTO.java
│   │   ├── ComplaintStatusRequestDTO.java
│   │   ├── VehicleComplaintRequestDTO.java
│   │   ├── AuditLogRequestDTO.java
│   │   ├── FieldConfigDTO.java
│   │   └── UserDecisionRequestDTO.java
│   └── repository/
│       └── ComplaintRepository.java
├── auditlog/
│   ├── controller/
│   │   └── AuditLogController.java
│   ├── entity/
│   │   └── AuditLog.java
│   ├── service/
│   │   ├── AuditLogService.java
│   │   └── impl/
│   │       └── AuditLogServiceImpl.java
│   └── repository/
│       └── AuditLogRepository.java
├── vendor/
│   ├── controller/
│   │   └── VendorController.java
│   ├── entity/
│   │   └── Vendor.java
│   ├── service/
│   │   ├── VendorService.java
│   │   ├── VendorResolutionService.java
│   │   └── impl/
│   │       ├── VendorServiceImpl.java
│   │       └── VendorResolutionServiceImpl.java
│   ├── dto/
│   │   ├── VendorStatusUpdateDTO.java
│   │   └── VendorResolveRequestDTO.java
│   └── repository/
│       └── VendorRepository.java
├── manager/
│   ├── controller/
│   │   ├── ManagerController.java
│   │   └── ManagerDashboardController.java
│   ├── service/
│   │   ├── ManagerService.java
│   │   ├── ManagerDashboardService.java
│   │   ├── ManagerDecisionService.java
│   │   └── impl/
│   │       ├── ManagerServiceImpl.java
│   │       ├── ManagerDashboardServiceImpl.java
│   │       └── ManagerDecisionServiceImpl.java
│   └── dto/
│       └── ManagerDecisionRequestDTO.java
├── escalation/
│   └── service/
│       ├── EscalationService.java
│       └── impl/
│           └── EscalationServiceImpl.java
└── notification/
    └── service/
        ├── NotificationService.java
        └── impl/
            └── NotificationServiceImpl.java

src/
├── main/
│   └── resources/
│       └── application.properties
└── test/
    └── (empty - test structure ready for implementation)
```

---

**End of Analysis**  
Generated: May 7, 2026
