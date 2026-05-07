# EV Fleet Mobility - useronboarding Module Reference Architecture

**Last Updated**: May 7, 2026  
**Purpose**: Complete reference guide for proper architecture patterns used in useronboarding module that complaintresolution should match

---

## 1. Complete Package Structure & Directory Tree

```
com.evfleetmobility/
│
├── common/                                    (SHARED ACROSS ALL MODULES)
│   ├── config/
│   │   ├── JacksonConfig.java
│   │   └── S3Config.java
│   │
│   ├── exception/                            (7 exception classes)
│   │   ├── GlobalExceptionHandler.java       ⭐ Centralized exception handling
│   │   ├── InvalidPasswordException.java
│   │   ├── ServiceHistoryNotFoundException.java
│   │   ├── TokenException.java
│   │   ├── UserAlreadyExistsException.java
│   │   ├── UserNotFoundException.java
│   │   └── VehicleNotFoundException.java
│   │
│   ├── response/
│   │   ├── ApiResponse<T>.java               ⭐ Generic response wrapper
│   │   └── ErrorResponse.java
│   │
│   └── security/                             (JWT + Spring Security)
│       ├── JwtConfig.java
│       ├── JwtFilter.java                    ⭐ Authentication filter
│       ├── JwtUtil.java                      ⭐ Token generation/validation
│       └── SecurityConfig.java               ⭐ Security configuration
│
└── useronboarding/
    │
    ├── adminservices/                        (SERVICE LAYER ONLY - NO DATA MODEL)
    │   ├── controller/
    │   │   ├── UserController.java
    │   │   └── StatusController.java
    │   │
    │   ├── dto/
    │   │   ├── UserDetailsResponse.java
    │   │   ├── CompanyDetailsResponse.java
    │   │   └── StatusRequest.java
    │   │
    │   └── service/
    │       ├── AdminService.java             (Interface)
    │       └── impl/
    │           └── AdminServiceImpl.java      (Aggregates from other services)
    │
    ├── authservices/                         (FULL STACK)
    │   ├── controller/
    │   │   └── AuthController.java
    │   │       ├── /api/auth/signup
    │   │       ├── /api/auth/login
    │   │       ├── /api/auth/refresh
    │   │       └── /api/auth/logout
    │   │
    │   ├── dto/
    │   │   ├── SignupRequest.java            (with validation: @Email, @NotBlank, @Size)
    │   │   ├── LoginRequest.java
    │   │   ├── RefreshRequest.java
    │   │   ├── AuthResponse.java             (accessToken, refreshToken)
    │   │   └── UserResponse.java
    │   │
    │   ├── entity/
    │   │   ├── User.java                     (Core JPA entity)
    │   │   │   ├── id, username, email, password, role
    │   │   │   ├── userType (ENUM)
    │   │   │   ├── approvalStatus (ENUM)
    │   │   │   ├── relationships: IndividualDetails (1:1), OrganizationDetails (M:1)
    │   │   │   └── cascading: PERSIST, MERGE
    │   │   ├── UserType.java                 (ENUM: INDIVIDUAL, ORGANIZATION)
    │   │   └── ApprovalStatus.java           (ENUM: PENDING, APPROVED, REJECTED)
    │   │
    │   ├── repository/
    │   │   └── UserRepository.java
    │   │       ├── findByEmail(String): Optional<User>
    │   │       ├── existsByEmail(String): boolean
    │   │       ├── findByApprovalStatus(ApprovalStatus): List<User>
    │   │       ├── findByUserType(UserType): List<User>
    │   │       └── findByUserTypeAndApprovalStatus(...): List<User>
    │   │
    │   └── service/
    │       ├── AuthService.java              (Interface)
    │       │   ├── signup(SignupRequest): UserResponse
    │       │   ├── login(LoginRequest): AuthResponse
    │       │   └── refresh(RefreshRequest): AuthResponse
    │       │
    │       └── impl/
    │           └── AuthServiceImpl.java       (@Service, @Transactional)
    │               ├── Dependencies: UserRepository, IndividualRepository,
    │               │                 OrganizationRepository, PasswordEncoder, JwtUtil
    │               └── Business logic for user registration & authentication
    │
    ├── documentservices/                     (FULL STACK)
    │   ├── controller/
    │   │   └── DocumentController.java
    │   │       ├── POST /api/documents (upload)
    │   │       ├── GET /api/documents/{id} (download presigned URL)
    │   │       └── GET /api/documents (list)
    │   │
    │   ├── dto/
    │   │   ├── DocumentResponse.java
    │   │   └── DocumentDownloadResponse.java (Presigned URL)
    │   │
    │   ├── entity/
    │   │   ├── Document.java                 (JPA entity with S3 integration)
    │   │   │   ├── id, user (FK), documentType (ENUM)
    │   │   │   ├── fileUrl, metadata, remarks
    │   │   │   ├── status (ENUM), stage (ENUM)
    │   │   │   └── uploadedAt (LocalDateTime)
    │   │   ├── DocumentType.java             (ENUM)
    │   │   ├── DocumentStatus.java           (ENUM: PENDING, APPROVED, REJECTED)
    │   │   └── DocumentStage.java            (ENUM)
    │   │
    │   ├── repository/
    │   │   └── DocumentRepository.java
    │   │       ├── findByUserId(Long): List<Document>
    │   │       ├── findByStatus(DocumentStatus): List<Document>
    │   │       └── Custom S3-related queries
    │   │
    │   └── service/
    │       ├── DocumentService.java          (Interface)
    │       │   ├── uploadDocument(...): DocumentResponse
    │       │   ├── downloadDocument(Long): DocumentDownloadResponse
    │       │   └── getDocuments(Long): List<DocumentResponse>
    │       │
    │       └── impl/
    │           └── DocumentServiceImpl.java   (@Service, @Transactional)
    │               ├── Dependencies: DocumentRepository, UserRepository,
    │               │                 S3Client, S3Presigner
    │               └── S3 upload/download with presigned URLs
    │
    ├── profileservices/                      (FULL STACK)
    │   ├── controller/
    │   │   └── ProfileController.java
    │   │       ├── POST /api/profiles
    │   │       ├── GET /api/profiles/{id}
    │   │       └── PUT /api/profiles/{id}
    │   │
    │   ├── dto/
    │   │   ├── ProfileRequest.java
    │   │   └── ProfileResponse.java
    │   │
    │   ├── entity/
    │   │   ├── IndividualDetails.java        (1:1 with User)
    │   │   │   ├── id, user (PK FK), fullName, phoneNumber
    │   │   │   ├── personalEmail, countryCode
    │   │   │   ├── addressLine1, addressLine2, panNumber
    │   │   │   ├── latitude, longitude, gender
    │   │   │   └── companyApprovalStatus (ENUM)
    │   │   │
    │   │   └── OrganizationDetails.java      (M:1 with User - shared org)
    │   │       ├── id, companyName, phoneNumber, email
    │   │       ├── addressLine1, addressLine2
    │   │       ├── panNumber, gstNumber, cin
    │   │       ├── approvalStatus (ENUM)
    │   │       └── users (One org many users)
    │   │
    │   ├── repository/
    │   │   ├── IndividualRepository.java
    │   │   │   ├── findByUser(User): Optional<IndividualDetails>
    │   │   │   └── existsByPhoneNumber(String): boolean
    │   │   │
    │   │   └── OrganizationRepository.java
    │   │       ├── findByCompanyName(String): Optional<OrganizationDetails>
    │   │       ├── existsByPhoneNumber(String): boolean
    │   │       └── findByApprovalStatus(ApprovalStatus): List<OrganizationDetails>
    │   │
    │   └── service/
    │       ├── ProfileService.java           (Interface)
    │       │   ├── completeProfile(Long, ProfileRequest): String
    │       │   ├── getProfile(Long): ProfileResponse
    │       │   └── updateProfile(Long, ProfileRequest): ProfileResponse
    │       │
    │       └── impl/
    │           └── ProfileServiceImpl.java    (@Service, @Transactional)
    │               ├── Dependencies: UserRepository, OrganizationRepository,
    │               │                 DocumentService
    │               └── Handles both INDIVIDUAL and ORGANIZATION profiles
    │
    └── vehicleservices/                      (FULL STACK - 2 services)
        ├── controller/
        │   ├── VehicleController.java        (@PreAuthorize("hasRole('ADMIN')"))
        │   │   ├── POST /api/vehicles
        │   │   ├── PUT /api/vehicles/{id}
        │   │   ├── DELETE /api/vehicles/{id}
        │   │   ├── GET /api/vehicles/{id}
        │   │   └── GET /api/vehicles
        │   │
        │   └── ServiceHistoryController.java
        │       ├── POST /api/service-history
        │       ├── GET /api/service-history/{vehicleId}
        │       └── GET /api/service-history/{id}
        │
        ├── dto/
        │   ├── VehicleRequest.java
        │   ├── VehicleResponse.java
        │   ├── ServiceHistoryRequest.java
        │   └── ServiceHistoryResponse.java
        │
        ├── entity/
        │   ├── Vehicle.java                  (JPA entity)
        │   │   ├── id, registrationNumber, modelName, manufacturer
        │   │   ├── purchaseDate, status (ENUM)
        │   │   └── organizationId (FK)
        │   │
        │   ├── ServiceHistory.java
        │   │   ├── id, vehicle (FK), serviceType (ENUM)
        │   │   ├── serviceDate, cost, remarks
        │   │   └── maintenanceDetails
        │   │
        │   ├── VehicleStatus.java            (ENUM)
        │   └── ServiceType.java              (ENUM)
        │
        ├── repository/
        │   ├── VehicleRepository.java
        │   │   ├── findByRegistrationNumber(String): Optional<Vehicle>
        │   │   ├── findByOrganizationId(Long): List<Vehicle>
        │   │   └── findByStatus(VehicleStatus): List<Vehicle>
        │   │
        │   └── ServiceHistoryRepository.java
        │       ├── findByVehicleId(Long): List<ServiceHistory>
        │       └── findByServiceType(ServiceType): List<ServiceHistory>
        │
        └── service/
            ├── VehicleService.java           (Interface)
            │   ├── addVehicle(VehicleRequest): VehicleResponse
            │   ├── updateVehicle(Long, VehicleRequest): VehicleResponse
            │   ├── deleteVehicle(Long): void
            │   ├── getVehicleById(Long): VehicleResponse
            │   └── getAllVehicles(): List<VehicleResponse>
            │
            ├── ServiceHistoryService.java    (Interface)
            │   ├── addServiceHistory(...): ServiceHistoryResponse
            │   ├── getServiceHistory(Long): List<ServiceHistoryResponse>
            │   └── getServiceHistoryById(Long): ServiceHistoryResponse
            │
            └── impl/
                ├── VehicleServiceImpl.java    (@Service, @Transactional)
                │   └── Dependencies: VehicleRepository
                │
                └── ServiceHistoryServiceImpl.java
                    └── Dependencies: ServiceHistoryRepository, VehicleRepository
```

---

## 2. Class Counts by Layer & Module

### Horizontal View (by module)

| Module | Controllers | Service (I) | Service (Impl) | Entities | Repositories | DTOs | Enums | Total |
|--------|:-----------:|:-----------:|:--------------:|:--------:|:------------:|:----:|:-----:|:-----:|
| **adminservices** | 2 | 1 | 1 | 0 | 0 | 3 | 0 | **7** |
| **authservices** | 1 | 1 | 1 | 3 | 1 | 5 | 2 | **14** |
| **documentservices** | 1 | 1 | 1 | 4 | 1 | 2 | 3 | **13** |
| **profileservices** | 1 | 1 | 1 | 2 | 2 | 2 | 0 | **9** |
| **vehicleservices** | 2 | 2 | 2 | 4 | 2 | 4 | 2 | **18** |
| **useronboarding total** | **7** | **6** | **6** | **13** | **6** | **16** | **7** | **61** |
| **common module** | **0** | **1** | **1** | **0** | **0** | **2** | **0** | **15** |
| **GRAND TOTAL** | **7** | **7** | **7** | **13** | **6** | **18** | **7** | **76** |

### Vertical View (by layer)

| Layer | Count | Components |
|-------|:-----:|-----------|
| **Controllers** | 7 | REST endpoints with @RequestMapping |
| **Service Interfaces** | 6 | Abstraction contracts |
| **Service Implementations** | 6 | Business logic (@Service, @Transactional) |
| **Entities** | 13 | JPA @Entity classes with relationships |
| **Repositories** | 6 | Spring Data JpaRepository |
| **DTOs** | 18 | Request/Response data transfer objects |
| **Enums** | 7 | Type-safe enumerations |
| **Exception Handlers** | 1 | GlobalExceptionHandler (@RestControllerAdvice) |
| **Config/Util Classes** | 8 | Security, Jackson, S3 config, JWT utils |

---

## 3. Package Naming Patterns & Conventions

### Pattern Formula
```
com.evfleetmobility.{TOP_LEVEL_MODULE}.{SERVICE_MODULE}.{LAYER}
                     └─ useronboarding ──┬─ adminservices
                                         ├─ authservices
                                         ├─ documentservices
                                         ├─ profileservices
                                         └─ vehicleservices
```

### Layer Packages (within each service)

```
authservices/
├── controller/           # API endpoints
├── dto/                  # Data transfer objects (Request/Response)
├── entity/               # JPA domain models
├── repository/           # Data access layer
├── service/              # Business logic interface
└── service/impl/         # Implementation
```

### Naming Conventions

| Layer | Class Name Pattern | Example | Notes |
|-------|-------------------|---------|-------|
| **Controller** | `{Domain}Controller` | `AuthController` | REST endpoints, @RequestMapping |
| **Service (Interface)** | `{Domain}Service` | `AuthService` | Contract definition |
| **Service (Implementation)** | `{Domain}ServiceImpl` | `AuthServiceImpl` | @Service, dependencies injected |
| **Entity** | `{Domain}` | `User`, `Document` | @Entity, JPA mapping |
| **Entity (Enum)** | `{AttributeName}` | `UserType`, `ApprovalStatus` | Attribute-based naming |
| **Repository** | `{Domain}Repository` | `UserRepository` | Extends JpaRepository |
| **DTO (Request)** | `{Domain}Request` | `SignupRequest`, `VehicleRequest` | Input DTO with @Valid |
| **DTO (Response)** | `{Domain}Response` | `UserResponse`, `VehicleResponse` | Output DTO, no validation |
| **Exception** | `{Type}Exception` | `UserNotFoundException` | Extends RuntimeException |

### Common Package Naming Anti-Patterns (NOT Used)

❌ No util, helper, or common packages within service modules  
❌ No separate models package (entities co-located with layer)  
❌ No separate request/response subdirectories (all in dto/)  
❌ No base classes or abstract services in modules  

---

## 4. Security & Auth Integration Points

### JWT Flow

```
┌─────────────────────────────────────────────────────────────┐
│ REQUEST: POST /api/auth/signup                              │
├─────────────────────────────────────────────────────────────┤
│  1. AuthController receives SignupRequest with @Valid       │
│  2. AuthServiceImpl.signup() validates data                  │
│  3. PasswordEncoder.encode() hashes password                │
│  4. User + related entities (IndividualDetails/Org) saved   │
│  5. UserResponse returned                                   │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ REQUEST: POST /api/auth/login                               │
├─────────────────────────────────────────────────────────────┤
│  1. AuthController receives LoginRequest                    │
│  2. AuthServiceImpl validates email & password               │
│  3. passwordEncoder.matches() compares stored hash          │
│  4. JwtUtil.generateAccessToken(userId, role) creates JWT  │
│  5. JwtUtil.generateRefreshToken(userId) creates refresh   │
│  6. AuthResponse{accessToken, refreshToken} returned       │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ REQUEST: POST /api/auth/refresh                             │
├─────────────────────────────────────────────────────────────┤
│  1. AuthController receives RefreshRequest                  │
│  2. JwtUtil validates token type = "REFRESH"                │
│  3. Extracts userId from token claims                       │
│  4. Generates new access token                              │
│  5. AuthResponse returned                                   │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ REQUEST: GET /api/vehicles (with JWT)                       │
├─────────────────────────────────────────────────────────────┤
│  1. JwtFilter intercepts request                            │
│  2. Extracts "Authorization: Bearer <token>" header         │
│  3. JwtUtil validates token signature & expiry              │
│  4. Creates SecurityContext with UserDetails                │
│  5. Request passes through @PreAuthorize("hasRole(...)")    │
│  6. If valid, proceeds to VehicleController.getAllVehicles │
└─────────────────────────────────────────────────────────────┘
```

### JWT Token Structure

**Access Token Claims**:
```json
{
  "sub": "ROLE_ADMIN",           // Subject = role (unusual choice)
  "userId": 123,                 // Custom claim: actual user ID
  "type": "ACCESS",              // Token type
  "iat": 1609459200,             // Issued at
  "exp": 1609462800              // Expiration (typically 1 hour)
}
```

**Refresh Token Claims**:
```json
{
  "sub": "123",                  // Subject = user ID
  "type": "REFRESH",             // Token type
  "iat": 1609459200,             // Issued at
  "exp": 1609545600              // Expiration (typically 7 days)
}
```

### Security Annotations Usage

```java
// Class-level access control (entire controller)
@PreAuthorize("hasRole('ADMIN')")
public class VehicleController { }

// Method-level access control
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'VENDOR_ADMIN')")
public ResponseEntity<ApiResponse<List<Users>>> getUsers() { }

// Accessing authenticated user
public ResponseEntity<...> getUsers(Principal principal) {
    Long userId = Long.valueOf(principal.getName()); // From SecurityContext
}
```

### Exception Handling for Auth

```
Request → JwtFilter validates token
  ├─ ✅ Valid → Set SecurityContext, proceed
  ├─ ❌ Expired → GlobalExceptionHandler → ExpiredJwtException → 401
  ├─ ❌ Invalid → GlobalExceptionHandler → JwtException → 401
  ├─ ❌ Malformed → GlobalExceptionHandler → JwtException → 401
  └─ ❌ AccessDenied → GlobalExceptionHandler → AccessDeniedException → 403
```

### SecurityConfig Features

```java
@Configuration
@EnableMethodSecurity                    // Enables @PreAuthorize
public class SecurityConfig {
    
    // HTTP Security chain
    HttpSecurity.sessionManagementPolicy(STATELESS)      // JWT - no sessions
    HttpSecurity.cors()                                   // CORS configuration
    HttpSecurity.csrf().disable()                         // Stateless API
    HttpSecurity.addFilterBefore(JwtFilter)               // JWT filter chain
    
    // Password encoding
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();               // BCrypt hashing
    }
    
    // Authentication entry point for unauthorized
    // Access denied handler for forbidden
}
```

---

## 5. DTO Structure & Patterns

### Request DTOs (Input)

**Location**: `*.dto` with naming: `{Domain}Request`

```java
public class SignupRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    private String role;                           // Not validated - enum later
    private String userType;                       // Not validated - enum later
    private String phoneNumber;
    private String countryCode;
    private String gender;
    private String companyName;
    
    // Standard getters/setters (NO Lombok @Data)
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    // ... more getters/setters
}
```

**Validation Annotations Available**:
- `@NotBlank`, `@NotNull`, `@NotEmpty` - null/empty checks
- `@Email` - email format validation
- `@Size(min, max)` - length constraints
- `@Pattern(regex)` - regex validation
- `@Valid` - nested object validation

### Response DTOs (Output)

**Location**: `*.dto` with naming: `{Domain}Response`

```java
public class UserResponse {
    private String username;
    private String email;
    
    public UserResponse(String username, String email) {
        this.username = username;
        this.email = email;
    }
    
    public String getUsername() { return username; }
    public String getEmail() { return email; }
}
```

**Characteristics**:
- ✅ No validation annotations
- ✅ All fields optional (no required checks)
- ✅ Standard getters/setters
- ✅ May include constructor for ease

### Auth-Specific DTOs

```java
// Request
public class LoginRequest {
    @Email private String email;
    @NotBlank private String password;
}

// Response
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    // Constructor + getters
}
```

### Relationship DTOs

Some DTOs include nested data from related entities:

```java
public class ProfileResponse {
    private Long userId;
    private String fullName;
    private String email;
    
    // Nested: address details
    private String addressLine1;
    private String addressLine2;
    private String countryCode;
    
    // Profile can include document references
    private List<DocumentResponse> documents;
}
```

---

## 6. Common Utilities & Base Classes

### Exception Classes (in `common.exception`)

**Hierarchy**:
```
java.lang.RuntimeException
├── UserNotFoundException
│   └── Message: "User not found" | "User with email {X} not found"
│   └── HTTP Status: 404
│
├── UserAlreadyExistsException
│   └── Message: "Email is already registered" | "User already exists"
│   └── HTTP Status: 409
│
├── InvalidPasswordException
│   └── Message: "Invalid password"
│   └── HTTP Status: 401
│
├── TokenException
│   └── Message: "Invalid token type" | "Token expired" | "Token validation failed"
│   └── HTTP Status: 400 or 401
│
├── VehicleNotFoundException
│   └── Message: "Vehicle not found"
│   └── HTTP Status: 404
│
├── ServiceHistoryNotFoundException
│   └── Message: "Service history not found"
│   └── HTTP Status: 404
│
└── [Custom exceptions can extend RuntimeException]
```

**Implementation Pattern**:
```java
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}

// Usage in service
if (!found) {
    throw new UserNotFoundException("User with email " + email + " not found");
}
```

### Global Exception Handler

**Location**: `common.exception.GlobalExceptionHandler`

```java
@RestControllerAdvice                                    // Global for all controllers
public class GlobalExceptionHandler {
    
    @ExceptionHandler(UserNotFoundException.class)       // Maps to exception type
    public ResponseEntity<ErrorResponse> handle(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(404, "User Not Found", ex.getMessage()));
    }
    
    @ExceptionHandler({ExpiredJwtException.class})
    public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse(401, "Token Expired", 
                "Your token has expired. Please refresh."));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
            
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(400, "Validation Failed", errors));
    }
}
```

### Response Classes

**ApiResponse** (for successful responses):
```java
@Getter
public class ApiResponse<T> {
    private String message;      // e.g., "Signup successful"
    private T data;              // Generic data payload
    
    public ApiResponse(String msg, T data) {
        this.message = msg;
        this.data = data;
    }
}

// Usage in controller
return ResponseEntity.ok(
    new ApiResponse<>("User created successfully", userResponse)
);
```

**ErrorResponse** (for error responses):
```java
public class ErrorResponse {
    private int status;                  // HTTP status code
    private String error;                // Error type
    private String message;              // Detailed message
    
    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }
}

// Example error response body
{
  "status": 404,
  "error": "User Not Found",
  "message": "User with email user@example.com not found"
}
```

### Utility Annotations

**Lombok** (reduces boilerplate):
```java
@RequiredArgsConstructor          // Generates constructor for final fields
@Getter                           // Generates getters
@Setter                           // Generates setters
public class MyClass {
    private final SomeService service;  // Injected via constructor
}
```

**Validation** (jakarta.validation):
```java
@NotBlank(message = "Email is required")
@Email(message = "Valid email required")
private String email;

@Size(min = 6, message = "Minimum 6 characters")
private String password;
```

**JPA** (jakarta.persistence):
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Enumerated(EnumType.STRING)
    private UserType userType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id")
    private Organization org;
}
```

---

## 7. Integration with com.evfleetmobility.common

### What's Shared

```
common/
├── config/
│   ├── JacksonConfig.java         ← Configure Jackson for JSON serialization
│   └── S3Config.java              ← AWS S3 client configuration
│
├── exception/
│   ├── GlobalExceptionHandler.java ← Handles all exceptions from all modules
│   ├── UserNotFoundException.java
│   ├── TokenException.java
│   ├── UserAlreadyExistsException.java
│   ├── InvalidPasswordException.java
│   ├── VehicleNotFoundException.java
│   └── ServiceHistoryNotFoundException.java
│
├── response/
│   ├── ApiResponse<T>.java        ← Standard success response wrapper
│   └── ErrorResponse.java         ← Standard error response format
│
└── security/
    ├── JwtUtil.java               ← Token generation & validation
    ├── JwtFilter.java             ← Request filter for JWT extraction
    ├── JwtConfig.java             ← JWT configuration
    └── SecurityConfig.java        ← Spring Security setup (@EnableMethodSecurity)
```

### How Services Integrate

```
authservices (USES common)
├── Imports:
│   ├── com.evfleetmobility.common.response.ApiResponse
│   ├── com.evfleetmobility.common.exception.*
│   └── com.evfleetmobility.common.security.JwtUtil
│
├── In AuthController:
│   ├── Returns: ResponseEntity<ApiResponse<UserResponse>>
│   └── Throws: (caught by GlobalExceptionHandler)
│
├── In AuthServiceImpl:
│   ├── Throws: UserNotFoundException, UserAlreadyExistsException, InvalidPasswordException
│   ├── Uses: jwtUtil.generateAccessToken(), jwtUtil.generateRefreshToken()
│   └── All exceptions caught by @RestControllerAdvice

documentservices (USES common)
├── Imports:
│   ├── com.evfleetmobility.common.response.ApiResponse
│   ├── com.evfleetmobility.common.exception.UserNotFoundException
│   └── com.evfleetmobility.common.security.JwtUtil (via JWT filter)
│
├── In DocumentController:
│   ├── Returns: ResponseEntity<ApiResponse<DocumentResponse>>
│   └── Protected by JwtFilter + @PreAuthorize

vehicleservices (USES common)
├── Imports:
│   ├── com.evfleetmobility.common.response.ApiResponse
│   ├── com.evfleetmobility.common.exception.VehicleNotFoundException
│   ├── com.evfleetmobility.common.exception.ServiceHistoryNotFoundException
│   └── com.evfleetmobility.common.security (for @PreAuthorize)
│
├── In VehicleController:
│   ├── @PreAuthorize("hasRole('ADMIN')")  ← Uses security from common
│   └── Returns: ResponseEntity<ApiResponse<VehicleResponse>>
```

### Dependency Injection Flow

```
ApplicationContext (Spring Boot)
│
├─ GlobalExceptionHandler (@RestControllerAdvice)
├─ SecurityConfig (@Configuration)
├─ JwtUtil (@Component)
├─ JwtFilter (filter bean)
│
└─ For each service:
   ├─ AuthController
   │  └─ requires: AuthService
   │
   ├─ AuthServiceImpl (@Service)
   │  └─ requires: UserRepository, PasswordEncoder, JwtUtil
   │
   ├─ ProfileServiceImpl (@Service)
   │  └─ requires: UserRepository, OrganizationRepository
   │
   └─ DocumentServiceImpl (@Service)
      └─ requires: DocumentRepository, S3Client, S3Presigner
```

### Exception Flow Example

```
Request: POST /api/auth/signup { "email": "existing@test.com" }

1. AuthController.signup() calls authService.signup()
2. AuthServiceImpl checks: userRepository.existsByEmail(email)
3. Found duplicate → throw new UserAlreadyExistsException("Email already registered")
4. Exception propagates up, NOT caught in service
5. Spring catches exception, routes to GlobalExceptionHandler
6. GlobalExceptionHandler.handleUserAlreadyExists() matches exception type
7. Returns: ResponseEntity.status(409).body(
     new ErrorResponse(409, "Duplicate Entry", "Email already registered")
   )
8. Response sent to client with status 409
```

---

## 8. Architectural Principles & Patterns

### Layered Architecture Pattern

```
┌─────────────────────────────────────────────────────────────┐
│ CLIENT (REST API)                                           │
├─────────────────────────────────────────────────────────────┤
│         Layer 1: CONTROLLER (@RestController)               │
│    (HTTP handling, request validation, routing)             │
├─────────────────────────────────────────────────────────────┤
│         Layer 2: SERVICE (Business Logic)                   │
│    (Orchestration, validation, transformations)             │
├─────────────────────────────────────────────────────────────┤
│         Layer 3: REPOSITORY (Data Access)                   │
│    (Database queries, persistence)                          │
├─────────────────────────────────────────────────────────────┤
│         Layer 4: DATABASE (PostgreSQL/MySQL)                │
└─────────────────────────────────────────────────────────────┘

Communication: Controller → Service → Repository → Database
Never: Controller directly → Database (violates layering)
Never: Shared service implementations (violates encapsulation)
```

### Service Isolation Pattern

```
authservices/          documentservices/        vehicleservices/
├─ Own entities       ├─ Own entities           ├─ Own entities
├─ Own repositories   ├─ Own repositories       ├─ Own repositories
├─ Own DTOs           ├─ Own DTOs               ├─ Own DTOs
├─ Own controllers    ├─ Own controllers        ├─ Own controllers
└─ Can use            └─ Can use                └─ Can use
   ↓                     ↓                          ↓
   Other services' repositories (UserRepository)
   Common module (security, exceptions, response)
```

### Dependency Injection Pattern

```java
@Service
@RequiredArgsConstructor                    // Constructor injection
public class MyServiceImpl implements MyService {
    private final DependencyA depA;         // Final fields (immutable)
    private final DependencyB depB;
    
    // Constructor generated by Lombok
    // public MyServiceImpl(DependencyA depA, DependencyB depB) { ... }
    
    public void doSomething() {
        depA.method();                      // No null checks needed
    }
}

// In Spring Config or Application context
@Bean
MyService myService(DependencyA a, DependencyB b) {
    return new MyServiceImpl(a, b);          // Explicit wiring
}
```

### Interface-First Design

```java
// Define contract first
public interface UserService {
    User findById(Long id);
    void save(User user);
}

// Implement contract
@Service
public class UserServiceImpl implements UserService {
    @Override
    public User findById(Long id) { ... }
    
    @Override
    public void save(User user) { ... }
}

// Inject interface, not implementation
@Controller
public class UserController {
    private final UserService service;  // Interface type
}
```

### Transaction Boundaries

```java
@Service
public class ProfileServiceImpl {
    
    @Transactional                          // DB transaction begins
    public String completeProfile(Long userId, ProfileRequest request) {
        // Multiple operations in single transaction
        User user = userRepository.findById(userId).orElseThrow(...);
        user.setDetails(...);
        userRepository.save(user);          // Auto-flushed
        
        IndividualDetails details = new IndividualDetails();
        details.setUser(user);
        detailsRepository.save(details);    // Auto-flushed
        
        // On return: transaction commits
        return "Profile completed";
    }                                       // DB transaction commits/rollsback
}
```

### Error Handling Pattern

```java
// Throw checked or runtime exceptions in service
if (!found) {
    throw new ResourceNotFoundException("Resource not found");
}

// Let exceptions bubble up (don't catch in service)
// Global handler catches all

// Global handler defines HTTP status mapping
@ExceptionHandler(ResourceNotFoundException.class)
ResponseEntity<ErrorResponse> handle(ResourceNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(404, "Not Found", ex.getMessage()));
}
```

---

## 9. API Endpoint Conventions

### RESTful Endpoints Pattern

```
Authentication (authservices)
├─ POST   /api/auth/signup        Create user account
├─ POST   /api/auth/login         Authenticate & get tokens
├─ POST   /api/auth/refresh       Refresh access token
└─ POST   /api/auth/logout        Invalidate tokens

User Management (adminservices)
├─ GET    /api/users              List users (with status filter)
├─ GET    /api/users?status=      Filter by approval status
├─ GET    /api/users/individuals  List individual users
├─ GET    /api/users/organizations List organization users
└─ PUT    /api/users/{id}/status  Update approval status

Profile Management (profileservices)
├─ POST   /api/profiles           Create profile
├─ GET    /api/profiles/{id}      Get user profile
├─ PUT    /api/profiles/{id}      Update profile
└─ GET    /api/profiles/{id}/documents List user documents

Document Management (documentservices)
├─ POST   /api/documents          Upload document
├─ GET    /api/documents/{id}     Download (presigned URL)
├─ GET    /api/documents          List user documents
└─ PUT    /api/documents/{id}     Update status

Vehicle Management (vehicleservices)
├─ POST   /api/vehicles           Add vehicle
├─ GET    /api/vehicles           List all vehicles
├─ GET    /api/vehicles/{id}      Get vehicle details
├─ PUT    /api/vehicles/{id}      Update vehicle
└─ DELETE /api/vehicles/{id}      Delete vehicle

Service History (vehicleservices)
├─ POST   /api/service-history    Record maintenance
├─ GET    /api/service-history/{vehicleId}  List by vehicle
└─ GET    /api/service-history/{id}         Get record details
```

### HTTP Status Code Usage

| Code | Status | Usage | Example |
|------|--------|-------|---------|
| 200 | OK | Successful GET, PUT, DELETE | Fetch user, update profile |
| 201 | Created | Successful POST | Create new user/vehicle |
| 400 | Bad Request | Validation error | Invalid email format |
| 401 | Unauthorized | Missing/invalid JWT | Expired token |
| 403 | Forbidden | Insufficient permissions | Non-admin accessing admin endpoint |
| 404 | Not Found | Resource doesn't exist | User ID not found |
| 409 | Conflict | Duplicate/constraint violation | Email already registered |
| 500 | Server Error | Unhandled exception | Database connection error |

### Response Format

**Success (200)**:
```json
{
  "message": "Users fetched successfully",
  "data": [
    { "id": 1, "email": "user@example.com", ... }
  ]
}
```

**Validation Error (400)**:
```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "Full name is required, Valid email required"
}
```

**Not Found (404)**:
```json
{
  "status": 404,
  "error": "User Not Found",
  "message": "User with email test@example.com not found"
}
```

**Duplicate (409)**:
```json
{
  "status": 409,
  "error": "Duplicate Entry",
  "message": "Email is already registered"
}
```

---

## 10. Database Schema Relationships

### Entity Relationship Diagram (Simplified)

```
USERS (authservices)
├─ id (PK)
├─ username, email (UNIQUE), password
├─ role, userType (ENUM), approvalStatus (ENUM)
│
├─ [1:1] ─→ INDIVIDUAL_DETAILS (profileservices)
│           ├─ id (PK), user_id (FK, UNIQUE)
│           ├─ fullName, phoneNumber, gender
│           ├─ personalEmail, countryCode
│           ├─ addressLine1/2, panNumber
│           └─ latitude, longitude
│
└─ [M:1] ─→ ORGANIZATION_DETAILS (profileservices)
            ├─ id (PK)
            ├─ companyName, phoneNumber, email
            ├─ addressLine1/2, panNumber, gstNumber, cin
            └─ approvalStatus (ENUM)

DOCUMENTS (documentservices)
├─ id (PK)
├─ user_id (FK) ─→ USERS
├─ documentType (ENUM)
├─ fileUrl (S3 path)
├─ status (ENUM), stage (ENUM)
├─ metadata (JSON), remarks
└─ uploadedAt (timestamp)

VEHICLES (vehicleservices)
├─ id (PK)
├─ registrationNumber, modelName, manufacturer
├─ purchaseDate, status (ENUM)
└─ organizationId (FK) ─→ ORGANIZATION_DETAILS

SERVICE_HISTORY (vehicleservices)
├─ id (PK)
├─ vehicle_id (FK) ─→ VEHICLES
├─ serviceType (ENUM), serviceDate
├─ cost, remarks
└─ maintenanceDetails
```

---

## Summary: Reference Architecture Checklist

### ✅ Patterns Used in useronboarding

- [x] Layered Architecture (Controller → Service → Repository → Database)
- [x] Interface-first Service Design
- [x] Constructor Dependency Injection (@RequiredArgsConstructor)
- [x] Global Exception Handling (@RestControllerAdvice)
- [x] Generic Response Wrapper (ApiResponse<T>)
- [x] Request/Response DTOs with validation
- [x] JPA Entities with proper relationships
- [x] Spring Data Repositories (JpaRepository)
- [x] Enum-based type safety (UserType, ApprovalStatus, etc.)
- [x] JWT-based authentication with access/refresh tokens
- [x] Method-level security (@PreAuthorize)
- [x] Transaction management (@Transactional)
- [x] RESTful API conventions (proper HTTP methods & status codes)
- [x] Cross-service integration (e.g., AdminService aggregates data)
- [x] Shared common module (security, exceptions, responses)

### ❌ Patterns NOT Used (Anti-patterns avoided)

- [ ] Monolithic service classes
- [ ] Shared service implementations between modules
- [ ] Direct database calls in controllers
- [ ] Catch-all exception handling (specific handlers per exception type)
- [ ] Passing entities over API (use DTOs instead)
- [ ] Field injection (constructor injection preferred)
- [ ] Lombok @Data on entities (manual getters/setters for clarity)
- [ ] Hardcoded strings (use constants/properties)
- [ ] Circular dependencies between services
- [ ] SOAP/XML responses (REST JSON only)

### 🎯 Key Metrics

- **7 Controllers** serving different domains
- **6 Service Interfaces** with clear contracts
- **13 JPA Entities** with proper relationships
- **18 DTOs** for request/response separation
- **6 Repositories** for data access
- **15 Common utilities** shared across modules
- **Zero service-to-service direct calls** (uses repositories)
- **100% method-level security** where needed

---

## Recommendations for Complaint Resolution Module

To align with this reference architecture:

1. **Structure**: Create `Complaint_Resolution/complaint/` and `Complaint_Resolution/resolution/` services
2. **Layers**: Each service gets controller, service (interface+impl), entity, repository, dto
3. **Exception**: Add complaint-specific exceptions to `common.exception`
4. **Responses**: Use `ApiResponse<T>` wrapper from common
5. **Security**: Leverage `common.security` JWT validation and `@PreAuthorize`
6. **DTOs**: Separate Request (with validation) and Response (no validation)
7. **Entities**: Use JPA with proper relationships to User and other entities
8. **Repositories**: Extend `JpaRepository` with custom query methods
9. **Validation**: Use `jakarta.validation` annotations on DTOs
10. **Transactions**: `@Transactional` on write operations in service impl

