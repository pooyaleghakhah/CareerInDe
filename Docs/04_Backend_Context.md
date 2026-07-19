<!-- ========================================================= -->
<!-- CareerInDe -->
<!-- BACKEND CONTEXT -->
<!-- Version 1.0 -->
<!-- ========================================================= -->

# CareerInDe

# Backend Context

> **Backend Architecture, Engineering Standards and Implementation Guide**

---

# Document Metadata

| Property | Value |
|----------|-------|
| Project | CareerInDe |
| Product | CareerInDe |
| Document | Backend Context |
| Version | 1.0 |
| Status | Living Document |
| Owner | Backend Engineering Team |
| Category | Technical Documentation |
| Purpose | Define the architecture, standards and implementation rules for the backend |

---

# About this Document

The Backend Context document defines the complete backend architecture of the CareerInDe platform.

Unlike implementation code, this document explains **why the backend is designed the way it is**, **how each module should interact**, and **which engineering principles must be followed**.

This document serves as the primary technical reference for every backend developer joining the project.

Whenever backend implementation decisions are required, this document should be consulted before writing code.

---

# Purpose

The objective of this document is to guarantee that the backend evolves consistently over time.

The document defines:

- Backend Architecture
- Package Organization
- Module Responsibilities
- Coding Standards
- Naming Conventions
- Layer Responsibilities
- Dependency Rules
- Validation Rules
- Security Integration
- Database Interaction
- Exception Handling
- Logging
- Testing Strategy
- Future Scalability

---

# Reading Guide

Recommended reading order:

1. Backend Vision
2. Architecture Principles
3. Package Structure
4. Layer Responsibilities
5. Module Standards
6. Coding Standards
7. Security
8. Database
9. Testing
10. Future Architecture

---

# Backend Vision

The CareerInDe backend is designed to become a robust, scalable and maintainable platform capable of supporting thousands of concurrent users while remaining simple enough for rapid feature development.

The backend should prioritize:

- Maintainability
- Readability
- Scalability
- Security
- Performance
- Testability

The system should evolve gradually without requiring frequent architectural rewrites.

---

# Engineering Philosophy

Backend development follows several core engineering principles.

---

## Simplicity First

The simplest correct solution is preferred over a complex solution.

Avoid unnecessary abstractions.

Avoid premature optimization.

Avoid introducing frameworks without clear business value.

---

## Readability First

Code is read significantly more often than it is written.

Every class should be understandable within a few minutes.

Developers should immediately understand:

- What the class does.
- Why it exists.
- Which module owns it.
- Which dependencies it has.

Readable code always has higher priority than clever code.

---

## Maintainability

Every implementation should minimize future maintenance costs.

This means:

- Small classes
- Small methods
- Clear naming
- Limited dependencies
- Predictable behavior

Future developers should be able to extend existing functionality without rewriting major parts of the system.

---

## Modularity

Every business capability should exist inside its own module.

Example

Authentication

↓

User

↓

Profile

↓

Resume

↓

ATS

↓

Dashboard

↓

Career GPS

Each module owns its own business logic.

Modules communicate through well-defined interfaces.

---

## Business Driven Design

Business requirements always drive technical implementation.

Technology should never dictate product behavior.

Every backend component should exist because it solves a business problem.

---

# Backend Goals

The backend should provide:

- Stable APIs
- Secure authentication
- Reliable persistence
- AI integration
- Fast response times
- Easy deployment
- Low operational cost
- High developer productivity

---

# Current Technology Stack

Programming Language

Java 21

Framework

Spring Boot 3

Security

Spring Security

Persistence

Spring Data JPA

Hibernate

Database

PostgreSQL

Dependency Management

Maven

Build Tool

Maven Wrapper

Template Engine

Thymeleaf

Future Frontend

Angular

Containerization

Docker

Reverse Proxy

Nginx

Cloud

Hetzner VPS

Version Control

Git

GitHub

---

# High-Level Backend Architecture

```
Browser

↓

Spring Security

↓

Controller

↓

Service

↓

Repository

↓

PostgreSQL
```

Every request follows this sequence.

Controllers never access repositories directly.

Business logic never exists inside controllers.

Repositories never contain business rules.

---

# Backend Layers

The backend follows a layered architecture.

```
Presentation Layer

↓

Application Layer

↓

Domain Layer

↓

Persistence Layer

↓

Infrastructure Layer
```

Each layer has a clearly defined responsibility.

---

# Presentation Layer

Responsible for:

- HTTP Requests
- HTTP Responses
- Validation
- Status Codes
- DTO Mapping

Contains:

- Controllers
- Request DTOs
- Response DTOs

Controllers should remain thin.

Controllers coordinate requests but never implement business rules.

---

# Application Layer

Responsible for:

- Business Logic
- Use Cases
- Transactions
- Validation
- Integration

Contains:

- Services

Every business process belongs inside a service.

Services coordinate repositories and other services.

---

# Domain Layer

Responsible for:

- Business Entities
- Domain Rules
- Business Models

Contains:

- Entities
- Enums
- Value Objects (future)

The domain layer represents the core of the application.

Business rules should remain independent from infrastructure whenever possible.

---

# Persistence Layer

Responsible for:

- Database Communication
- CRUD Operations
- Queries

Contains:

- Repositories

Repositories should never implement business logic.

---

# Infrastructure Layer

Responsible for:

- Security
- Configuration
- File Storage
- AI Providers
- External APIs
- Logging
- Email
- Future Queue Processing

Infrastructure supports the application but should not contain business rules.

---

# Package Organization

CareerInDe follows a **Feature-Oriented Package Structure** rather than a purely technical package structure.

This decision was made to improve maintainability, scalability and developer productivity.

Instead of separating controllers, services and repositories into large global packages, every business capability owns its complete implementation.

Example

```
profile/

    controller/

    service/

    repository/

    dto/

    mapper/

    entity/
```

rather than

```
controllers/

services/

repositories/

entities/
```

The feature-oriented approach keeps related code together and minimizes cross-module dependencies.

---

# Package Hierarchy

The backend is organized into business modules.

```
com.careerinde.careerinde_app

│

├── authentication

├── user

├── profile

├── resume

├── ats

├── dashboard

├── career

├── application

├── notification

├── ai

├── admin

│

├── common

├── config

├── security

├── exception

├── util

└── shared
```

Each package represents a business capability.

No package should become a "miscellaneous" collection of unrelated classes.

---

# Module Independence

Every module should satisfy the following principles.

Each module owns:

- Controllers
- Services
- DTOs
- Repositories
- Business Logic
- Validation
- Mappers

A module should expose only what other modules require.

Internal implementation details should remain private.

---

# Dependency Rules

Dependencies always point downward.

```
Controller

↓

Service

↓

Repository

↓

Database
```

The reverse direction is forbidden.

Repositories never call services.

Controllers never call repositories.

Entities never depend on controllers.

Utilities never depend on business modules.

---

# Module Communication

Modules communicate through services.

Example

```
ResumeService

↓

ProfileService

↓

UserService
```

Direct repository access across modules is discouraged.

Incorrect

```
ResumeController

↓

ProfileRepository
```

Correct

```
ResumeService

↓

ProfileService
```

---

# Controller Standards

Controllers represent the public API of each module.

Responsibilities

- Receive HTTP requests
- Validate input
- Delegate work
- Return HTTP responses
- Convert DTOs

Controllers should **never**

- contain business logic
- execute database queries
- implement calculations
- call EntityManager directly

Controllers should remain lightweight.

Target size

Less than 200 lines.

---

# Service Standards

Services contain the business logic of the application.

Responsibilities

- Business rules
- Transactions
- AI orchestration
- Validation
- Repository coordination

Services may call:

- Repositories
- Other Services
- External APIs
- AI Providers

Services should avoid circular dependencies.

Target size

Less than 500 lines.

Large services should be divided into smaller business services.

---

# Repository Standards

Repositories are responsible only for persistence.

Responsibilities

- CRUD
- Queries
- Pagination
- Specifications

Repositories should not contain:

Business calculations

AI logic

Validation

Business workflows

Repositories should remain declarative whenever possible.

Example

```
findByEmail()

findByUser()

existsByEmail()
```

---

# Entity Standards

Entities represent persistent business objects.

Entities should contain:

- Persistent fields
- Relationships
- Basic domain invariants

Entities should avoid:

- Business workflows
- Service calls
- HTTP knowledge
- External API calls

Entities must remain persistence-focused.

---

# DTO Standards

DTOs isolate the API from the persistence model.

Request DTOs

Represent client input.

Response DTOs

Represent API output.

DTOs should never expose internal entity implementation.

Benefits

- API stability
- Security
- Versioning
- Separation of concerns

---

# Mapper Standards

Mappers convert between Entities and DTOs.

Responsibilities

Entity

↓

DTO

DTO

↓

Entity

Mapping logic should never be duplicated.

Future versions may introduce MapStruct.

Current implementation may use manual mapping.

---

# Configuration Standards

Configuration classes belong only inside the config package.

Examples

```
SecurityConfig

OpenApiConfig

CorsConfig

JacksonConfig

StorageConfig
```

Configuration should never contain business logic.

---

# Common Package

The common package contains reusable components shared across multiple modules.

Examples

- Constants
- Generic Responses
- Shared DTOs
- Base Exceptions
- Utility Interfaces

Business-specific classes must not be placed here.

---

# Utility Package

Utility classes should remain stateless.

Examples

```
DateUtils

PdfUtils

ValidationUtils

StringUtils
```

Utilities should never depend on business modules.

Utilities should contain only generic helper functionality.

---

# Exception Package

Global exception handling should be centralized.

Contains

- Custom Exceptions
- GlobalExceptionHandler
- Error Response Models

Every API should return a consistent error structure.

Future standard response:

```
{
    "timestamp": "...",
    "status":404,
    "error":"Not Found",
    "message":"Resume not found.",
    "path":"/api/resumes/12"
}
```

---

# Shared Package

The shared package contains reusable infrastructure components.

Examples

- Base Classes
- Generic Interfaces
- Shared Services
- Common Enums

Business logic should not accumulate inside shared.

Keep the package small and reusable.

---

# Backend Design Principles

Every backend component should satisfy the following principles.

✓ Single Responsibility

✓ Low Coupling

✓ High Cohesion

✓ Explicit Dependencies

✓ Testability

✓ Readability

✓ Reusability

✓ Production Readiness

These principles apply to every new module introduced into CareerInDe.

---

# Database Access Strategy

CareerInDe uses Spring Data JPA as the primary persistence abstraction.

The objective is to keep database interaction simple, maintainable and consistent.

Repositories should expose only business-relevant persistence operations.

Complex database logic should be avoided whenever possible.

---

## General Principles

Repositories should:

- Extend JpaRepository whenever possible.
- Use derived query methods for simple queries.
- Use JPQL only when necessary.
- Use native SQL only as a last resort.
- Return Optional for nullable results.
- Support pagination for large datasets.

Example

```
Optional<User> findByEmail(String email);

boolean existsByEmail(String email);

List<Resume> findByUser(User user);

Page<Application> findAll(Pageable pageable);
```

---

# Transaction Management

Transactions belong inside the Service layer.

Controllers should never manage transactions.

Repositories should never manage transactions.

Example

```
Controller

↓

Service (@Transactional)

↓

Repository
```

---

## Transaction Rules

Read operations

```
@Transactional(readOnly = true)
```

Write operations

```
@Transactional
```

Large business workflows should execute inside one transaction whenever consistency is required.

Nested transactions should be avoided unless absolutely necessary.

---

# Validation Strategy

Validation occurs at multiple levels.

---

## Client Validation

Purpose

Improve user experience.

Examples

- Required fields
- Email format
- Password confirmation
- File type

Client validation never replaces server validation.

---

## API Validation

DTOs should use Jakarta Bean Validation.

Examples

```
@NotBlank

@NotNull

@Email

@Size

@Pattern

@Min

@Max
```

Example

```java
public class RegisterRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;

}
```

---

## Business Validation

Business validation belongs inside Services.

Examples

- Email already exists
- Duplicate resume
- Invalid career goal
- Unsupported subscription

Business validation should never be implemented inside controllers.

---

# Exception Handling Strategy

CareerInDe follows centralized exception handling.

Every exception should produce a predictable API response.

Controllers should never use try/catch blocks for normal business exceptions.

---

## Exception Categories

Validation Exceptions

Authentication Exceptions

Authorization Exceptions

Resource Not Found

Business Exceptions

AI Exceptions

Infrastructure Exceptions

External API Exceptions

---

## Global Exception Handler

A single GlobalExceptionHandler should translate exceptions into HTTP responses.

Benefits

- Consistency
- Reusability
- Cleaner controllers
- Easier debugging

---

## Standard Error Response

```json
{
  "timestamp": "2026-07-16T15:40:22Z",
  "status": 404,
  "error": "Not Found",
  "message": "Resume not found.",
  "path": "/api/resumes/25",
  "requestId": "8e6bfa32"
}
```

Every error response should follow this format.

---

# Logging Strategy

Logging is essential for production systems.

CareerInDe should use structured logging.

The objective is to support monitoring, debugging and auditing.

---

## Log Levels

ERROR

Unexpected failures requiring immediate attention.

WARN

Unexpected but recoverable situations.

INFO

Business events.

DEBUG

Development diagnostics.

TRACE

Detailed execution information.

TRACE should never be enabled in production.

---

## Events That Must Be Logged

Authentication

User Registration

Resume Upload

Resume Analysis

ATS Generation

Subscription Changes

Critical Errors

External API Failures

Application Startup

Application Shutdown

---

## Events That Must Never Be Logged

Passwords

JWT Tokens

Session IDs

Personal Resume Content

Sensitive AI Prompts

Payment Information

PII should always be protected.

---

# Security Integration

Spring Security protects every incoming request.

Authentication occurs before controller execution.

Architecture

```
HTTP Request

↓

Security Filter Chain

↓

Authentication

↓

Authorization

↓

Controller

↓

Service

↓

Repository
```

---

## Authorization Strategy

Current Roles

USER

ADMIN

Future Roles

RECRUITER

UNIVERSITY

ENTERPRISE_ADMIN

Permissions should be role-based.

Business logic should never depend directly on HTTP roles.

---

# Password Strategy

Passwords should always be encrypted using BCrypt.

Plain-text passwords are never stored.

Minimum Password Policy

- Minimum 8 characters
- Mixed character types (future)
- Password strength indicator (future)
- Password history (enterprise)

---

# File Upload Strategy

Resume files are uploaded through a dedicated service.

Workflow

```
User

↓

Upload Controller

↓

Resume Service

↓

File Storage

↓

Database Metadata

↓

Resume Parser

↓

ATS Engine

↓

Dashboard
```

Uploaded files should be validated before storage.

---

## File Validation Rules

Allowed format

PDF

Maximum size

Configurable

Virus scanning

Future

Duplicate detection

Future

OCR support

Future

---

# AI Integration Strategy

AI should remain isolated from business modules.

Recommended architecture

```
ResumeService

↓

AIService

↓

PromptBuilder

↓

LLM Provider

↓

Response Parser

↓

Business Result
```

Business modules should never call LLM providers directly.

All AI communication should pass through AIService.

This simplifies provider replacement and testing.

---

# External Service Strategy

External systems should be wrapped behind service interfaces.

Examples

Groq

OpenAI

Email Provider

Storage Provider

Future Queue

This prevents vendor lock-in.

---

# Performance Guidelines

Backend performance goals

Average API Response

< 500 ms

AI Response

< 10 seconds

Authentication

< 300 ms

Dashboard

< 700 ms

Profile Update

< 500 ms

These targets should be monitored continuously.

---

# Caching Strategy

Current MVP

No dedicated cache.

Future

Redis

Candidate cache targets

Profile

Resume Metadata

Reference Data

Static Configuration

Frequently accessed AI results

Caching should improve performance without compromising consistency.

---

# Testing Strategy

CareerInDe follows a testing strategy that prioritizes confidence, maintainability and regression prevention.

Testing is considered an integral part of software development rather than an optional activity.

Every business-critical feature should be validated through automated tests before deployment.

---

## Testing Pyramid

CareerInDe follows the classical Testing Pyramid.

```
                E2E Tests
                    ▲
            Integration Tests
                    ▲
              Unit Tests
```

The majority of tests should be unit tests.

Integration tests verify module interaction.

End-to-End tests validate complete business workflows.

---

## Unit Testing

Purpose

Verify business logic in isolation.

Target

- Services
- Utilities
- Validators
- Mappers

Frameworks

- JUnit 5
- Mockito
- Spring Boot Test

Coverage Goal

Minimum 80% for business services.

Critical modules should target 90%+ coverage.

---

## Integration Testing

Purpose

Validate communication between multiple layers.

Examples

- Controller → Service → Repository
- Authentication workflow
- Resume upload
- ATS generation

Integration tests should use a dedicated test database.

---

## API Testing

Every public endpoint should be tested.

Tests should verify:

- Request validation
- Authentication
- Authorization
- HTTP status codes
- Response body
- Error handling

MockMvc or RestAssured may be used.

---

## End-to-End Testing

Purpose

Validate complete user workflows.

Examples

- User Registration
- Login
- Resume Upload
- ATS Analysis
- Dashboard Update

Future

Playwright or Cypress for frontend automation.

---

# Code Review Standards

Every Pull Request should be reviewed before merging.

Reviewers should evaluate:

- Business correctness
- Readability
- Performance
- Security
- Test coverage
- Documentation updates

A Pull Request should never be approved solely because it compiles successfully.

---

## Code Review Checklist

Architecture

✓ Follows module boundaries

✓ No circular dependencies

✓ No duplicated logic

---

Business Logic

✓ Requirements implemented

✓ Business rules respected

✓ Edge cases handled

---

Code Quality

✓ SOLID principles

✓ Clean Code

✓ Small methods

✓ Clear naming

✓ No dead code

---

Security

✓ Input validation

✓ Authorization checks

✓ Sensitive data protected

✓ Passwords encrypted

✓ File validation implemented

---

Performance

✓ No unnecessary database queries

✓ Pagination where required

✓ No N+1 query issues

✓ Efficient algorithms

---

Testing

✓ Unit tests added

✓ Integration tests updated

✓ Existing tests pass

---

Documentation

✓ Public APIs documented

✓ Architecture updated (if necessary)

✓ New configuration explained

---

# Performance Optimization

Backend performance should be continuously monitored.

General Guidelines

- Avoid unnecessary object creation.
- Minimize database round trips.
- Prefer batch operations.
- Keep transactions short.
- Avoid blocking operations.
- Use asynchronous processing for long-running tasks.

---

## Database Optimization

Recommended practices

- Index frequently queried columns.
- Use pagination for large datasets.
- Avoid SELECT * queries.
- Fetch only required fields.
- Review execution plans for slow queries.

Future improvements

- Query optimization
- Read replicas
- Redis cache
- Materialized views (if required)

---

## Asynchronous Processing

Operations that may take several seconds should execute asynchronously.

Examples

- Resume Parsing
- ATS Analysis
- AI Recommendation Generation
- Email Sending
- PDF Generation

Future implementation

Spring Events

↓

Message Queue

↓

Worker Service

↓

Notification

This prevents long user wait times.

---

# Scalability Strategy

CareerInDe is intentionally designed as a Modular Monolith.

Scaling should occur in stages.

---

## Stage 1

Single Spring Boot application

Single PostgreSQL database

Single VPS

---

## Stage 2

Redis Cache

Dedicated File Storage

Background Jobs

Improved Monitoring

---

## Stage 3

Independent AI Service

Dedicated Search Service

Notification Service

Object Storage

---

## Stage 4

Microservices (only when justified)

Potential candidates

- AI Service
- ATS Service
- Notification Service
- Analytics Service

Migration should be driven by measurable bottlenecks rather than assumptions.

---

# Backend Definition of Done

A backend feature is complete only when all of the following conditions are satisfied.

Business

✓ Requirements implemented

✓ Acceptance criteria satisfied

Technical

✓ Code reviewed

✓ Tests written

✓ Logging added

✓ Validation completed

✓ Security reviewed

✓ Documentation updated

Operational

✓ Deployable

✓ No known critical bugs

✓ Monitoring ready

✓ Configuration documented

---

# Production Readiness Checklist

Before deploying to production, verify:

Infrastructure

✓ Environment variables configured

✓ Database migrations applied

✓ HTTPS enabled

✓ Backups configured

Application

✓ Logging enabled

✓ Error handling verified

✓ Security configuration reviewed

✓ Performance tested

Business

✓ Feature approved

✓ Documentation complete

✓ Rollback plan prepared

---

# Backend Anti-Patterns

The following practices are prohibited.

❌ Business logic inside controllers

❌ Repository access from controllers

❌ Hardcoded configuration values

❌ Static mutable state

❌ Catching generic Exception without handling

❌ Large God Services

❌ Duplicate validation logic

❌ SQL inside controllers

❌ Circular dependencies

❌ Direct AI provider calls from business modules

---

# Backend Roadmap

Version 0.1

- Authentication
- Profile
- Resume Upload
- Dashboard

Version 0.5

- Resume Parser
- ATS Engine
- AI Recommendation Engine

Version 1.0

- Career GPS
- Application Tracker
- Subscription Support
- Interview Coach

Version 2.0

- Recruiter Portal
- University Portal
- Enterprise APIs

---

# Backend Engineering Principles Summary

Every backend implementation should answer the following questions before being merged:

- Does it solve a real business problem?
- Does it follow the existing architecture?
- Is the code readable after six months?
- Can it be tested independently?
- Is it secure?
- Is it documented?
- Is it scalable?
- Is it maintainable?

If any answer is **No**, the implementation should be reconsidered.

---

# End of Backend Context

This document defines the engineering standards for the CareerInDe backend.

Every controller, service, repository, entity, configuration class and future backend module should comply with these principles.

The Backend Context is a living document and should evolve together with the architecture, business requirements and engineering best practices.

