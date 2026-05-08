# Enterprise API Documentation: Complaint Resolution Module

## Architecture & Security Overview

### Centralized Authentication Flow
The complaint resolution module integrates with the enterprise unified architecture by relying on a centralized authentication flow. Authentication is decoupled from individual controllers, ensuring that API routing is only performed for securely authenticated contexts.

### JWT Usage
JSON Web Tokens (JWT) are strictly validated upstream by the `JwtFilter`. The filter parses the token, extracts the claims (including user ID and authorities/roles), and populates the Spring `SecurityContextHolder`. Controllers do not parse JWTs directly from the `HttpServletRequest`.

### AuthContextService Usage
Sensitive identifiers representing the currently authenticated user are injected directly via `AuthContextService`. For example, `authContextService.getCurrentUserId()` is used instead of parsing the JWT or checking headers manually in the controller.

### DTO-Based API Standardization
To prevent exposure of sensitive resource IDs (like `complaintId`, `vendorId`, `vehicleId`, `taskId`) in URL path parameters, all such parameters have been moved into secure Request Body Data Transfer Objects (DTOs). New HTTP `POST` and `PUT` methods accept these DTOs.

### Deprecated Endpoint Mappings
Old path-variable endpoints have been annotated with `@Deprecated`. They are retained alongside the new secure DTO APIs to maintain backward compatibility during the transitional phase.

---

## 1. Complaint APIs (`ComplaintController`)

### 1.1 Create Complaint
1. **Endpoint URL**: `/api/complaints`
2. **HTTP Method**: `POST`
3. **Purpose of API**: Create a new vehicle complaint.
4. **Required Role/RBAC**: `USER`, `DRIVER`
5. **Request Headers**: `Authorization: Bearer <token>`
6. **Request Body DTO**: `ComplaintRequestDTO`
7. **Full sample request JSON**:
```json
{
  "vehicleId": "VEH-1001",
  "issueDescription": "Battery draining quickly",
  "priority": "HIGH"
}
```
8. **Response structure**: `String`
9. **Full sample response JSON**: `"Complaint generated successfully with ID: 10"`
10. **Error responses**: `403 Forbidden`, `500 Internal Server Error`
11. **Workflow impact**: Initiates Camunda BPMN workflow for complaint resolution.
12. **Deprecated old APIs**: None.
13. **Authentication requirement**: Required.
14. **Endpoint Category**: DRIVER

### 1.2 Get All Complaints
1. **Endpoint URL**: `/api/complaints`
2. **HTTP Method**: `GET`
3. **Purpose of API**: Fetch all unified complaints based on role access.
4. **Required Role/RBAC**: `USER`, `DRIVER`, `VENDOR`, `VENDOR_ADMIN`, `MANAGER`, `ADMIN`, `SUPER_ADMIN`
5. **Request Headers**: `Authorization: Bearer <token>`
6. **Request Body DTO**: None
7. **Full sample request JSON**: N/A
8. **Response structure**: `List<Complaint>`
9. **Full sample response JSON**:
```json
[
  {
    "id": 10,
    "vehicleId": "VEH-1001",
    "status": "OPEN"
  }
]
```
10. **Error responses**: `403 Forbidden`
11. **Workflow impact**: None
12. **Deprecated old APIs**: `GET /api/complaints/all`
13. **Authentication requirement**: Required.
14. **Endpoint Category**: ADMIN / MANAGER

### 1.3 Get Complaint Details
1. **Endpoint URL**: `/api/complaints/details`
2. **HTTP Method**: `POST`
3. **Purpose of API**: Fetch specific complaint details securely.
4. **Required Role/RBAC**: `USER`, `DRIVER`, `VENDOR`, `VENDOR_ADMIN`, `MANAGER`, `ADMIN`, `SUPER_ADMIN`
5. **Request Headers**: `Authorization: Bearer <token>`
6. **Request Body DTO**: `ComplaintDetailsRequestDTO`
7. **Full sample request JSON**:
```json
{
  "complaintId": 10
}
```
8. **Response structure**: `Complaint`
9. **Full sample response JSON**:
```json
{
  "id": 10,
  "vehicleId": "VEH-1001",
  "status": "OPEN",
  "issueDescription": "Battery draining quickly"
}
```
10. **Error responses**: `404 Not Found`, `403 Forbidden`
11. **Workflow impact**: None
12. **Deprecated old APIs**: `GET /api/complaints/{id}`
13. **Authentication requirement**: Required.
14. **Endpoint Category**: DRIVER / VENDOR / MANAGER / ADMIN

### 1.4 Get Complaints by Status
1. **Endpoint URL**: `/api/complaints/status`
2. **HTTP Method**: `POST`
3. **Purpose of API**: Filter complaints by status.
4. **Required Role/RBAC**: `MANAGER`, `ADMIN`, `SUPER_ADMIN`
5. **Request Headers**: `Authorization: Bearer <token>`
6. **Request Body DTO**: `ComplaintStatusRequestDTO`
7. **Full sample request JSON**:
```json
{
  "status": "RESOLVED"
}
```
8. **Response structure**: `List<Complaint>`
9. **Full sample response JSON**: `[]`
10. **Error responses**: `403 Forbidden`
11. **Workflow impact**: None
12. **Deprecated old APIs**: `GET /api/complaints/status/{status}`
13. **Authentication requirement**: Required.
14. **Endpoint Category**: MANAGER / ADMIN

### 1.5 Get Complaints by Vehicle
1. **Endpoint URL**: `/api/complaints/vehicle`
2. **HTTP Method**: `POST`
3. **Purpose of API**: Filter complaints by vehicle ID.
4. **Required Role/RBAC**: `VENDOR`, `VENDOR_ADMIN`, `MANAGER`, `ADMIN`, `SUPER_ADMIN`
5. **Request Headers**: `Authorization: Bearer <token>`
6. **Request Body DTO**: `VehicleComplaintRequestDTO`
7. **Full sample request JSON**:
```json
{
  "vehicleId": "VEH-1001"
}
```
8. **Response structure**: `List<Complaint>`
9. **Full sample response JSON**: `[]`
10. **Error responses**: `403 Forbidden`
11. **Workflow impact**: None
12. **Deprecated old APIs**: `GET /api/complaints/vehicle/{vehicleId}`
13. **Authentication requirement**: Required.
14. **Endpoint Category**: VENDOR / MANAGER / ADMIN

### 1.6 Handle Complaint Action
1. **Endpoint URL**: `/api/complaints/action`
2. **HTTP Method**: `POST`
3. **Purpose of API**: Process a dynamic action on a complaint.
4. **Required Role/RBAC**: `USER`, `DRIVER`, `VENDOR`, `VENDOR_ADMIN`, `MANAGER`
5. **Request Headers**: `Authorization: Bearer <token>`
6. **Request Body DTO**: `ComplaintActionRequestDTO`
7. **Full sample request JSON**:
```json
{
  "complaintId": 10,
  "action": "ESCALATE",
  "remarks": "Needs urgent attention"
}
```
8. **Response structure**: `String` or generic Object
9. **Full sample response JSON**: `"Action successfully recorded"`
10. **Error responses**: `403 Forbidden`
11. **Workflow impact**: Updates DB and may interact with current BPMN task.
12. **Deprecated old APIs**: None
13. **Authentication requirement**: Required.
14. **Endpoint Category**: DRIVER / VENDOR / MANAGER

### 1.7 Get My Complaints
1. **Endpoint URL**: `/api/complaints/my-complaints`
2. **HTTP Method**: `GET`
3. **Purpose of API**: Get complaints assigned/created by the current user.
4. **Required Role/RBAC**: `USER`, `DRIVER`
5. **Request Headers**: `Authorization: Bearer <token>`
6. **Request Body DTO**: None
7. **Full sample request JSON**: N/A
8. **Response structure**: `List<Complaint>`
9. **Full sample response JSON**: `[]`
10. **Error responses**: `403 Forbidden`
11. **Workflow impact**: None
12. **Deprecated old APIs**: Note: The API is currently marked `@Deprecated` because it is transitioning.
13. **Authentication requirement**: Required.
14. **Endpoint Category**: DRIVER

---

## 2. Vendor APIs (`VendorController`)

### 2.1 Get All Vendors
1. **Endpoint URL**: `/api/vendors`
2. **HTTP Method**: `GET`
3. **Purpose of API**: Fetch all registered vendors.
4. **Required Role/RBAC**: `MANAGER`, `ADMIN`, `SUPER_ADMIN`
5. **Request Headers**: `Authorization: Bearer <token>`
6. **Request Body DTO**: None
7. **Full sample request JSON**: N/A
8. **Response structure**: `List<Vendor>`
9. **Full sample response JSON**: `[{"id": 1, "vendorName": "Mechanic Inc"}]`
10. **Error responses**: `403 Forbidden`
11. **Workflow impact**: None
12. **Deprecated old APIs**: None
13. **Authentication requirement**: Required.
14. **Endpoint Category**: MANAGER / ADMIN

### 2.2 Get Vendor Details
1. **Endpoint URL**: `/api/vendors/details`
2. **HTTP Method**: `POST`
3. **Purpose of API**: Securely fetch details of a specific vendor.
4. **Required Role/RBAC**: `VENDOR`, `VENDOR_ADMIN`, `MANAGER`, `ADMIN`, `SUPER_ADMIN`
5. **Request Headers**: `Authorization: Bearer <token>`
6. **Request Body DTO**: `VendorIdRequestDTO`
7. **Full sample request JSON**:
```json
{
  "vendorId": 1
}
```
8. **Response structure**: `Vendor`
9. **Full sample response JSON**: `{"id": 1, "vendorName": "Mechanic Inc"}`
10. **Error responses**: `404 Not Found`
11. **Workflow impact**: None
12. **Deprecated old APIs**: `GET /api/vendors/{id}`
13. **Authentication requirement**: Required.
14. **Endpoint Category**: VENDOR / MANAGER

### 2.3 Get Vendors by Expertise
1. **Endpoint URL**: `/api/vendors/expertise`
2. **HTTP Method**: `POST`
3. **Purpose of API**: Filter vendors by their domain of expertise.
4. **Required Role/RBAC**: `MANAGER`, `ADMIN`, `SUPER_ADMIN`
5. **Request Headers**: `Authorization: Bearer <token>`
6. **Request Body DTO**: `VendorExpertiseRequestDTO`
7. **Full sample request JSON**:
```json
{
  "expertise": "BATTERY"
}
```
8. **Response structure**: `List<Vendor>`
9. **Full sample response JSON**: `[]`
10. **Error responses**: `403 Forbidden`
11. **Workflow impact**: None
12. **Deprecated old APIs**: `GET /api/vendors/expertise/{expertise}`
13. **Authentication requirement**: Required.
14. **Endpoint Category**: MANAGER

### 2.4 Get Assigned Complaints (Vendor Dashboard)
1. **Endpoint URL**: `/api/vendors/complaints`
2. **HTTP Method**: `POST`
3. **Purpose of API**: Fetch complaints assigned to a particular vendor.
4. **Required Role/RBAC**: `VENDOR`, `VENDOR_ADMIN`, `MANAGER`, `ADMIN`, `SUPER_ADMIN`
5. **Request Headers**: `Authorization: Bearer <token>`
6. **Request Body DTO**: `VendorNameRequestDTO`
7. **Full sample request JSON**:
```json
{
  "vendorName": "Mechanic Inc"
}
```
8. **Response structure**: `List<Complaint>`
9. **Full sample response JSON**: `[]`
10. **Error responses**: `403 Forbidden`
11. **Workflow impact**: None
12. **Deprecated old APIs**: `GET /api/vendors/{vendorName}/complaints`
13. **Authentication requirement**: Required.
14. **Endpoint Category**: VENDOR

### 2.5 Resolve Complaint
1. **Endpoint URL**: `/api/vendors/complaints/resolve`
2. **HTTP Method**: `PUT`
3. **Purpose of API**: Allows a vendor to mark an assigned complaint as resolved.
4. **Required Role/RBAC**: `VENDOR`, `VENDOR_ADMIN`, `MANAGER`
5. **Request Headers**: `Authorization: Bearer <token>`
6. **Request Body DTO**: `VendorResolveRequestDTO`
7. **Full sample request JSON**:
```json
{
  "complaintId": 10,
  "resolved": true,
  "resolutionRemarks": "Battery replaced successfully."
}
```
8. **Response structure**: `String`
9. **Full sample response JSON**: `"Complaint resolved"`
10. **Error responses**: `403 Forbidden`, `404 Not Found`
11. **Workflow impact**: Completes the vendor task in Camunda BPMN.
12. **Deprecated old APIs**: `PUT /api/vendors/complaints/{complaintId}/resolve`
13. **Authentication requirement**: Required.
14. **Endpoint Category**: VENDOR

---

## 3. Manager APIs (`ManagerController` & `ManagerDashboardController`)

### 3.1 Get Escalated Complaints
1. **Endpoint URL**: `/api/manager/complaints/escalated`
2. **HTTP Method**: `GET`
3. **Purpose of API**: Fetch complaints escalated to the manager.
4. **Required Role/RBAC**: `MANAGER`, `ADMIN`, `SUPER_ADMIN`
5. **Request Headers**: `Authorization: Bearer <token>`
6. **Request Body DTO**: None
7. **Full sample request JSON**: N/A
8. **Response structure**: `List<Map<String, Object>>`
9. **Full sample response JSON**: `[]`
10. **Error responses**: `403 Forbidden`
11. **Workflow impact**: None
12. **Deprecated old APIs**: None
13. **Authentication requirement**: Required.
14. **Endpoint Category**: MANAGER

### 3.2 Submit Manager Decision
1. **Endpoint URL**: `/api/manager/complaints/decision`
2. **HTTP Method**: `PUT`
3. **Purpose of API**: Process a decision (e.g., RETRY, APPROVE) on an escalated complaint.
4. **Required Role/RBAC**: `MANAGER`, `ADMIN`, `SUPER_ADMIN`
5. **Request Headers**: `Authorization: Bearer <token>`
6. **Request Body DTO**: `ManagerDecisionRequestDTO`
7. **Full sample request JSON**:
```json
{
  "complaintId": 10,
  "managerDecision": "RETRY"
}
```
8. **Response structure**: `String`
9. **Full sample response JSON**: `"Decision submitted"`
10. **Error responses**: `403 Forbidden`
11. **Workflow impact**: Signals the Camunda engine to follow the appropriate path (e.g., retry AI or vendor loop).
12. **Deprecated old APIs**: `PUT /api/manager/complaints/{complaintId}/decision`
13. **Authentication requirement**: Required.
14. **Endpoint Category**: MANAGER

### 3.3 Approve and Assign Complaint
1. **Endpoint URL**: `/api/manager/complaints/approve`
2. **HTTP Method**: `PUT`
3. **Purpose of API**: Approve an initial complaint and assign it to a team/vendor.
4. **Required Role/RBAC**: `MANAGER`, `ADMIN`, `SUPER_ADMIN`
5. **Request Headers**: `Authorization: Bearer <token>`
6. **Request Body DTO**: `ManagerApproveRequestDTO`
7. **Full sample request JSON**:
```json
{
  "complaintId": 10,
  "teamName": "Mechanic Inc"
}
```
8. **Response structure**: `Complaint`
9. **Full sample response JSON**: `{"id": 10, "status": "ASSIGNED"}`
10. **Error responses**: `403 Forbidden`
11. **Workflow impact**: Triggers vendor assignment in the workflow.
12. **Deprecated old APIs**: `PUT /api/manager/complaints/{complaintId}/approve`
13. **Authentication requirement**: Required.
14. **Endpoint Category**: MANAGER

### 3.4 Reject Complaint
1. **Endpoint URL**: `/api/manager/complaints/reject`
2. **HTTP Method**: `PUT`
3. **Purpose of API**: Reject an invalid or duplicate complaint.
4. **Required Role/RBAC**: `MANAGER`, `ADMIN`, `SUPER_ADMIN`
5. **Request Headers**: `Authorization: Bearer <token>`
6. **Request Body DTO**: `ComplaintDetailsRequestDTO`
7. **Full sample request JSON**:
```json
{
  "complaintId": 10
}
```
8. **Response structure**: `Complaint`
9. **Full sample response JSON**: `{"id": 10, "status": "REJECTED"}`
10. **Error responses**: `403 Forbidden`
11. **Workflow impact**: Terminates workflow.
12. **Deprecated old APIs**: `PUT /api/manager/complaints/{complaintId}/reject`
13. **Authentication requirement**: Required.
14. **Endpoint Category**: MANAGER

---

## 4. Workflow APIs (`WorkflowController`)

### 4.1 Submit User Workflow Response
1. **Endpoint URL**: `/api/workflow/user-response`
2. **HTTP Method**: `POST`
3. **Purpose of API**: Complete a Camunda user task for the driver/user.
4. **Required Role/RBAC**: `USER`, `DRIVER`
5. **Request Headers**: `Authorization: Bearer <token>`
6. **Request Body DTO**: `WorkflowTaskRequestDTO`
7. **Full sample request JSON**:
```json
{
  "taskId": "task-uuid-1234",
  "resolved": false,
  "continueAi": true
}
```
8. **Response structure**: `String`
9. **Full sample response JSON**: `"User response submitted successfully"`
10. **Error responses**: `403 Forbidden`, `404 Not Found` (Task not found)
11. **Workflow impact**: Completes task, populates variables `resolved` and `continueAi`.
12. **Deprecated old APIs**: `POST /api/workflow/user-response/{taskId}`
13. **Authentication requirement**: Required.
14. **Endpoint Category**: DRIVER

### 4.2 Submit Vendor Workflow Response
1. **Endpoint URL**: `/api/workflow/vendor-response`
2. **HTTP Method**: `POST`
3. **Purpose of API**: Complete a Camunda user task for the vendor.
4. **Required Role/RBAC**: `VENDOR`, `VENDOR_ADMIN`
5. **Request Headers**: `Authorization: Bearer <token>`
6. **Request Body DTO**: `WorkflowTaskRequestDTO`
7. **Full sample request JSON**:
```json
{
  "taskId": "task-uuid-1234",
  "vendorResolved": true
}
```
8. **Response structure**: `String`
9. **Full sample response JSON**: `"Vendor response submitted successfully"`
10. **Error responses**: `403 Forbidden`, `404 Not Found`
11. **Workflow impact**: Completes task, populates variable `vendorResolved`.
12. **Deprecated old APIs**: `POST /api/workflow/vendor-response/{taskId}`
13. **Authentication requirement**: Required.
14. **Endpoint Category**: VENDOR

### 4.3 Submit Manager Workflow Response
1. **Endpoint URL**: `/api/workflow/manager-response`
2. **HTTP Method**: `POST`
3. **Purpose of API**: Complete a Camunda user task for the manager.
4. **Required Role/RBAC**: `MANAGER`, `ADMIN`, `SUPER_ADMIN`
5. **Request Headers**: `Authorization: Bearer <token>`
6. **Request Body DTO**: `WorkflowTaskRequestDTO`
7. **Full sample request JSON**:
```json
{
  "taskId": "task-uuid-1234",
  "managerDecision": "APPROVE"
}
```
8. **Response structure**: `String`
9. **Full sample response JSON**: `"Manager response submitted successfully"`
10. **Error responses**: `403 Forbidden`, `404 Not Found`
11. **Workflow impact**: Completes task, populates variable `managerDecision`.
12. **Deprecated old APIs**: `POST /api/workflow/manager-response/{taskId}`
13. **Authentication requirement**: Required.
14. **Endpoint Category**: MANAGER

---

## 5. Audit APIs (`AuditLogController`)

### 5.1 Fetch Logs by Complaint ID
1. **Endpoint URL**: `/api/audit-logs/complaint`
2. **HTTP Method**: `POST`
3. **Purpose of API**: View historical lifecycle changes for a complaint.
4. **Required Role/RBAC**: `ADMIN`, `SUPER_ADMIN`
5. **Request Headers**: `Authorization: Bearer <token>`
6. **Request Body DTO**: `AuditLogRequestDTO`
7. **Full sample request JSON**:
```json
{
  "complaintId": 10
}
```
8. **Response structure**: `List<AuditLog>`
9. **Full sample response JSON**: `[]`
10. **Error responses**: `403 Forbidden`
11. **Workflow impact**: None
12. **Deprecated old APIs**: `GET /api/audit-logs/complaint/{complaintId}`
13. **Authentication requirement**: Required.
14. **Endpoint Category**: ADMIN

### 5.2 Fetch Logs by Action
1. **Endpoint URL**: `/api/audit-logs/action`
2. **HTTP Method**: `POST`
3. **Purpose of API**: View historical logs filtered by a specific system action.
4. **Required Role/RBAC**: `ADMIN`, `SUPER_ADMIN`
5. **Request Headers**: `Authorization: Bearer <token>`
6. **Request Body DTO**: `AuditLogActionRequestDTO`
7. **Full sample request JSON**:
```json
{
  "action": "ESCALATED"
}
```
8. **Response structure**: `List<AuditLog>`
9. **Full sample response JSON**: `[]`
10. **Error responses**: `403 Forbidden`
11. **Workflow impact**: None
12. **Deprecated old APIs**: `GET /api/audit-logs/action/{action}`
13. **Authentication requirement**: Required.
14. **Endpoint Category**: ADMIN

---

## 6. Config APIs (`ConfigController`)

### 6.1 Get Config Fields
1. **Endpoint URL**: `/api/config`
2. **HTTP Method**: `GET`
3. **Purpose of API**: Expose frontend dynamic form configurations.
4. **Required Role/RBAC**: `USER`, `DRIVER`, `VENDOR`, `VENDOR_ADMIN`, `MANAGER`, `ADMIN`, `SUPER_ADMIN`
5. **Request Headers**: `Authorization: Bearer <token>`
6. **Request Body DTO**: None
7. **Full sample request JSON**: N/A
8. **Response structure**: `List<FieldConfigDTO>`
9. **Full sample response JSON**: `[]`
10. **Error responses**: `403 Forbidden`
11. **Workflow impact**: None
12. **Deprecated old APIs**: None
13. **Authentication requirement**: Required.
14. **Endpoint Category**: ALL (Authenticated)
