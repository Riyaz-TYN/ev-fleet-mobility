# EV Fleet Mobility - API Documentation & Technical Guide

## 1. Project Overview
The EV Fleet Mobility system is a **Modular Monolith** application designed to handle user onboarding for drivers and organizations, vehicle management, and a comprehensive complaint resolution system integrated with AI and human-in-the-loop workflows (Camunda).


> All secured endpoints require: `Authorization: Bearer <ACCESS_TOKEN>`
> All responses are wrapped in `ApiResponse<T>` → `{ "message": "...", "data": {...} }`

---

## Base URL
```
http://localhost:8080
```

---

## 1. Authentication  —  `/api/auth`

### POST `/api/auth/signup`
Register a new user. Auto-creates `IndividualDetails` or `OrganizationDetails` based on `userType`.
- **Auth:** Public
- **Validation:** `fullName`, `email`, `password` (min 6 chars), `companyName` are `@NotBlank`.
- **Response:**
```json
{
  "message": "Signup successful",
  "data": {
    "fullName": "John Doe",
    "email": "john@example.com"
  }
}
```



### Core Modules:
- **`useronboarding`**: Authentication, Profile Management (Individual/Organization), and Admin Approvals.
- **`complaintresolution`**: Lifecycle of a complaint from driver report to vendor resolution and manager oversight.

---

## 2. Technical Architecture
- **Framework**: Spring Boot 3.4.0 (Java 21)
- **Database**: PostgreSQL (Hybrid storage: `BYTEA` for high-security docs, standard tables for relational data)
- **Workflow**: Camunda BPMN Engine (v7.21.0)
- **Security**: Spring Security + Stateless JWT
- **Module Boundaries**:
    - `common`: Security, JWT, Global Exception Handling.
    - `useronboarding`: Auth, Profiles, Documents, Vehicles.
    - `complaintresolution`: Complaints, AI Integration, Vendors, Managers, Audit Logs.

---

## 3. Authentication & User Management

### **Signup & Identity**
- **Endpoint**: `POST /api/auth/signup`

#### **Example: Individual Signup**
Used when a person registers as an employee or independent driver.
```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "DRIVER",
  "userType": "INDIVIDUAL",
  "phoneNumber": "9876543210",
  "countryCode": "+91",
  "companyName": "Alpha Fleet",
  "gender": "MALE"
}
```

#### **Example: Organization Signup**
Used when a company account is being created.
```json
{
  "fullName": "Alice Smith",
  "email": "admin@ev-service.com",
  "password": "securePass123",
  "role": "VENDOR_ADMIN",
  "userType": "ORGANIZATION",
  "phoneNumber": "1234567890",
  "countryCode": "+1",
  "companyName": "EV Service Solutions"
}
```

*Note: Roles can be `DRIVER`, `VENDOR_ADMIN`, `MANAGER`, `ADMIN`, `SUPER_ADMIN`.*
*Note: `companyName` is **mandatory** for all signup types to ensure the user is linked to an organization entity.*

### **Login**
- **Endpoint**: `POST /api/auth/login`
- **Request Body**:
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```
- **Response**:
```json
{
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGci...",
    "refreshToken": "eyJhbGci..."
  }
}
```


---

## 4. Profile & Document Management

### **Complete Profile**
- **Endpoint**: `POST /api/profile/complete`
- **Method**: `Multipart Form-Data`

#### **Fields for Individual (Driver/Manager)**
*Note: Basic info (Name, Phone, Company) is already captured during signup.*

| Field | Type | Description |
|---|---|---|
| `addressLine1` | Text | Primary address |
| `addressLine2` | Text | Secondary address |
| `panNumber` | Text | Tax ID |
| `panCardFile` | File | Image/PDF of PAN card (stored as BYTEA) |
| `latitude` / `longitude` | Double |User location  |

- **Response:**
```json
{
  "message": "Profile updated successfully",
  "data": null
}
```


#### **Fields for Organization (Vendor/Admin)**
*Note: Basic info (Company Name, Email) is already captured during signup.*

| Field | Type | Description |
|---|---|---|
| `addressLine1` | Text | Business physical address |
| `gstin` | Text | GST identification number |
| `gstinDocumentFile` | File | PDF/Image of GSTIN (uploaded to S3) |
| `panNumber` | Text | Company PAN |
| `panCardFile` | File | Image/PDF of PAN card (stored as BYTEA) |
| `vendorAvailability` | Boolean | True/False availability status |
| `expertise` | Text | Service specialization (e.g., "Battery") |
| `latitude` / `longitude` | Double | HQ/Shop GPS location |

### **Admin Approvals**
- **Endpoint**: `POST /api/status/update`
- **Roles**: `ADMIN`, `SUPER_ADMIN`, `VENDOR_ADMIN` (for employees)
- **Request Body**:
```json
{
  "targetId": 10,
  "status": "APPROVED"
}
```
- **Response:**
```json
{
  "message": "Status updated successfully",
  "data": "Success"
}
```

*Note: `VENDOR_ADMIN` can only approve individuals linked to their own organization.*

---

## 5. Complaint Resolution Lifecycle

### **Step 1: Raise Complaint (Driver)**
- **Endpoint**: `POST /api/complaints`
- **Logic**: Creates a `Complaint` record and starts the `complaintWorkflow` process in Camunda.
- **Body**:
```json
{
  "complaintData": {
    "issueCategory": "BATTERY",
    "issueDescription": "Vehicle won't start after charging.",
    "location": "Sector 5 Hub"
  },
  "latitude": 28.6139,
  "longitude": 77.2090
}
```
- **Response:**
```json
{
  "message": "Complaint raised successfully. AI is analyzing...",
  "payloadSentToAI": { ... original request ... }
}
```


### **Step 2: AI & User Interaction**
The system automatically triggers AI analysis. The driver then interacts with the workflow:
- **Variables**: `resolved` (bool), `continueAi` (bool), `userFollowUp` (string).
- **Response:** `User response submitted successfully` (Plain Text)


### **Step 3: Vendor Assignment (Automated or Manual)**
- **Endpoint**: `PUT /api/complaints/assign` (Manual override by Manager)
- **Logic**: Assigns a `vendorId` to the complaint.
- **Roles**: `MANAGER`, `ADMIN`.

### **Step 4: Technician Management (Vendor Admin)**
- **Endpoint**: `POST /api/complaints/assign-technician`
- **Body**: `{"complaintId": 1, "technicianId": 50}`
- **Constraint**: Only technicians from the same organization can be assigned.

### **Step 5: Resolution**
- **Update Status**: `PUT /api/complaints/status` (e.g., `IN_REPAIR`, `RESOLVED`)
- **Resolve Endpoint**: `PUT /api/complaints/resolve`
- **Body**: `{"complaintId": 1, "resolved": true, "remarks": "Replaced cable"}`
- **Response:** `Complaint resolved successfully` (Plain Text)


---

## 6. Workflow State Reference

| Status | Triggered By | Next Possible States |
|---|---|---|
| `OPEN` | Driver Signup | `IN_PROGRESS` |
| `IN_PROGRESS` | Camunda Start | `AI_PROCESSED`, `ASSIGNED_TO_VENDOR` |
| `AI_PROCESSED` | AI Service Delegate | `RESOLVED`, `ASSIGNED_TO_VENDOR` |
| `ASSIGNED_TO_VENDOR` | Vendor Service / Manager | `RESOLVED`, `ESCALATED_TO_MANAGER` |
| `ESCALATED_TO_MANAGER`| SLA Breach (2h) / Vendor Failure | `RESOLVED`, `REJECTED`, `RETRY_VENDOR` |
| `RESOLVED` | Final Resolution | (End State) |

---

## 7. Audit & Troubleshooting
- **Audit Logs**: `POST /api/complaints/audit-logs` with body `{"complaintId": 1}` — Auth: `ADMIN`, `SUPER_ADMIN`
- **Nearby Vendors**: `POST /api/complaints/{complaintId}/nearby-vendors` — uses GPS coordinates from the complaint.
- **Camunda Cockpit**: Access via `/camunda/app/cockpit/` (login: `demo` / `demo`) for real-time process monitoring.

---

## 8. Admin — User Management  —  `/api/users`

### GET `/api/users` — List Users (Role-Filtered)
- **Auth:** `SUPER_ADMIN`, `ADMIN`, `VENDOR_ADMIN`
- **Query Param:** `?status=PENDING` (optional — `PENDING` | `APPROVED` | `REJECTED`)
- `VENDOR_ADMIN` sees only users within their own organization.
- **Response:**
```json
{
  "message": "Users fetched successfully",
  "data": [
    {
      "id": 1,
      "email": "user@example.com",
      "role": "DRIVER",
      "userType": "INDIVIDUAL",
      "approvalStatus": "APPROVED",
      "companyName": "Alpha Fleet"
    }
  ]
}
```


### GET `/api/users/organizations` — List Organizations
- **Auth:** `SUPER_ADMIN`, `ADMIN`
- **Query Param:** `?status=APPROVED` (optional)
- **Response:**
```json
{
  "message": "Organizations fetched successfully",
  "data": [
    {
      "id": 10,
      "companyName": "EV Service Solutions",
      "email": "admin@ev-service.com",
      "approvalStatus": "APPROVED"
    }
  ]
}
```


### GET `/api/users/individuals` — List Individual Users
- **Auth:** `SUPER_ADMIN`, `ADMIN`, `VENDOR_ADMIN`
- **Query Param:** `?status=PENDING` (optional)
- **Response:**
```json
{
  "message": "Individuals fetched successfully",
  "data": [
    {
      "id": 5,
      "email": "driver@alpha.com",
      "role": "DRIVER",
      "userType": "INDIVIDUAL",
      "approvalStatus": "PENDING",
      "companyName": "Alpha Fleet"
    }
  ]
}
```


### PUT `/api/users/assign-vehicle` — Assign Vehicle to Driver
- **Auth:** `SUPER_ADMIN`, `ADMIN`, `VENDOR_ADMIN`
```json
{ "vehicleId": 5, "driverId": 12 }
```
- **Response:**
```json
{
  "message": "Driver assigned to vehicle successfully",
  "data": null
}
```

---

## 9. Admin — Status  —  `/api/status`

### POST `/api/status/update` — Approve / Reject User or Organization
- **Auth:** `SUPER_ADMIN`, `ADMIN`, `VENDOR_ADMIN`
- **Logic:** `VENDOR_ADMIN` can only update users within their own organization.
```json
{
  "targetId": 10,
  "status": "APPROVED"
}
```
- `status`: `PENDING` | `APPROVED` | `REJECTED`
- **Response:**
```json
{
  "message": "Status updated successfully",
  "data": "Success"
}
```


---

## 10. Documents  —  `/api/documents`

### POST `/api/documents/upload` — Upload Document to S3
- **Auth:** Any authenticated user
- **Content-Type:** `multipart/form-data`
- **Params:** `file` (File), `documentType` (String — e.g., `GSTIN`)
```json
// Response data
{
  "documentId": 1,
  "documentType": "GSTIN",
  "fileUrl": "https://s3.amazonaws.com/ev-fleet-docs/...",
  "status": "PENDING"
}
```

### GET `/api/documents/my` — Get My Documents
- **Auth:** Any authenticated user
- **Response:**
```json
{
  "message": "Fetched documents successfully",
  "data": [
    {
      "id": 1,
      "documentType": "GSTIN",
      "fileUrl": "...",
      "status": "APPROVED"
    }
  ]
}
```


### GET `/api/documents/presign/{documentId}` — Generate Pre-signed URL
- Returns a time-limited S3 URL (valid 15 minutes).
- **Response:**
```json
{
  "message": "Pre-signed URL generated (valid 15 minutes)",
  "data": "https://s3.amazonaws.com/ev-fleet-docs/..."
}
```


### GET `/api/documents/download/pan` — Download Own PAN Card
- **Note:** PAN cards are stored as `BYTEA` in PostgreSQL (not S3).
- **Response:** Raw binary bytes with `Content-Type` header.

### GET `/api/documents/download/pan/{targetUserId}` — Download PAN Card (Admin)
- Download any user's PAN card for verification purposes.
- **Response:** Raw binary bytes with `Content-Type` header (image/jpeg, application/pdf, etc.).


---

## 11. Vehicles  —  `/api/vehicles`
> **Note:** `GET` endpoints are available to `DRIVER`, `MANAGER`, and `VENDOR_ADMIN`. CRUD (POST/PUT/DELETE) require `ADMIN` role.

### POST `/api/vehicles` — Register Vehicle
```json
{
  "userId": 12,
  "make": "Tata",
  "model": "Nexon EV",
  "licensePlate": "KA01AB1234",
  "vin": "1HGCM82633A123456",
  "chassisNo": "CH123456789",
  "status": "AVAILABLE",
  "yearOfManufacture": 2023,
  "batteryCapacityKwh": 40.5
}
```
- `userId` is **optional** — vehicles can be registered without an assigned driver.
- `status`: `AVAILABLE` | `ACTIVE` | `INACTIVE` | `UNDER_MAINTENANCE` | `DECOMMISSIONED`
- **Response:**
```json
{
  "message": "Vehicle added successfully",
  "data": {
    "id": 5,
    "make": "Tata",
    "model": "Nexon EV",
    "licensePlate": "KA01AB1234",
    "status": "AVAILABLE"
  }
}
```


### PUT `/api/vehicles/{id}` — Update Vehicle  (same body as POST)
- **Response:**
```json
{
  "message": "Vehicle updated successfully",
  "data": {
    "id": 5,
    "status": "ACTIVE"
  }
}
```

### DELETE `/api/vehicles/{id}` — Delete Vehicle
- **Response:**
```json
{
  "message": "Vehicle deleted successfully",
  "data": null
}
```

### GET `/api/vehicles/{id}` — Get Vehicle by ID
- **Auth:** `ADMIN`, `SUPER_ADMIN`, `DRIVER`, `MANAGER`, `VENDOR_ADMIN`
- **Response:**
```json
{
  "message": "Vehicle fetched successfully",
  "data": {
    "id": 5,
    "make": "Tata",
    "model": "Nexon EV",
    "licensePlate": "KA01AB1234",
    "vin": "...",
    "status": "AVAILABLE"
  }
}
```


### GET `/api/vehicles` — Get All Vehicles
- **Auth:** `ADMIN`, `SUPER_ADMIN`, `DRIVER`, `MANAGER`, `VENDOR_ADMIN`
- **Response:**
```json
{
  "message": "Vehicles fetched successfully",
  "data": [
    {
      "id": 5,
      "make": "Tata",
      "model": "Nexon EV",
      "status": "AVAILABLE"
    }
  ]
}
```


#### **VehicleResponse DTO**
| Field | Type | Description |
|---|---|---|
| `id` | Long | Internal ID |
| `userId` | Long | ID of the assigned driver |
| `make` | String | e.g., "Tata" |
| `model` | String | e.g., "Nexon EV" |
| `licensePlate` | String | Vehicle registration number |
| `status` | String | `AVAILABLE`, `ACTIVE`, `UNDER_MAINTENANCE`, etc. |
| `batteryCapacityKwh` | Double | Battery pack size |

---

## 12. Service History  —  `/api/service-history`
> **All endpoints require:** `ADMIN` role

### POST `/api/service-history` — Log Service Entry
```json
{
  "vehicleId": 5,
  "serviceDate": "2024-11-15",
  "odometerReading": 24500,
  "serviceType": "BATTERY_SERVICE",
  "description": "Full battery pack replacement",
  "cost": 45000.00,
  "providerName": "EV Care Ltd"
}
```
- `serviceType`: `ROUTINE_MAINTENANCE` | `BATTERY_SERVICE` | `BATTERY_CHECK` | `TIRE_REPLACEMENT` | `BRAKE_SERVICE` | `SOFTWARE_UPDATE` | `INSPECTION` | `REPAIR` | `EMERGENCY_SERVICE` | `OTHER`
- **Response:**
```json
{
  "message": "Service entry added successfully",
  "data": {
    "id": 1,
    "vehicleId": 5,
    "serviceDate": "2024-11-15",
    "cost": 45000.00
  }
}
```


### PUT `/api/service-history/{id}` — Update Entry
- **Response:**
```json
{
  "message": "Service entry updated successfully",
  "data": { "id": 1, "cost": 46000.00 }
}
```

### DELETE `/api/service-history/{id}` — Delete Entry
- **Response:**
```json
{
  "message": "Service entry deleted successfully",
  "data": null
}
```

### GET `/api/service-history/vehicle/{vehicleId}` — Full Service History
- **Response:**
```json
{
  "message": "Service history fetched successfully",
  "data": [
    { "id": 1, "serviceDate": "2024-11-15", "serviceType": "BATTERY_SERVICE" }
  ]
}
```

### GET `/api/service-history/vehicle/{vehicleId}/cost` — Total Maintenance Cost (returns `BigDecimal`)
- **Response:**
```json
{
  "message": "Total cost fetched successfully",
  "data": 45000.00
}
```

### GET `/api/service-history/vehicle/{vehicleId}/odometer` — Latest Odometer Reading (returns `Long`)
- **Response:**
```json
{
  "message": "Latest odometer fetched successfully",
  "data": 24500
}
```


---

## 13. All Complaint Endpoints — Quick Reference  —  `/api/complaints`

| Method | Endpoint | Auth Roles | Body / Params | Response Data |
|---|---|---|---|---|
| `POST` | `/api/complaints` | `DRIVER` | `{ complaintData{}, latitude, longitude }` | `Map<String, Object>` |
| `GET` | `/api/complaints` | All | — (role-filtered) | `List<Complaint>` |
| `POST` | `/api/complaints/details` | All | `{ complaintId }` | `Complaint` |
| `POST` | `/api/complaints/filter/status` | `MANAGER`, `ADMIN`, `SUPER_ADMIN` | `{ status }` | `List<Complaint>` |
| `POST` | `/api/complaints/filter/vehicle` | `VENDOR_ADMIN`, `MANAGER`, `ADMIN`, `SUPER_ADMIN` | `{ vehicleId }` | `List<Complaint>` |
| `POST` | `/api/complaints/audit-logs` | `ADMIN`, `SUPER_ADMIN` | `{ complaintId }` | `List<AuditLog>` |
| `POST` | `/api/complaints/assigned` | `VENDOR_ADMIN`, `MANAGER`, `ADMIN`, `SUPER_ADMIN` | `{ vendorId }` | `List<Complaint>` |
| `PUT` | `/api/complaints/status` | `VENDOR_ADMIN`, `MANAGER` | `{ complaintId, status }` | `String` (Success msg) |
| `PUT` | `/api/complaints/resolve` | `VENDOR_ADMIN`, `MANAGER` | `{ complaintId, resolved, remarks }` | `String` (Success msg) |
| `PUT` | `/api/complaints/assign` | `MANAGER`, `ADMIN`, `SUPER_ADMIN` | `{ complaintId, vendorId }` | `Complaint` |
| `PUT` | `/api/complaints/reject` | `MANAGER`, `ADMIN`, `SUPER_ADMIN` | `{ complaintId }` | `Complaint` |
| `PUT` | `/api/complaints/decision` | `MANAGER`, `ADMIN`, `SUPER_ADMIN` | `{ complaintId, decision, remarks }` | `String` (Success msg) |
| `GET` | `/api/complaints/vendors` | `MANAGER`, `ADMIN`, `SUPER_ADMIN` | — | `List<OrganizationDetails>` |
| `POST` | `/api/complaints/vendors/details` | `VENDOR_ADMIN`, `MANAGER`, `ADMIN`, `SUPER_ADMIN` | `{ vendorId }` | `OrganizationDetails` |
| `POST` | `/api/complaints/{id}/nearby-vendors` | `MANAGER`, `ADMIN`, `SUPER_ADMIN` | path param | `List<VendorDTO>` |
| `PUT` | `/api/complaints/reassign` | `MANAGER`, `ADMIN`, `SUPER_ADMIN` | `{ complaintId, vendorId }` | `Complaint` |
| `PUT` | `/api/complaints/assign-technician` | `VENDOR_ADMIN`, `MANAGER`, `ADMIN`, `SUPER_ADMIN` | `{ complaintId, technicianId }` | `Complaint` |

#### **Example: Complaint Response**
```json
{
  "id": 1,
  "status": "ASSIGNED_TO_VENDOR",
  "issueCategory": "BATTERY",
  "priority": "HIGH",
  "customerId": "1",
  "vehicleId": "5",
  "vendorId": 10,
  "technicianId": null,
  "latitude": 28.6139,
  "longitude": 77.2090,
  "workSummary": "2024-11-15: Complaint Raised\n2024-11-15: Vendor Assigned"
}
```


**`managerDecision` values:** `RESOLVE` | `REJECT` | `RETRY`

---

## 14. Workflow Tasks  —  `/api/workflow`
Directly interact with Camunda task engine. `taskId` is optional — auto-looked up by `complaintId` if omitted.

### POST `/api/workflow/user-response`
- **Auth:** `DRIVER`, `MANAGER`, `ADMIN`, `SUPER_ADMIN`
```json
{
  "complaintId": 1,
  "taskId": null,
  "resolved": false,
  "continueAi": false,
  "userFollowUp": "AI suggestion didn't work. Please send a technician."
}
```
| Condition | Outcome |
|---|---|
| `resolved: true` | Complaint closed. Workflow ends. |
| `continueAi: true` | Re-runs AI (up to 3 attempts). After 3 → auto-routes to vendor. |
| Both `false` | Immediately routes to vendor assignment. Sets `escalationReason`. |

### POST `/api/workflow/vendor-response`
- **Auth:** `VENDOR_ADMIN`, `MANAGER`, `ADMIN`, `SUPER_ADMIN`
```json
{ "complaintId": 1, "taskId": null, "vendorResolved": true }
```
- **Response:** `Vendor response submitted successfully` (Plain Text)

- `vendorResolved: false` → escalates to Manager.

### POST `/api/workflow/manager-response`
- **Auth:** `MANAGER`, `ADMIN`, `SUPER_ADMIN`
```json
{ "complaintId": 1, "taskId": null, "managerDecision": "RESOLVE" }
```
- **Response:** `Manager response submitted successfully` (Plain Text)

- `managerDecision`: `RESOLVE` | `REJECT` | `RETRY`

---

## 15. AI Gateway  —  `/api/ai`
Used by the external FastAPI AI service to persist its queries and responses into the main database.

### POST `/api/ai/queries` — Save AI Query
```json
{ "userId": 1, "vehicleId": "5", "vehicleModel": "Nexon EV", "question": "Battery not charging..." }
```
- **Response:**
```json
{
  "id": 1,
  "userId": 1,
  "question": "Battery not charging...",
  "createdAt": "2024-11-15T10:00:00"
}
```


### POST `/api/ai/responses` — Save AI Response
```json
{
  "queryId": 1, "userId": 1, "vehicleId": "5", "issueId": "BATTERY",
  "answer": "Check the charging port...", "confidence": 0.87,
  "status": "RESOLVED", "title": "Battery Issue", "description": "..."
}
```
- **Response:**
```json
{
  "id": 1,
  "queryId": 1,
  "answer": "Check the charging port...",
  "confidence": 0.87,
  "status": "RESOLVED"
}
```


### GET `/api/ai/queries/user/{userId}` — Queries by User
- **Response:** `List<AIQuery>`

### GET `/api/ai/responses/query/{queryId}` — Responses for a Query
- **Response:** `List<AIResponse>`


---

## 16. Config  —  `/api/config`

### GET `/api/config`
Returns the dynamic field configuration for the complaint submission form.
- **Auth:** All authenticated roles
- **Response:**
```json
[
  {
    "fieldName": "issueCategory",
    "label": "Issue Category",
    "type": "select",
    "required": true,
    "options": ["BATTERY", "TIRE", "SOFTWARE"]
  }
]
```


---

## 17. Complaint Entity — Field Reference

| Field | Type | Description |
|---|---|---|
| `id` | Long | Auto-generated primary key |
| `status` | String | Current lifecycle status |
| `issueCategory` | String | e.g., `BATTERY`, `TIRE`, `SOFTWARE` |
| `priority` | String | `LOW`, `MEDIUM`, `HIGH` (default: `LOW`) |
| `data` | TEXT | Full JSON of the original complaint payload |
| `customerId` | String | Submitting driver's user ID |
| `vehicleId` | String | Linked vehicle ID |
| `vendorId` | Long | Assigned vendor organization ID |
| `technicianId` | Long | Assigned technician user ID |
| `latitude` | Double | Driver GPS location when complaint was raised |
| `longitude` | Double | Driver GPS location when complaint was raised |
| `escalationReason` | TEXT | Set when AI limit reached or user unsatisfied |
| `workSummary` | TEXT | Chronological append-only action log |
| `createdAt` | LocalDateTime | Immutable timestamp set on creation |

---

## 18. User & Organization Management — `/api/users`

### GET `/api/users` — List All Users
- **Auth:** `ADMIN`, `SUPER_ADMIN`, `VENDOR_ADMIN`
- **Params:** `status` (Optional - `PENDING`, `APPROVED`, etc.)
- **Logic:** Platform Admins see all; Vendor Admins see only their organization's employees.

### GET `/api/users/organizations` — List Organizations
- **Auth:** `ADMIN`, `SUPER_ADMIN`

### GET `/api/users/individuals` — List Individuals
- **Auth:** `ADMIN`, `SUPER_ADMIN`, `VENDOR_ADMIN`
- **Response:** `List<UserDetailsResponse>`


### PUT `/api/users/assign-vehicle` — Assign Driver to Vehicle
- **Auth:** `ADMIN`, `SUPER_ADMIN`, `VENDOR_ADMIN`
- **Body:** `{ "driverId": 12, "vehicleId": 5 }`

### PUT `/api/users/{targetUserId}/rating` — Update Vendor Rating
- **Auth:** `ADMIN`, `SUPER_ADMIN`
- **Body:** `{ "rating": 4.5 }`

---

## 19. Vendor Management — `/api/vendors`

### GET `/api/vendors` — List All Vendors
- **Auth:** `MANAGER`, `ADMIN`, `SUPER_ADMIN`
- **Response:** `List<CompanyDetailsResponse>`

### GET `/api/vendors/available` — List Active/Available Vendors
- **Auth:** `VENDOR_ADMIN`, `MANAGER`, `ADMIN`, `SUPER_ADMIN`
- **Response:** `List<CompanyDetailsResponse>`


---

## 20. Audit Logs — `/api/audit-logs`
> **Auth:** `ADMIN`, `SUPER_ADMIN`, `MANAGER`

### POST `/api/audit-logs/complaint` — Logs by Complaint
- **Body:** `{ "complaintId": 1 }`
- **Response:** `List<AuditLog>`

### POST `/api/audit-logs/vehicle` — Logs by Vehicle
- **Body:** `{ "vehicleId": "5" }`
- **Response:** `List<AuditLog>`

### POST `/api/audit-logs/action` — Logs by Action Type
- **Body:** `{ "action": "AI_ANALYZED" }`
- **Response:** `List<AuditLog>`


---
