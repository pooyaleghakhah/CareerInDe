<!-- ========================================================= -->
<!-- CareerInDe -->
<!-- API CONTEXT -->
<!-- Version 1.0 -->
<!-- ========================================================= -->

# CareerInDe

# API Context

> **REST API Architecture, Endpoint Contracts and Integration Standards**

---

# Document Metadata

| Property | Value |
|---|---|
| Project | CareerInDe |
| Document | API Context |
| Version | 1.0 |
| Status | Living Document |
| Owner | Backend Engineering Team |
| Category | Technical Documentation |
| Purpose | Define the complete API architecture, contracts and implementation standards |
| Primary Consumers | Thymeleaf Frontend, Future Angular SPA, Mobile Clients, Internal Services |
| Current API Style | Spring MVC and REST |
| Future API Style | Versioned REST API |
| Last Updated | 2026 |

---

# About this Document

This document defines the complete API strategy of CareerInDe.

It explains how clients communicate with the backend, how endpoints should be structured, how requests and responses should be validated, and how errors should be represented.

The API Context is not limited to a list of URLs.

It defines the contract between:

- Frontend and Backend
- Backend Modules
- External Integrations
- Future Mobile Applications
- Future Enterprise Clients
- Future Public API Consumers

Every API endpoint should be documented before being considered production-ready.

---

# Purpose

The purpose of this document is to ensure that CareerInDe APIs remain:

- Consistent
- Predictable
- Secure
- Versionable
- Testable
- Maintainable
- Easy to consume
- Compatible with future frontend migration

The API layer should hide internal database and implementation details from clients.

Changes to internal entities should not automatically break public API contracts.

---

# API Vision

CareerInDe APIs should provide a stable interface for all product capabilities.

The current Thymeleaf frontend and the future Angular application must both be able to interact with the same business services.

The long-term API vision includes support for:

- Web Application
- Angular SPA
- Mobile Applications
- Recruiter Portal
- University Portal
- Enterprise Integrations
- Public Developer API
- Internal Microservices

The API must therefore be designed as a product capability rather than a temporary implementation detail.

---

# Current API Status

The current backend contains a mixture of server-rendered page routes and REST-style endpoints.

## Current Web Routes

| Route | Method | Status | Purpose |
|---|---:|---|---|
| `/` | GET | Implemented or partially implemented | Landing page |
| `/login` | GET | Implemented | Display login page |
| `/login` | POST | Implemented through Spring Security | Process form login |
| `/register` | GET | Implemented | Display registration page |
| `/register` | POST | Implemented or partially implemented | Create account |
| `/dashboard` | GET | Implemented or partially implemented | Display authenticated dashboard |
| `/profile` | GET | Existing page planned for integration | Display profile page |

## Current REST Endpoints Discussed

| Endpoint | Method | Status | Purpose |
|---|---:|---|---|
| `/api/profiles` | POST | In Progress | Create a profile |
| `/api/profiles` | GET | In Progress | Retrieve all profiles |
| `/api/profiles/{id}` | GET | In Progress | Retrieve a profile by ID |
| `/api/profiles/{id}` | PUT | In Progress | Update a profile |
| `/api/profiles/{id}` | DELETE | In Progress | Delete a profile |
| `/api/auth/login` | POST | Planned / previously discussed | JWT-based login for future SPA |
| `/api/auth/**` | Various | Planned or partially implemented | Authentication API namespace |

The exact implementation must be verified against the current repository before this document is considered final.

---

# API Architecture

CareerInDe follows a layered API architecture.

```text
Client
   ↓
HTTP Request
   ↓
Spring Security Filter Chain
   ↓
Controller
   ↓
Request DTO Validation
   ↓
Application Service
   ↓
Repository / External Service
   ↓
Response DTO
   ↓
HTTP Response
```

Controllers serve as adapters between HTTP and application services.

Controllers must not contain business logic.

---

# API Design Principles

## 1. Resource-Oriented Design

Endpoints should represent business resources.

Good examples:

```text
/api/profiles
/api/resumes
/api/applications
/api/ats-analyses
/api/career-goals
```

Avoid action-based endpoint names when standard HTTP methods already express the operation.

Prefer:

```text
POST /api/resumes
```

instead of:

```text
POST /api/uploadResume
```

Prefer:

```text
DELETE /api/resumes/{id}
```

instead of:

```text
POST /api/deleteResume
```

---

## 2. Consistent Naming

API paths should:

- Use lowercase letters.
- Use plural resource names.
- Use hyphens for multi-word resources.
- Avoid verbs unless representing a true command.
- Avoid implementation-specific terminology.

Examples:

```text
/api/career-goals
/api/ats-analyses
/api/job-applications
/api/resume-versions
```

Incorrect:

```text
/api/CareerGoals
/api/getProfile
/api/job_applications
```

---

## 3. HTTP Method Semantics

| Method | Purpose | Idempotent |
|---|---|---:|
| GET | Read a resource | Yes |
| POST | Create a resource or initiate a command | No |
| PUT | Replace or fully update a resource | Yes |
| PATCH | Partially update a resource | Usually |
| DELETE | Delete a resource | Yes |

Example:

```text
GET /api/profiles/10
```

Retrieves profile `10`.

```text
POST /api/profiles
```

Creates a profile.

```text
PUT /api/profiles/10
```

Updates profile `10`.

```text
DELETE /api/profiles/10
```

Deletes profile `10`.

---

## 4. DTO-Based Contracts

Entities must not be exposed directly through production APIs.

Current early development code may still return JPA entities directly, but the target architecture requires DTOs.

Recommended DTO structure:

```text
CreateProfileRequest
UpdateProfileRequest
ProfileResponse
```

Benefits:

- Prevent accidental exposure of sensitive fields.
- Preserve API stability.
- Support versioning.
- Avoid serialization problems.
- Separate persistence from transport.
- Improve validation.

Example:

```java
public record CreateProfileRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String city,
        String country,
        String targetJob
) {
}
```

Example response:

```java
public record ProfileResponse(
        Long id,
        String firstName,
        String lastName,
        String city,
        String country,
        String targetJob,
        Integer experienceYears
) {
}
```

---

# API Base Path

The recommended base path is:

```text
/api
```

All REST endpoints should exist under this namespace.

Examples:

```text
/api/profiles
/api/resumes
/api/applications
/api/ats-analyses
/api/dashboard
```

Server-rendered Thymeleaf routes remain outside the API namespace.

Examples:

```text
/login
/register
/dashboard
/profile
```

This separation prevents confusion between HTML routes and JSON API endpoints.

---

# API Versioning Strategy

The current MVP may use unversioned endpoints:

```text
/api/profiles
```

Before opening the API to external consumers, the platform should introduce explicit versioning.

Recommended future structure:

```text
/api/v1/profiles
/api/v1/resumes
/api/v1/applications
```

Version changes should follow these rules:

- Additive changes should remain within the same version.
- Breaking request changes require a new version.
- Breaking response changes require a new version.
- Removing fields requires deprecation first.
- Existing versions should remain supported during a transition period.

---

# Authentication Modes

CareerInDe currently has two authentication strategies in its evolution.

## Current Strategy: Session-Based Authentication

Used by the Thymeleaf frontend.

Flow:

```text
Login Form
   ↓
POST /login
   ↓
Spring Security
   ↓
CustomUserDetailsService
   ↓
UserRepository.findByEmail(...)
   ↓
BCrypt Password Verification
   ↓
Authenticated Session
   ↓
Dashboard
```

The current form uses:

```html
<form action="/login" method="post">
```

Input names:

```text
username
password
```

The `username` field currently represents the user's email address.

---

## Future Strategy: JWT Authentication

Planned for the Angular SPA and mobile clients.

Expected flow:

```text
POST /api/v1/auth/login
   ↓
Credentials Validated
   ↓
Access Token Issued
   ↓
Refresh Token Issued
   ↓
Client Stores Tokens Securely
   ↓
Authenticated API Requests
```

JWT should not be mixed casually with the current session-based Thymeleaf flow.

Migration should occur through a documented security plan.

---

# Authorization

API authorization should be role-based.

Current roles:

```text
USER
ADMIN
```

Future roles:

```text
RECRUITER
UNIVERSITY
ENTERPRISE_ADMIN
SUPPORT
```

Example authorization rules:

| Resource | USER | ADMIN | RECRUITER |
|---|:---:|:---:|:---:|
| Own Profile | Read/Write | Read/Write | No |
| Own Resume | Read/Write | Read/Write | No |
| Own ATS Results | Read | Read | No |
| All Users | No | Read/Write | No |
| Candidate Search | No | Optional | Read |
| Admin Dashboard | No | Read/Write | No |

Ownership checks are required in addition to role checks.

A normal user must never access another user's profile by changing an ID in the URL.

---

# Ownership-Based Access

The API should prefer current-user endpoints when possible.

Instead of:

```text
GET /api/profiles/{id}
```

for normal users, prefer:

```text
GET /api/me/profile
```

Advantages:

- Prevents ID enumeration.
- Simplifies frontend logic.
- Reduces authorization errors.
- Makes ownership explicit.

Admin endpoints may use IDs:

```text
GET /api/admin/users/{userId}/profile
```

Recommended future endpoint structure:

```text
GET    /api/v1/me/profile
PUT    /api/v1/me/profile
DELETE /api/v1/me/profile
```

---

# Request Validation

Every API request must be validated at multiple levels.

## Structural Validation

Performed using Jakarta Bean Validation.

Examples:

```java
@NotBlank
@Email
@Size
@Min
@Max
@Pattern
@PositiveOrZero
```

## Business Validation

Performed in the service layer.

Examples:

- Email must be unique.
- Profile must belong to the authenticated user.
- Resume must be a valid PDF.
- ATS score must remain between 0 and 100.
- Subscription must permit premium analysis.

## Database Validation

Enforced by constraints.

Examples:

- Unique email.
- Non-null password.
- Unique profile per user.
- Valid foreign keys.

---

# Standard Response Strategy

Successful API responses should use predictable resource representations.

Example:

```json
{
  "id": 42,
  "firstName": "Pooya",
  "lastName": "Leghakhah",
  "country": "Germany",
  "city": "Clausthal-Zellerfeld",
  "targetCity": "Hannover",
  "targetJob": "Java Backend Developer",
  "experienceYears": 4,
  "germanLevel": "B2",
  "englishLevel": "B1"
}
```

Avoid wrapping every successful response unnecessarily unless metadata is required.

For paginated responses, a wrapper is appropriate.

Example:

```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0
}
```

---

# Standard Error Response

All API errors should follow one consistent structure.

```json
{
  "timestamp": "2026-07-16T21:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "code": "PROFILE_VALIDATION_FAILED",
  "message": "The profile request contains invalid fields.",
  "path": "/api/v1/me/profile",
  "requestId": "4d421681-1eaf-4e33-b6a1-37f21f5c0b41",
  "fieldErrors": {
    "firstName": "First name is required.",
    "experienceYears": "Experience years cannot be negative."
  }
}
```

Recommended fields:

| Field | Purpose |
|---|---|
| timestamp | Time of failure |
| status | HTTP status code |
| error | General HTTP error |
| code | Stable application error code |
| message | Human-readable explanation |
| path | Requested endpoint |
| requestId | Traceability |
| fieldErrors | Validation details |

---

# HTTP Status Code Standards

| Status | Meaning | Typical Usage |
|---:|---|---|
| 200 | OK | Successful GET or update |
| 201 | Created | Resource successfully created |
| 204 | No Content | Successful deletion |
| 400 | Bad Request | Invalid input |
| 401 | Unauthorized | Missing or invalid authentication |
| 403 | Forbidden | Authenticated but not permitted |
| 404 | Not Found | Resource does not exist |
| 409 | Conflict | Duplicate email or conflicting state |
| 413 | Payload Too Large | Resume exceeds size limit |
| 415 | Unsupported Media Type | File is not PDF |
| 422 | Unprocessable Entity | Valid syntax but invalid business request |
| 429 | Too Many Requests | Rate limit exceeded |
| 500 | Internal Server Error | Unexpected backend error |
| 502 | Bad Gateway | AI or external provider failure |
| 503 | Service Unavailable | Temporary service outage |

---

# API Documentation Standard

Every endpoint must document:

- Purpose
- Business value
- Method
- Path
- Authentication
- Required role
- Ownership rules
- Request headers
- Path parameters
- Query parameters
- Request body
- Validation
- Success response
- Error responses
- Side effects
- Idempotency
- Dependencies
- Current implementation status

---

# Endpoint Documentation Template

```markdown
## Endpoint Name

**Status:** Implemented / In Progress / Planned

**Method:** `GET`

**Path:** `/api/v1/example`

**Authentication:** Required

**Role:** `USER`

### Purpose

Explain what the endpoint does.

### Request

Describe headers, parameters and body.

### Response

Provide a JSON example.

### Validation

List structural and business rules.

### Error Codes

List all expected failures.

### Side Effects

Describe database or external changes.

### Notes

Document limitations and future improvements.
```

---

# Current API Technical Debt

The API currently has several known technical gaps.

- Some controllers may return JPA entities directly.
- DTOs are not yet consistently implemented.
- API versioning is not yet active.
- Profile endpoints currently use direct IDs.
- Current profile CRUD endpoints may be temporarily public for testing.
- Session authentication and planned JWT architecture are not yet fully separated.
- Swagger/OpenAPI documentation is not yet complete.
- Global exception handling is not yet fully implemented.
- Ownership validation must be added.
- Validation annotations must be added to request DTOs.
- API integration tests are incomplete.

These issues should be resolved before public production launch.

---

# Known Authentication Incident

A login failure occurred because multiple database rows shared the same email address.

The repository method:

```java
Optional<User> findByEmail(String email);
```

expected one result, but the database returned fourteen records.

Observed error:

```text
Query did not return a unique result: 14 results were returned
```

Root cause:

- The `users.email` column did not have a unique constraint.
- Repeated test registrations created duplicate accounts.

Required remediation:

```java
@Column(nullable = false, unique = true)
private String email;
```

Database cleanup is also required before adding the constraint.

This incident demonstrates why API validation and database constraints must both be implemented.

---

# API Principles Summary

Every CareerInDe endpoint should satisfy these questions:

- Does it represent a real business resource?
- Is the endpoint naming consistent?
- Is authentication required where appropriate?
- Is resource ownership checked?
- Are request DTOs validated?
- Are entities hidden from external clients?
- Are errors predictable?
- Is the endpoint documented?
- Is it tested?
- Can it evolve without breaking clients?

An endpoint should not be considered production-ready until every answer is **Yes**.

# Authentication API

CareerInDe currently supports server-rendered authentication through Spring Security form login.

A JWT-based REST authentication API is planned for the future Angular SPA and mobile clients.

The two authentication approaches must remain clearly separated.

---

## Current Authentication Model

Current frontend:

```text
Thymeleaf

Current authentication:

Session-Based Form Login

Current login processing endpoint:

POST /login

Spring Security processes the request directly.

The application does not currently require a custom REST controller for form login.

Login Page

Status: Implemented

Method: GET

Path:

/login

Authentication: Public

Purpose

Displays the CareerInDe login form.

Current Form Structure
<form th:action="@{/login}" method="post">

    <input
        type="email"
        name="username"
        required>

    <input
        type="password"
        name="password"
        required>

    <button type="submit">
        Login
    </button>

</form>
Important Detail

The form field is named:

username

but its value represents the user's email address.

Spring Security passes this value to:

CustomUserDetailsService.loadUserByUsername(String email)
Process Login

Status: Implemented through Spring Security

Method: POST

Path:

/login

Authentication: Public

Content Type:

application/x-www-form-urlencoded
Request Parameters
Parameter	Required	Description
username	Yes	User email
password	Yes	Raw password entered by user
Request Example
username=pooya@example.com
password=ExamplePassword123
Successful Behavior

Spring Security should:

Load the user by email.
Compare the raw password with the BCrypt hash.
Create an authenticated session.
Redirect the user to the dashboard.

Expected success redirect:

/dashboard
Failed Behavior

The user should be redirected to:

/login?error=true

Possible causes:

Email does not exist.
Password is incorrect.
Duplicate users exist with the same email.
Account is locked.
User is disabled.
An internal authentication service error occurs.
Logout

Status: Implemented or configured

Method: POST recommended

Path:

/logout

Authentication: Required

Purpose

Terminates the authenticated user session.

Successful Behavior
Invalidates the HTTP session.
Clears the Spring Security context.
Removes the session cookie.
Redirects to the login page.

Recommended redirect:

/login?logout=true
Security Note

Logout should preferably use POST when CSRF protection is enabled.

Registration API and Web Flow

CareerInDe currently uses a server-rendered registration flow.

The exact controller implementation must be verified against the current repository.

Registration Page

Status: Implemented

Method: GET

Path:

/register

Authentication: Public

Purpose

Displays the registration form.

Expected Fields
Email
Password
Confirm Password

Future fields may include:

First Name
Last Name
Terms Acceptance
Privacy Policy Acceptance
Preferred Language
Create User Account

Status: Implemented or partially implemented

Method: POST

Path:

/register

Authentication: Public

Expected Request
application/x-www-form-urlencoded

or future JSON request:

{
  "email": "pooya@example.com",
  "password": "ExamplePassword123",
  "confirmPassword": "ExamplePassword123"
}
Business Rules
Email is required.
Email must have a valid format.
Email must be normalized before comparison.
Email must be unique.
Password is required.
Password confirmation must match.
Password must be encoded before storage.
Default role should be USER.
Plain-text passwords must never be stored.
Registration should fail with a conflict when the email already exists.
Recommended Email Normalization

Before storage:

email.trim().toLowerCase()

This prevents duplicates such as:

Pooya@Example.com
pooya@example.com
 pooya@example.com
Successful Response

Current server-rendered behavior may redirect to:

/login?registered=true

Future REST behavior:

HTTP/1.1 201 Created
Location: /api/v1/users/{id}

Example body:

{
  "id": 15,
  "email": "pooya@example.com",
  "role": "USER",
  "status": "ACTIVE"
}
Error Responses
Status	Code	Meaning
400	REGISTRATION_VALIDATION_FAILED	Invalid fields
409	EMAIL_ALREADY_EXISTS	Duplicate email
500	REGISTRATION_FAILED	Unexpected failure
Future JWT Authentication API

JWT authentication is planned for the Angular SPA.

It should be implemented only after the SPA migration has a clear security design.

JWT Login

Status: Planned

Method: POST

Path:

/api/v1/auth/login

Authentication: Public

Request
{
  "email": "pooya@example.com",
  "password": "ExamplePassword123"
}
Validation
Email is required.
Email format must be valid.
Password is required.
Credentials must match an enabled account.
Success Response
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "b8bd4f36-...",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "user": {
    "id": 15,
    "email": "pooya@example.com",
    "role": "USER"
  }
}
Error Responses
{
  "timestamp": "2026-07-16T21:30:00Z",
  "status": 401,
  "error": "Unauthorized",
  "code": "INVALID_CREDENTIALS",
  "message": "The email or password is incorrect.",
  "path": "/api/v1/auth/login"
}
Security Rules
Do not reveal whether the email exists.
Apply rate limiting.
Record failed login attempts.
Lock accounts after repeated failures when required.
Rotate refresh tokens.
Use short-lived access tokens.
Store refresh tokens securely.
Do not store access tokens in insecure browser storage without a documented risk analysis.
Refresh Access Token

Status: Planned

Method: POST

Path:

/api/v1/auth/refresh
Request
{
  "refreshToken": "b8bd4f36-..."
}
Success Response
{
  "accessToken": "new-access-token",
  "refreshToken": "rotated-refresh-token",
  "tokenType": "Bearer",
  "expiresIn": 900
}
Business Rules
Refresh token must exist.
Token must not be expired.
Token must not be revoked.
Token must belong to an active account.
Refresh token rotation should be enabled.
JWT Logout

Status: Planned

Method: POST

Path:

/api/v1/auth/logout
Purpose

Revokes the refresh token or token family.

Success Response
HTTP/1.1 204 No Content
Current User

Status: Planned

Method: GET

Path:

/api/v1/auth/me

Authentication: Required

Response
{
  "id": 15,
  "email": "pooya@example.com",
  "role": "USER",
  "emailVerified": true
}
Profile API

The current Profile module contains:

Profile
ProfileRepository
ProfileService
ProfileController

The current early controller uses:

/api/profiles

The target production API should prefer current-user ownership endpoints.

Current Create Profile Endpoint

Status: In Progress

Method: POST

Path:

/api/profiles

Authentication: Temporarily public during testing or intended to be protected

Current Request Model

The early implementation accepts the Profile entity directly.

This is temporary and should be replaced by a DTO.

Target Request
{
  "firstName": "Pooya",
  "lastName": "Leghakhah",
  "country": "Germany",
  "city": "Clausthal-Zellerfeld",
  "targetCity": "Hannover",
  "targetJob": "Java Backend Developer",
  "experienceYears": 4,
  "salaryExpectation": 45000,
  "linkedinUrl": "https://www.linkedin.com/in/example",
  "githubUrl": "https://github.com/example",
  "portfolioUrl": "https://example.com",
  "phone": "+49...",
  "nationality": "Iranian",
  "aboutMe": "Backend developer focused on Java and Spring Boot.",
  "germanLevel": "B2",
  "englishLevel": "B1"
}
Validation Rules
Field	Rule
firstName	Required, length 1–100
lastName	Required, length 1–100
country	Optional or required according to onboarding
city	Optional
targetCity	Optional
targetJob	Recommended
experienceYears	Zero or greater
salaryExpectation	Positive or zero
linkedinUrl	Valid URL
githubUrl	Valid URL
portfolioUrl	Valid URL
phone	Valid normalized phone format
aboutMe	Maximum length
germanLevel	Approved language-level value
englishLevel	Approved language-level value
Success Response
HTTP/1.1 201 Created
{
  "id": 20,
  "firstName": "Pooya",
  "lastName": "Leghakhah",
  "country": "Germany",
  "city": "Clausthal-Zellerfeld",
  "targetCity": "Hannover",
  "targetJob": "Java Backend Developer",
  "experienceYears": 4,
  "salaryExpectation": 45000,
  "linkedinUrl": "https://www.linkedin.com/in/example",
  "githubUrl": "https://github.com/example",
  "portfolioUrl": "https://example.com",
  "phone": "+49...",
  "nationality": "Iranian",
  "aboutMe": "Backend developer focused on Java and Spring Boot.",
  "germanLevel": "B2",
  "englishLevel": "B1",
  "createdAt": "2026-07-16T20:10:00",
  "updatedAt": "2026-07-16T20:10:00"
}
Expected Errors
Status	Code	Meaning
400	PROFILE_VALIDATION_FAILED	Invalid profile data
401	AUTHENTICATION_REQUIRED	User not logged in
409	PROFILE_ALREADY_EXISTS	User already owns a profile
500	PROFILE_CREATION_FAILED	Unexpected failure
Current List Profiles Endpoint

Status: In Progress and intended primarily for admin use

Method: GET

Path:

/api/profiles
Current Behavior

Returns every profile.

Production Concern

Normal users must not retrieve all user profiles.

This endpoint should be restricted to:

ADMIN

or removed from the public API.

Recommended Admin Path
GET /api/v1/admin/profiles
Pagination
GET /api/v1/admin/profiles?page=0&size=20&sort=createdAt,desc
Current Get Profile by ID Endpoint

Status: In Progress

Method: GET

Path:

/api/profiles/{id}
Security Concern

A user may attempt to access another user's profile by changing the path ID.

Required Protection
Verify authenticated user ownership.
Permit admin access separately.
Return 403 when the user does not own the profile.
Prefer /api/v1/me/profile for normal users.
Recommended Get Current Profile Endpoint

Status: Planned

Method: GET

Path:

/api/v1/me/profile

Authentication: Required

Success Response
{
  "id": 20,
  "firstName": "Pooya",
  "lastName": "Leghakhah",
  "country": "Germany",
  "city": "Clausthal-Zellerfeld",
  "targetCity": "Hannover",
  "targetJob": "Java Backend Developer",
  "experienceYears": 4,
  "germanLevel": "B2",
  "englishLevel": "B1",
  "profileCompletion": 78
}
Profile Not Created

Possible response:

HTTP/1.1 404 Not Found
{
  "status": 404,
  "code": "PROFILE_NOT_FOUND",
  "message": "No profile exists for the authenticated user."
}

An alternative product decision is to automatically create an empty profile during registration. This must be decided and documented consistently.

Update Current Profile

Status: Planned target; current ID-based endpoint exists in progress

Method: PUT

Path:

/api/v1/me/profile
Request
{
  "firstName": "Pooya",
  "lastName": "Leghakhah",
  "country": "Germany",
  "city": "Clausthal-Zellerfeld",
  "targetCity": "Braunschweig",
  "targetJob": "Java Backend Developer",
  "experienceYears": 5,
  "salaryExpectation": 48000,
  "germanLevel": "B2",
  "englishLevel": "B1"
}
Success Response
HTTP/1.1 200 OK

The response contains the updated profile.

Side Effects
updatedAt changes.
Profile completion is recalculated.
Future AI recommendations may be marked stale.
Dashboard values may be refreshed.
Delete Current Profile

Status: Planned

Method: DELETE

Path:

/api/v1/me/profile
Product Concern

A profile is foundational to the account.

Deleting it may affect:

AI recommendations
Resume analysis
Career GPS
Dashboard
Application matching

The product may prefer:

clearing profile fields,
soft deletion,
or full account deletion.

This behavior requires explicit product approval.

Resume API

The Resume API manages uploaded CV documents and their metadata.

Resume files must be associated with the authenticated user.

Upload Resume

Status: Implemented or partially implemented

Method: POST

Path:

/api/v1/resumes

Authentication: Required

Content Type:

multipart/form-data
Request
file: resume.pdf

Optional metadata:

language: en
label: Backend Developer CV
Validation
File is required.
File extension must be .pdf.
MIME type must be validated.
Maximum size is currently expected to be 10 MB.
Empty files are rejected.
Malicious filenames are sanitized.
User storage quota is checked.
File content should be inspected, not only the extension.
Success Response
HTTP/1.1 201 Created
{
  "id": 55,
  "originalFilename": "pooya_backend_cv.pdf",
  "contentType": "application/pdf",
  "sizeBytes": 482100,
  "status": "UPLOADED",
  "uploadedAt": "2026-07-16T21:45:00Z"
}
Error Responses
Status	Code	Meaning
400	RESUME_FILE_REQUIRED	Missing file
413	RESUME_TOO_LARGE	File exceeds limit
415	UNSUPPORTED_RESUME_TYPE	File is not valid PDF
422	RESUME_CONTENT_INVALID	PDF cannot be processed
507	STORAGE_CAPACITY_EXCEEDED	Storage unavailable
List Current User Resumes

Status: Planned or partially implemented

Method: GET

Path:

/api/v1/resumes

Authentication: Required

Query Parameters
Parameter	Default	Purpose
page	0	Page index
size	20	Page size
sort	uploadedAt,desc	Sorting
status	all	Filter by parsing status
Response
{
  "content": [
    {
      "id": 55,
      "originalFilename": "pooya_backend_cv.pdf",
      "status": "ANALYZED",
      "uploadedAt": "2026-07-16T21:45:00Z",
      "latestAtsScore": 82
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
Get Resume Metadata

Status: Planned

Method: GET

Path:

/api/v1/resumes/{resumeId}
Ownership Rule

Only the owner or an authorized administrator may access the metadata.

Response
{
  "id": 55,
  "originalFilename": "pooya_backend_cv.pdf",
  "contentType": "application/pdf",
  "sizeBytes": 482100,
  "status": "ANALYZED",
  "uploadedAt": "2026-07-16T21:45:00Z",
  "parsedAt": "2026-07-16T21:45:04Z",
  "latestAtsAnalysisId": 81
}
Download Resume

Status: Planned or partially implemented

Method: GET

Path:

/api/v1/resumes/{resumeId}/file
Response Headers
Content-Type: application/pdf
Content-Disposition: attachment; filename="pooya_backend_cv.pdf"
Security Requirements
Ownership validation.
Safe filename handling.
No direct filesystem path exposure.
Download audit logging.
Authorization before opening the file stream.
Delete Resume

Status: Planned or partially implemented

Method: DELETE

Path:

/api/v1/resumes/{resumeId}
Success Response
HTTP/1.1 204 No Content
Side Effects
File is removed or archived.
Metadata is soft-deleted or deleted.
Associated temporary analysis may be removed.
Historical ATS analysis retention depends on product policy.
Dashboard is recalculated.
Start Resume Parsing

Status: Planned

Method: POST

Path:

/api/v1/resumes/{resumeId}/parse
Purpose

Starts or retries text extraction and structured parsing.

Success Response

Synchronous option:

{
  "resumeId": 55,
  "status": "PARSED",
  "detectedSections": [
    "experience",
    "education",
    "skills"
  ]
}

Asynchronous preferred option:

HTTP/1.1 202 Accepted
{
  "jobId": "parse-55-174",
  "resumeId": 55,
  "status": "QUEUED"
}
ATS Analysis API

The ATS API calculates and returns resume-quality analysis.

Create ATS Analysis

Status: Implemented at a basic level or in progress

Method: POST

Path:

/api/v1/resumes/{resumeId}/ats-analyses

Authentication: Required

Optional Request

Without job description:

{
  "mode": "GENERAL"
}

With target job description:

{
  "mode": "JOB_MATCH",
  "jobTitle": "Java Backend Developer",
  "jobDescription": "We are looking for a Java developer..."
}
Business Rules
Resume must belong to the current user.
Resume must be successfully parsed.
Subscription or usage limits must be checked.
An analysis should create a new immutable record.
The previous analysis should not be overwritten.
Algorithm and prompt versions should be stored.
Success Response
HTTP/1.1 201 Created
{
  "id": 81,
  "resumeId": 55,
  "overallScore": 82,
  "keywordScore": 85,
  "formatScore": 80,
  "skillsScore": 84,
  "experienceScore": 78,
  "detectedSkills": [
    "Java",
    "Spring Boot",
    "PostgreSQL",
    "Docker"
  ],
  "missingSkills": [
    "Kubernetes",
    "Kafka"
  ],
  "recommendations": [
    {
      "priority": "HIGH",
      "category": "EXPERIENCE",
      "message": "Add measurable outcomes to your recent Java project."
    }
  ],
  "analysisVersion": "ats-0.1",
  "analyzedAt": "2026-07-16T21:50:00Z"
}
Errors
Status	Code	Meaning
404	RESUME_NOT_FOUND	Resume does not exist
403	RESUME_ACCESS_DENIED	Resume belongs to another user
409	RESUME_NOT_PARSED	Parsing not complete
422	ATS_ANALYSIS_FAILED	Content cannot be evaluated
429	ATS_LIMIT_EXCEEDED	Plan limit reached
502	AI_PROVIDER_ERROR	External model unavailable
Get ATS Analysis

Status: Planned or partially implemented

Method: GET

Path:

/api/v1/ats-analyses/{analysisId}
Authorization

The analysis must belong to a resume owned by the authenticated user.

List ATS Analysis History

Status: Planned

Method: GET

Path:

/api/v1/resumes/{resumeId}/ats-analyses
Purpose

Displays the resume's score history.

Response
{
  "content": [
    {
      "id": 81,
      "overallScore": 82,
      "analysisVersion": "ats-0.1",
      "analyzedAt": "2026-07-16T21:50:00Z"
    },
    {
      "id": 70,
      "overallScore": 74,
      "analysisVersion": "ats-0.1",
      "analyzedAt": "2026-06-30T12:10:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 2,
  "totalPages": 1
}
Dashboard API

The Dashboard API aggregates information from multiple modules.

The dashboard should not require the frontend to call many unrelated endpoints for its initial view.

Get Dashboard Summary

Status: Planned or partially implemented

Method: GET

Path:

/api/v1/dashboard

Authentication: Required

Response
{
  "profileCompletion": 78,
  "resume": {
    "exists": true,
    "latestResumeId": 55,
    "status": "ANALYZED"
  },
  "ats": {
    "latestScore": 82,
    "previousScore": 74,
    "change": 8
  },
  "applications": {
    "total": 20,
    "applied": 7,
    "interviews": 4,
    "offers": 1,
    "rejected": 8
  },
  "recommendations": [
    {
      "id": 91,
      "priority": "HIGH",
      "title": "Strengthen Spring Security experience"
    }
  ]
}
Performance Requirement

The dashboard should generally respond within:

700 ms

excluding live AI generation.

AI recommendations should be precomputed or loaded asynchronously.

API Status Summary
API Area	Current State
Form Login	Implemented
Registration	Implemented or partial
JWT Authentication	Planned
Profile CRUD	In Progress
Resume Upload	Implemented or partial
Resume Parsing	Implemented at basic level or planned refinement
ATS Analysis	Basic implementation or in progress
Dashboard API	Partial / Planned
Application Tracking	Planned
Notifications	Planned
Billing	Planned
Admin APIs	Partial / Planned

این بخش را مستقیماً بعد از بخش قبلی `06_API_Context.md` قرار بده. بخش بعدی باید **Application Tracking API، AI Recommendation API، Career GPS API، Admin API، filtering/pagination، idempotency، rate limiting، Swagger/OpenAPI و API testing strategy** را پوشش دهد.
