
# CareerInDe Master Context

> **Single Source of Truth for the CareerInDe Platform**

------------------------------------------------------------------------

# Document Metadata

  Property   Value
  ---------- -------------------------
  Project    CareerInDe
  Product    AI-Powered HR-Tech SaaS
  Document   Master Context
  Status     Living Document
  Version    1.0 (Draft)
  Owner      CareerInDe Team
  Purpose    Project Constitution

------------------------------------------------------------------------

# About this Document

This document is the constitutional foundation of the CareerInDe
project.

Unlike a README, this document defines the permanent vision, engineering
philosophy, product direction and architectural principles that guide
every future decision.

Every developer, architect, product manager and AI assistant should read
this document before contributing to the project.

------------------------------------------------------------------------

# Purpose

CareerInDe aims to become an AI-powered Career Operating System for
Germany.

This document ensures that:

-   Product vision remains consistent.
-   Architecture evolves safely.
-   Business decisions remain aligned.
-   Documentation becomes the Single Source of Truth.

------------------------------------------------------------------------

# Reading Guide

Recommended order:

1.  Executive Overview
2.  Vision
3.  Mission
4.  Product Philosophy
5.  Engineering Philosophy
6.  Architecture
7.  Business
8.  Technical Modules

------------------------------------------------------------------------

# Project Identity

## Name

CareerInDe

## Category

-   HR-Tech
-   Artificial Intelligence
-   SaaS
-   Career Platform

------------------------------------------------------------------------

# Executive Overview

CareerInDe is being built to solve one core problem:

International professionals struggle to understand what they need in
order to build a successful career in Germany.

Instead of offering only resume analysis or job search, CareerInDe
combines AI, ATS analysis, career planning, application tracking and
continuous guidance into one ecosystem.

------------------------------------------------------------------------

# Vision

Become Europe's leading AI-powered Career Operating System.

------------------------------------------------------------------------

# Mission

Empower international professionals to build successful careers in
Germany through intelligent software, structured guidance and AI-driven
recommendations.

------------------------------------------------------------------------

# Core Values

-   User First
-   AI First
-   Documentation First
-   Security by Design
-   Privacy by Default
-   Scalability
-   Continuous Improvement

------------------------------------------------------------------------

# Product Position

CareerInDe is **not** merely:

-   a job board
-   a resume builder
-   an ATS checker

CareerInDe is an AI Career Operating System.

------------------------------------------------------------------------

# Current Technical Direction

Current Architecture

-   Modular Monolith
-   Java 21
-   Spring Boot 3
-   PostgreSQL
-   Thymeleaf
-   Docker (planned)

Future

-   Angular Frontend
-   JWT
-   AI Career Coach
-   Career GPS
-   Recommendation Engine

------------------------------------------------------------------------

# Long-Term Goal

Transform CareerInDe into a scalable HR-Tech platform serving:

-   International Students
-   Skilled Workers
-   Universities
-   Recruiters
-   Employers

This document will continuously evolve and remains the highest-level
reference for all future documentation.

---

# Product Philosophy

CareerInDe is not designed as a collection of isolated features.

It is designed as a unified ecosystem where every component contributes to a single objective:

> Helping international professionals build successful careers in Germany through continuous, AI-assisted guidance.

Every product decision should strengthen this ecosystem rather than introduce disconnected functionality.

The platform follows several long-term principles.

---

## AI First

Artificial Intelligence is considered a core capability rather than an optional enhancement.

Every major workflow should eventually benefit from AI.

Examples include:

- Resume Analysis
- ATS Evaluation
- Career Roadmaps
- Interview Coaching
- Cover Letter Generation
- Career Recommendation Engine
- Skill Gap Analysis
- Career GPS

AI should reduce uncertainty rather than replace human decision-making.

The objective is augmentation, not automation.

---

## User First

Every feature must solve a measurable user problem.

Technology is never introduced simply because it is modern.

Every implementation should answer at least one question:

- Does it reduce user effort?
- Does it increase employability?
- Does it improve career outcomes?
- Does it simplify decision making?

If the answer is "No", the feature should be reconsidered.

---

## Documentation First

Documentation is considered part of the software.

No significant architectural decision should exist only inside source code.

Every important module should have documentation covering:

- Purpose
- Business Value
- Architecture
- Dependencies
- Future Improvements
- Risks

The documentation itself evolves alongside the product.

---

## Security by Design

Security is integrated from the beginning.

It is never treated as an afterthought.

The platform should protect:

- User Identity
- Personal Information
- Resume Data
- AI Conversations
- Application History

Future security measures include:

- JWT Authentication
- RBAC
- Audit Logs
- Email Verification
- Password Reset
- Rate Limiting
- Encryption of Sensitive Data

---

## Privacy by Default

CareerInDe processes highly sensitive career information.

Therefore the platform should minimize data collection.

Only information required for improving the user's career journey should be stored.

Users should always understand:

- why information is collected
- how it is used
- how it can be removed

Future versions should comply with GDPR principles.

---

## Simplicity over Complexity

Users should never experience internal system complexity.

Even if AI pipelines become sophisticated, the interface should remain intuitive.

The product should continuously remove friction rather than add configuration options.

---

## Continuous Improvement

Career development never ends.

CareerInDe therefore should continuously improve user recommendations as new information becomes available.

Examples:

- new resume uploaded
- new language certificate
- completed interview
- rejected application
- accepted offer
- updated LinkedIn profile

Each event should improve future recommendations.

---

# Engineering Philosophy

CareerInDe is engineered for long-term maintainability rather than short-term speed.

Every implementation should optimize for readability, scalability and consistency.

---

## Clean Architecture

Business logic should remain independent from frameworks whenever possible.

Spring Boot provides infrastructure.

Business rules belong to the application.

---

## Modular Monolith

Current architecture:

Modular Monolith

Reasons:

- Faster development
- Easier deployment
- Lower infrastructure costs
- Simpler debugging
- Reduced operational complexity

Microservices will only be introduced when justified by measurable scaling requirements.

---

## SOLID Principles

All backend development should follow SOLID principles.

Benefits include:

- maintainability
- extensibility
- testability
- lower coupling

---

## Clean Code

Every class should have one clear responsibility.

Methods should remain short.

Variable names should be explicit.

Avoid unnecessary abstractions.

Readable code is preferred over clever code.

---

## Testability

Every business-critical service should be designed for testing.

Future testing strategy includes:

- Unit Tests
- Integration Tests
- API Tests
- End-to-End Tests

---

## Documentation Driven Development

Whenever possible:

Design

↓

Documentation

↓

Implementation

↓

Testing

↓

Deployment

instead of

Implementation

↓

Documentation

---

# Product Scope

CareerInDe currently focuses on supporting international professionals throughout their complete career journey.

Current functional domains include:

- User Authentication
- User Profiles
- Resume Management
- ATS Analysis
- Career Recommendations
- Dashboard
- Application Tracking

Future domains include:

- AI Career Coach
- Interview Coach
- Learning Recommendations
- Networking Assistant
- Recruiter Portal
- University Portal
- Enterprise Dashboard

---

# Out of Scope

The following capabilities are intentionally excluded from the current product vision.

- Job Marketplace
- Payroll
- Employee Monitoring
- Time Tracking
- HR Administration
- ERP Integration

CareerInDe focuses on career intelligence rather than enterprise HR management.

---

# Business Domain

CareerInDe operates in the intersection of multiple industries.

Instead of competing directly within a single market, the platform combines technologies and methodologies from several domains to create a unified Career Intelligence Platform.

Primary domains include:

- HR Technology (HR-Tech)
- Artificial Intelligence
- Software as a Service (SaaS)
- Career Development
- Recruitment Technology
- Professional Education
- Employability Analytics

Future expansion domains may include:

- Enterprise HR Solutions
- University Career Services
- Workforce Analytics
- Corporate Talent Development

---

# Market Opportunity

Germany is experiencing one of the largest skilled labor shortages in Europe.

Every year:

- Thousands of international students graduate from German universities.
- Thousands of skilled workers immigrate through various visa programs.
- Companies continue reporting difficulties finding qualified employees.

Despite this demand, many qualified candidates remain unemployed or underemployed because they struggle to position themselves effectively.

CareerInDe addresses this gap by improving employability rather than simply increasing job applications.

---

# Target Market

## Primary Market

International professionals living in Germany.

Examples include:

- International Students
- Recent Graduates
- Skilled Workers
- Career Changers
- IT Professionals
- Engineers
- Data Analysts
- Software Developers

---

## Secondary Market

Organizations supporting international professionals.

Examples:

- Universities
- Career Centers
- Immigration Services
- Language Schools
- Professional Training Providers

---

## Future Enterprise Market

Long-term enterprise customers include:

- Recruitment Agencies
- HR Departments
- Large Employers
- Government Employment Programs
- Universities
- Educational Institutions

---

# User Personas

## Persona 1 — International Student

Age:

20–30

Goals:

- Find first internship
- Build German resume
- Improve German employability
- Understand labor market expectations

Challenges:

- Limited experience
- No professional network
- ATS compatibility
- Language barriers

CareerInDe helps by:

- Resume optimization
- ATS analysis
- Career roadmap
- AI coaching

---

## Persona 2 — Skilled Professional

Age:

25–45

Goals:

- Better salary
- Career progression
- Industry transition

Challenges:

- Resume quality
- Skill visibility
- Interview preparation

CareerInDe assists through:

- Career GPS
- Resume recommendations
- Skill Gap Analysis
- Interview Coach

---

## Persona 3 — Career Changer

Goals:

Move into a new industry.

Needs:

- Learning roadmap
- Skill recommendations
- AI-generated career plans

---

## Persona 4 — University Career Center

Goals:

Improve graduate employment rate.

Potential future capabilities:

- Student dashboards
- Employability analytics
- Aggregate reporting
- Career readiness monitoring

---

# Stakeholders

Primary Stakeholders

- End Users
- Founders
- Product Team
- Backend Team
- AI Team
- Frontend Team

Secondary Stakeholders

- Universities
- Employers
- Recruiters
- Investors

Future Stakeholders

- Enterprise Customers
- Government Programs
- Training Organizations

---

# Strategic Objectives

CareerInDe follows three planning horizons.

---

## Short-Term Objectives (0–12 Months)

- Deliver MVP
- Complete Authentication
- Complete Profile Module
- Complete Resume Upload
- Implement ATS Engine
- Launch Dashboard
- Deploy Production Environment
- Acquire First Users

---

## Medium-Term Objectives (1–3 Years)

- Angular Migration
- AI Career Coach
- Interview Coach
- Career GPS
- Job Tracker
- Subscription Plans
- Enterprise Dashboard
- Recruiter Portal

---

## Long-Term Objectives (3–7 Years)

Become the leading AI-powered Career Operating System in Europe.

Support:

- Millions of users
- Enterprise customers
- Universities
- Government organizations

Expand into additional European markets.

---

# Success Metrics

The success of CareerInDe is measured using business, product and technical KPIs.

## Business KPIs

- Monthly Active Users (MAU)
- Daily Active Users (DAU)
- Conversion Rate
- Monthly Recurring Revenue (MRR)
- Annual Recurring Revenue (ARR)
- Customer Acquisition Cost (CAC)
- Lifetime Value (LTV)

---

## Product KPIs

- Resume Completion Rate
- ATS Score Improvement
- Profile Completion
- Career Roadmap Completion
- Job Application Tracking Usage
- AI Recommendation Acceptance

---

## Technical KPIs

- API Response Time
- Error Rate
- Deployment Frequency
- Test Coverage
- Uptime
- Build Success Rate
- Database Performance

---

# Core Platform Modules

Current Modules

- Authentication
- User Management
- Profile
- Resume Upload
- ATS Analyzer
- Dashboard

Modules in Development

- Resume Parser
- AI Recommendation Engine
- Skill Analysis
- Career Profile

Planned Modules

- Career GPS
- Interview Coach
- Cover Letter Generator
- Learning Center
- Application Tracker
- Notification Center

Future Modules

- Recruiter Portal
- University Portal
- Enterprise Dashboard
- Marketplace
- Analytics Platform

---

# Capability Map

Core Platform

│

├── Authentication

├── User Profile

├── Resume Center

├── ATS Engine

├── AI Services

├── Career GPS

├── Dashboard

├── Application Tracker

├── Analytics

└── Administration

---

# Business Rules

Every feature implemented within CareerInDe must satisfy at least one of the following objectives:

- Increase employability.
- Improve career readiness.
- Reduce uncertainty.
- Save user time.
- Improve ATS compatibility.
- Provide measurable value.

Features that do not contribute to these objectives should not be included in the MVP.

---

---

# High-Level Architecture

CareerInDe is designed as a modern AI-powered SaaS platform following a **Modular Monolith Architecture**.

This architectural choice is intentional and based on the current product stage.

At the MVP stage, development speed, maintainability and operational simplicity provide greater value than premature distribution into microservices.

The architecture is therefore optimized for:

- Fast development
- Low operational cost
- High maintainability
- Clear module boundaries
- Future scalability

---

## Current Architecture

```
                    CareerInDe

                           │

               Spring Boot Application

                           │

 ┌──────────────┬──────────────┬──────────────┐

 Authentication   Career Core       AI Layer

 │

 User

 Profile

 Resume

 ATS

 Dashboard

 Applications

                           │

                     PostgreSQL

```

---

## Architectural Principles

CareerInDe follows the following architecture principles.

### 1. Modular Monolith

The application is deployed as one executable.

Internally it is divided into independent modules.

Benefits

- easier deployment

- lower hosting costs

- simpler debugging

- transaction consistency

- faster MVP delivery

---

### 2. Feature-Oriented Organization

Instead of grouping code by technical layer only,
the project is organized around business capabilities.

Example

```
profile/

controller

service

repository

dto

entity

mapper
```

instead of

```
controllers/

services/

repositories/

entities/
```

This makes every business module self-contained.

---

### 3. Layer Separation

Every module follows the same internal structure.

```
Controller

↓

Service

↓

Repository

↓

Database
```

Controllers never access the database directly.

Business logic belongs only inside services.

Repositories never contain business logic.

---

### 4. Single Responsibility Principle

Each class should have one responsibility.

Examples

Good

```
ResumeParserService
```

Bad

```
ResumeParserAndAtsCalculatorAndPdfExporterService
```

---

### 5. Dependency Direction

Dependencies always point inward.

```
UI

↓

Controller

↓

Service

↓

Repository

↓

Database
```

The reverse direction is never allowed.

---

# Repository Structure

```
careerinde-app/

src/

main/

java/

resources/

templates/

static/

docker/

docs/

```

Future repositories

```
careerinde-docs

careerinde-ai

careerinde-infra

careerinde-mobile
```

---

# Technology Stack

## Backend

Java 21

Spring Boot 3

Spring Security

Spring Data JPA

Hibernate

Maven

---

## Frontend

Current

Thymeleaf

Future

Angular

TypeScript

HTML

CSS

---

## Database

PostgreSQL

Future

Redis

Elasticsearch

---

## AI

Current

Groq API

Prompt Engineering

ATS Engine

Future

OpenAI

Anthropic

Local Models

Decision Engine

Career GPS

---

## DevOps

Docker

Docker Compose

GitHub

Hetzner VPS

Nginx

Future

GitHub Actions

Monitoring

Backup Automation

---

# Development Workflow

Every feature follows the same lifecycle.

```
Idea

↓

Business Validation

↓

Architecture Review

↓

Documentation

↓

Implementation

↓

Testing

↓

Deployment

↓

Monitoring

↓

Continuous Improvement
```

Implementation should never start before documentation exists.

---

# Git Workflow

Main Branch

```
main
```

Development Branch

```
develop
```

Feature Branch

```
feature/profile

feature/resume

feature/ats

feature/dashboard
```

Bug Fix

```
fix/login

fix/security

fix/upload
```

Hotfix

```
hotfix/production-login
```

---

# Coding Standards

General principles

- SOLID
- DRY
- KISS
- YAGNI

Naming

Classes

```
ResumeService
```

Methods

```
calculateATSScore()
```

Variables

```
resumeScore
```

Constants

```
MAX_UPLOAD_SIZE
```

Packages

Lowercase only.

---

# Documentation First

Every feature must include documentation.

Minimum documentation:

- Purpose
- Business Value
- API
- Database Changes
- Future Improvements

---

# Definition of Done

A feature is considered complete only if:

✅ Business requirements implemented

✅ Unit tests written

✅ API documented

✅ Database migration completed

✅ Error handling implemented

✅ Logging added

✅ Security reviewed

✅ Documentation updated

---

# AI Strategy

Artificial Intelligence is a platform capability.

It is not an isolated feature.

Every AI module should be reusable.

Current AI modules

- Resume Analysis

- ATS Evaluation

Future AI modules

- Career GPS

- Recommendation Engine

- Interview Coach

- Cover Letter Generator

- Skill Gap Analysis

- Career Planner

---

# Security Strategy

Security follows the principle of **Defense in Depth**.

Current

- Spring Security

- BCrypt Password Encoding

- Session Authentication

Future

- JWT

- Refresh Tokens

- MFA

- Email Verification

- Audit Logs

- Rate Limiting

- Account Lockout

- OAuth2

---

# Deployment Strategy

Development

```
Local Machine
```

↓

Docker

↓

GitHub

Production

↓

Hetzner VPS

↓

Nginx

↓

Spring Boot

↓

PostgreSQL

---

# Scalability Strategy

Current

One application

One database

Future

Separate services when required:

- AI Service

- ATS Service

- Notification Service

- Analytics Service

- Search Service

Only migrate after measurable bottlenecks appear.

Premature microservices are explicitly discouraged.

---


---

# Risk Register

CareerInDe is an ambitious long-term platform.

Like every software product, it contains technical, business and operational risks.

These risks should be continuously monitored.

---

## Business Risks

### Low User Adoption

Risk

The platform may fail to attract enough users after launch.

Mitigation

- Continuous user interviews
- MVP validation
- Feature prioritization
- SEO strategy
- LinkedIn content
- University partnerships

---

### Poor Product-Market Fit

Risk

Users may not perceive enough value to pay.

Mitigation

- Continuous feedback collection
- Analytics
- A/B testing
- Iterative feature releases

---

### Competition

Risk

Existing products may introduce similar AI features.

Examples

- LinkedIn
- Jobscan
- Teal
- Kickresume

Mitigation

Focus on complete career intelligence rather than isolated features.

---

# Technical Risks

## AI Cost

Risk

Large Language Models can become expensive.

Mitigation

- Prompt optimization
- Response caching
- Groq for inexpensive inference
- Local models where appropriate

---

## Performance

Risk

Resume parsing and AI analysis may become slow.

Mitigation

- Asynchronous processing
- Queue-based architecture
- Background workers

---

## Database Growth

Risk

Resume files and AI history will increase storage requirements.

Mitigation

- Object Storage
- Database indexing
- Archive strategy

---

## Security Risks

Potential Risks

- Credential theft
- Prompt Injection
- SQL Injection
- XSS
- CSRF
- File Upload attacks
- Account Enumeration

Mitigation

Follow OWASP Top 10.

---

# Assumptions

The project currently assumes:

- Germany remains the primary market.
- AI adoption continues growing.
- International professionals remain the primary audience.
- Spring Boot remains the backend framework.
- PostgreSQL remains the primary database.
- Angular becomes the future frontend.
- Docker remains the deployment standard.

These assumptions should be reviewed annually.

---

# Constraints

Current constraints include:

## Budget

Limited startup budget.

Infrastructure should remain cost-efficient.

---

## Team Size

Currently developed by a very small team.

Architecture should optimize developer productivity.

---

## Time

MVP should be completed before advanced enterprise features.

---

## Infrastructure

Single VPS deployment is acceptable for MVP.

---

# Architecture Decision Records (ADR)

Major architectural decisions

| ADR | Decision | Status |
|------|----------|--------|
| ADR-001 | Modular Monolith | Accepted |
| ADR-002 | Feature-Based Packages | Accepted |
| ADR-003 | Spring Boot | Accepted |
| ADR-004 | PostgreSQL | Accepted |
| ADR-005 | Thymeleaf for MVP | Accepted |
| ADR-006 | Angular after MVP | Planned |
| ADR-007 | JWT after SPA Migration | Planned |
| ADR-008 | AI as Core Platform Capability | Accepted |
| ADR-009 | Documentation First | Accepted |

---

# Governance Model

Every significant change should follow this workflow.

Business Idea

↓

Architecture Review

↓

Documentation

↓

Implementation

↓

Testing

↓

Deployment

↓

Monitoring

↓

Review

Major architectural changes require documentation before implementation.

---

# Versioning Policy

Documentation Version

Semantic Versioning

Example

1.0.0

Major

Architecture changes

Minor

New features

Patch

Documentation corrections

---

# Release Strategy

MVP

↓

Beta

↓

Public Release

↓

Subscription Launch

↓

Enterprise Edition

↓

European Expansion

---

# ChatGPT Collaboration Rules

Future ChatGPT conversations must follow these principles.

## General Rules

Always read the project documentation before making decisions.

Never redesign the architecture without justification.

Never invent features.

Clearly distinguish between:

Implemented

In Progress

Planned

Future

---

## Code Generation

Generated code must be:

Production Ready

Readable

Well Documented

Modular

Testable

Maintainable

Follow Java 21 and Spring Boot best practices.

---

## Documentation Rules

Every significant implementation should update documentation.

Documentation is never optional.

---

## Decision Rules

Every major recommendation should include:

Decision

Reason

Benefits

Risks

Alternatives

Next Steps

---

# Developer Guidelines

Every developer should:

Understand the product vision.

Read architecture documentation.

Avoid duplicate code.

Write meaningful commit messages.

Keep modules independent.

Maintain documentation.

Prefer simplicity over complexity.

---

# Long-Term Vision (2035)

CareerInDe should evolve into a complete AI-powered Career Operating System.

Potential capabilities include:

- AI Career Agent
- Personalized Learning Engine
- Recruiter Intelligence
- University Dashboards
- Enterprise HR Platform
- Labor Market Analytics
- International Expansion
- AI Hiring Assistant
- Career Marketplace

The long-term objective is to become the central platform for career development across Europe.

---

# References

Primary References

- Spring Boot Documentation
- Java 21 Documentation
- PostgreSQL Documentation
- OWASP Top 10
- Docker Documentation
- OpenAPI Specification
- Clean Architecture (Robert C. Martin)
- Domain-Driven Design (Eric Evans)
- C4 Model
- Twelve-Factor App

---

# End of Document

This document is a **Living Document**.

It evolves together with the CareerInDe platform.

Every significant business decision, architectural change, AI capability, product feature or engineering practice should be reflected here.

CareerInDe documentation follows the principle:

> **Documentation First. Implementation Second. Continuous Evolution Always.**

---