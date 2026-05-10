# EV Fleet Mobility - API Documentation & Testing Guide

## 1. Project Overview
The EV Fleet Mobility system is a **Modular Monolith** application designed to handle user onboarding for drivers and organizations, vehicle management, and a comprehensive complaint resolution system integrated with AI and human-in-the-loop workflows (Camunda).

### Core Modules:
- **`useronboarding`**: Authentication, Profile Management (Individual/Organization), and Admin Approvals.
- **`complaintresolution`**: Lifecycle of a complaint from driver report to vendor resolution and manager oversight.

---

## 2. Architecture & Tech Stack
- **Framework**: Spring Boot 3.4.0 (Java 21)
- **Database**: PostgreSQL
- **Security**: Spring Security with JWT (Stateless)
- **Workflow**: Camunda BPMN Engine
- **Storage**: AWS S3 (for documents) & Database (BYTEA for PAN cards)
- **Communication**: REST API with JSON & Multipart-form-data

---

## 3. Authentication Flow
All secured endpoints require a `Authorization: Bearer <JWT_TOKEN>` header.

### **Signup**
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
  "companyName": "Alpha Fleet"
}
```
*Note: `role` can be `DRIVER`, `VENDOR_ADMIN`, `MANAGER`, `ADMIN`, `SUPER_ADMIN`.*

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

## 4. User Onboarding & Profiles

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

### **Admin: Update User Status**
- **Endpoint**: `POST /api/status/update`
- **Roles**: `SUPER_ADMIN`, `ADMIN`
- **Request Body**:
```json
{
  "targetUserId": 10,
  "status": "APPROVED",
  "type": "USER"
}
```

---

## 5. Complaint Resolution Workflow

### **Step 1: Raise a Complaint (Driver)**
- **Endpoint**: `POST /api/complaints`
- **Request Body**:
```json
{
  "complaintData": {
    "issueCategory": "BATTERY",
    "issueDescription": "The battery is draining too fast and won't charge above 80%",
    "location": "Downtown Hub"
  },
  "latitude": 12.9716,
  "longitude": 77.5946
}
```

### **Step 2: AI Assistance & User Feedback**
After raising a complaint, an AI processes it. The user must provide feedback if the AI suggests a fix.
- **Endpoint**: `POST /api/workflow/user-response`
- **Request Body**:
```json
{
  "complaintId": 1,
  "resolved": false,
  "continueAi": false,
  "userFollowUp": "The AI suggestion didn't work, please assign a technician."
}
```

### **Step 3: Manager Approval & Vendor Assignment**
- **Endpoint**: `PUT /api/complaints/assign`
- **Request Body**:
```json
{
  "complaintId": 1,
  "vendorId": 5
}
```

### **Step 4: Vendor Resolution**
- **Update Status**: `PUT /api/complaints/status` (Body: `{"complaintId": 1, "status": "IN_REPAIR"}`)
- **Resolve**: `PUT /api/complaints/resolve`
```json
{
  "complaintId": 1,
  "resolved": true,
  "resolutionRemarks": "Replaced faulty battery module. Tested and verified."
}
```

---

## 6. Testing Scenarios

### **Scenario A: End-to-End Individual Onboarding**
1. **Signup** as `DRIVER` with `userType: INDIVIDUAL`.
2. **Login** to get the JWT.
3. **Complete Profile** using the `/api/profile/complete` endpoint with a PAN card image.
4. **Admin Approval**: Login as `ADMIN` and approve the driver via `/api/status/update`.
5. **Verify**: Call `/api/profile/me` as the driver to see `approvalStatus: APPROVED`.

### **Scenario B: Escalated Complaint Flow**
1. **Driver** raises a complaint.
2. **Camunda Workflow** starts. AI fails to resolve.
3. **Driver** submits `/api/workflow/user-response` with `resolved: false`.
4. **Manager** views escalated complaints via `GET /api/complaints`.
5. **Manager** assigns a Vendor via `/api/complaints/assign`.
6. **Vendor** resolves the complaint.
7. **Verify Audit Logs**: Call `/api/complaints/audit-logs` to see the full history.

---

## 7. Key Configuration (application.properties)
- **DB_URL**: Database connection string.
- **JWT_SECRET**: Secret key for token signing.
- **AI_SERVICE_URL**: External service for AI analysis (Ngrok bridge).
- **Camunda Logs**: Set to `ERROR` for cleaner testing console.
