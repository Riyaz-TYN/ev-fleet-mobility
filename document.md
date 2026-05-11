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
- **Request Body**:
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
*Note: Roles can be `DRIVER`, `VENDOR_ADMIN`, `MANAGER`, `ADMIN`, `SUPER_ADMIN`.*

### **Login**
- **Endpoint**: `POST /api/auth/login`
- **Request Body**:
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```
- **Response**: Returns `accessToken` and `refreshToken`.

---

## 4. Profile & Document Management

### **Complete Profile (Individual)**
- **Endpoint**: `POST /api/profile/complete`
- **Method**: `Multipart Form-Data`
- **Fields**:
    - `fullName` (Text)
    - `phoneNumber` (Text)
    - `addressLine1` (Text)
    - `panNumber` (Text)
    - `panCardFile` (File - Image/PDF)
    - `companyName` (Text) - Links to existing organization.

### **Admin Approvals**
- **Endpoint**: `POST /api/status/update`
- **Roles**: `ADMIN`, `SUPER_ADMIN`
- **Body**: `{"targetUserId": 10, "status": "APPROVED", "type": "USER"}`

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

### **Step 2: AI & User Interaction**
The system automatically triggers AI analysis. The driver then interacts with the workflow:
- **Endpoint**: `POST /api/workflow/user-response`
- **Variables**: `resolved` (bool), `continueAi` (bool), `userFollowUp` (string).

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

### GET `/api/users/organizations` — List Organizations
- **Auth:** `SUPER_ADMIN`, `ADMIN`
- **Query Param:** `?status=APPROVED` (optional)

### GET `/api/users/individuals` — List Individual Users
- **Auth:** `SUPER_ADMIN`, `ADMIN`, `VENDOR_ADMIN`
- **Query Param:** `?status=PENDING` (optional)

### PUT `/api/users/assign-vehicle` — Assign Vehicle to Driver
- **Auth:** `SUPER_ADMIN`, `ADMIN`, `VENDOR_ADMIN`
```json
{ "vehicleId": 5, "driverId": 12 }
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

### GET `/api/documents/presign/{documentId}` — Generate Pre-signed URL
- Returns a time-limited S3 URL (valid 15 minutes).

### GET `/api/documents/download/pan` — Download Own PAN Card
- **Note:** PAN cards are stored as `BYTEA` in PostgreSQL (not S3).
- **Response:** Raw binary bytes with `Content-Type` header.

### GET `/api/documents/download/pan/{targetUserId}` — Download PAN Card (Admin)
- Download any user's PAN card for verification purposes.

---

## 11. Vehicles  —  `/api/vehicles`
> **All endpoints require:** `ADMIN` role

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

### PUT `/api/vehicles/{id}` — Update Vehicle  (same body as POST)
### DELETE `/api/vehicles/{id}` — Delete Vehicle
### GET `/api/vehicles/{id}` — Get Vehicle by ID
### GET `/api/vehicles` — Get All Vehicles

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

### PUT `/api/service-history/{id}` — Update Entry
### DELETE `/api/service-history/{id}` — Delete Entry
### GET `/api/service-history/vehicle/{vehicleId}` — Full Service History
### GET `/api/service-history/vehicle/{vehicleId}/cost` — Total Maintenance Cost (returns `BigDecimal`)
### GET `/api/service-history/vehicle/{vehicleId}/odometer` — Latest Odometer Reading (returns `Long`)

---

## 13. All Complaint Endpoints — Quick Reference  —  `/api/complaints`

| Method | Endpoint | Auth Roles | Body / Params |
|---|---|---|---|
| `POST` | `/api/complaints` | `DRIVER` | `{ complaintData{}, latitude, longitude }` |
| `GET` | `/api/complaints` | All | — (role-filtered) |
| `POST` | `/api/complaints/details` | All | `{ complaintId }` |
| `POST` | `/api/complaints/filter/status` | `MANAGER`, `ADMIN`, `SUPER_ADMIN` | `{ status }` |
| `POST` | `/api/complaints/filter/vehicle` | `VENDOR_ADMIN`, `MANAGER`, `ADMIN`, `SUPER_ADMIN` | `{ vehicleId }` |
| `POST` | `/api/complaints/audit-logs` | `ADMIN`, `SUPER_ADMIN` | `{ complaintId }` |
| `POST` | `/api/complaints/assigned` | `VENDOR_ADMIN`, `MANAGER`, `ADMIN`, `SUPER_ADMIN` | `{ vendorId }` |
| `PUT` | `/api/complaints/status` | `VENDOR_ADMIN`, `MANAGER` | `{ complaintId, status }` |
| `PUT` | `/api/complaints/resolve` | `VENDOR_ADMIN`, `MANAGER` | `{ complaintId, resolved, resolutionRemarks }` |
| `PUT` | `/api/complaints/assign` | `MANAGER`, `ADMIN`, `SUPER_ADMIN` | `{ complaintId, vendorId }` |
| `PUT` | `/api/complaints/reject` | `MANAGER`, `ADMIN`, `SUPER_ADMIN` | `{ complaintId }` |
| `PUT` | `/api/complaints/decision` | `MANAGER`, `ADMIN`, `SUPER_ADMIN` | `{ complaintId, managerDecision, remarks }` |
| `GET` | `/api/complaints/vendors` | `MANAGER`, `ADMIN`, `SUPER_ADMIN` | — |
| `POST` | `/api/complaints/vendors/details` | `VENDOR_ADMIN`, `MANAGER`, `ADMIN`, `SUPER_ADMIN` | `{ vendorId }` |
| `POST` | `/api/complaints/{id}/nearby-vendors` | `MANAGER`, `ADMIN`, `SUPER_ADMIN` | path param |
| `PUT` | `/api/complaints/reassign` | `MANAGER`, `ADMIN`, `SUPER_ADMIN` | `{ complaintId, vendorId }` |
| `PUT` | `/api/complaints/assign-technician` | `VENDOR_ADMIN`, `MANAGER`, `ADMIN`, `SUPER_ADMIN` | `{ complaintId, technicianId }` |

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
- `vendorResolved: false` → escalates to Manager.

### POST `/api/workflow/manager-response`
- **Auth:** `MANAGER`, `ADMIN`, `SUPER_ADMIN`
```json
{ "complaintId": 1, "taskId": null, "managerDecision": "RESOLVE" }
```
- `managerDecision`: `RESOLVE` | `REJECT` | `RETRY`

---

## 15. AI Gateway  —  `/api/ai`
Used by the external FastAPI AI service to persist its queries and responses into the main database.

### POST `/api/ai/queries` — Save AI Query
```json
{ "userId": 1, "vehicleId": "5", "vehicleModel": "Nexon EV", "question": "Battery not charging..." }
```

### POST `/api/ai/responses` — Save AI Response
```json
{
  "queryId": 1, "userId": 1, "vehicleId": "5", "issueId": "BATTERY",
  "answer": "Check the charging port...", "confidence": 0.87,
  "status": "RESOLVED", "title": "Battery Issue", "description": "..."
}
```

### GET `/api/ai/queries/user/{userId}` — Queries by User
### GET `/api/ai/responses/query/{queryId}` — Responses for a Query

---

## 16. Config  —  `/api/config`

### GET `/api/config`
Returns the dynamic field configuration for the complaint submission form.
- **Auth:** All authenticated roles
- **Response:** List of `FieldConfigDTO` — each item has field name, type, label, required flag, and allowed options.

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
