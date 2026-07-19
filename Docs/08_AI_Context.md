<!-- ========================================================= -->
<!-- CareerInDe -->
<!-- AI CONTEXT -->
<!-- Version 1.0 -->
<!-- ========================================================= -->

# CareerInDe

# AI Context

> **AI Architecture, LLM Strategy, Prompt Engineering and Intelligent Career Systems**

---

# Document Metadata

| Property | Value |
|---|---|
| Project | CareerInDe |
| Document | AI Context |
| Version | 1.0 |
| Status | Living Document |
| Owner | AI and Backend Engineering Team |
| Category | Technical and Product Documentation |
| Purpose | Define the complete AI architecture, responsibilities, safety rules and implementation strategy |
| Primary AI Domain | Career Intelligence |
| Current Providers | Groq |
| Planned Providers | OpenAI and additional providers |
| Current AI Status | Basic resume and ATS analysis implemented or partially implemented |
| Target Architecture | Provider-independent AI platform layer |
| Last Updated | 2026 |

---

# About this Document

This document defines the complete artificial intelligence strategy of CareerInDe.

AI is not treated as an isolated feature or external API integration.

It is a central platform capability that supports multiple product domains, including:

- Resume analysis
- ATS scoring
- Skill detection
- Skill-gap analysis
- Career recommendations
- Career GPS
- Interview preparation
- Cover-letter generation
- Job matching
- Career planning
- Germany-readiness evaluation
- Long-term career guidance

The purpose of this document is to ensure that all AI capabilities remain consistent, explainable, secure, cost-efficient and aligned with the product vision.

Every new AI feature should follow the architecture and principles defined in this document.

---

# Purpose

The AI Context establishes the rules for designing, implementing, testing and operating CareerInDe’s intelligent systems.

It defines:

- AI product philosophy
- AI architecture
- LLM provider strategy
- Prompt engineering standards
- Context construction
- Output validation
- Resume intelligence
- ATS analysis
- Skill-gap analysis
- Recommendation engine
- Career GPS
- Interview Coach
- Cover-letter generation
- AI memory
- Evaluation methodology
- Cost management
- AI safety
- Privacy
- Monitoring
- Future AI roadmap

This document should be read together with:

```text
01_Executive_Context.md
02_Business_Context.md
03_Product_Context.md
04_Backend_Context.md
05_Database_Context.md
06_API_Context.md
07_Security_Context.md
```

---

# AI Vision

CareerInDe aims to become an AI-powered Career Operating System.

The platform should not merely generate text.

It should understand the user’s professional situation, evaluate career readiness, identify gaps and recommend the next best action.

The long-term AI vision is:

> Build a persistent, explainable and personalized career intelligence system that continuously helps international professionals improve their employability in Germany and Europe.

The system should become more useful as the user provides additional data.

Examples:

- New resume
- New skill
- New certificate
- Updated language level
- New job application
- Interview result
- Rejection reason
- Career goal change
- Salary expectation
- Preferred city
- Learning progress

AI recommendations should evolve when the user’s situation changes.

---

# AI Mission

The mission of CareerInDe AI is to transform complex career information into clear, prioritized and actionable guidance.

The AI system should help users answer questions such as:

- How strong is my resume?
- Why am I not receiving interviews?
- Which skills am I missing?
- What should I improve first?
- Which job roles fit my experience?
- How ready am I for the German labor market?
- What should I learn during the next three months?
- How should I prepare for an interview?
- Which resume version should I use?
- What is my next best career action?

The objective is not to replace human judgment.

The objective is to reduce uncertainty and improve decision quality.

---

# AI Product Philosophy

CareerInDe follows several fundamental AI principles.

---

## 1. AI Must Produce Action

AI output must lead to a clear next step.

Weak output:

```text
Your resume could be improved.
```

Preferred output:

```text
Add two measurable outcomes to your latest Java project.

Example:

“Reduced report-generation time by 30% by automating KPI reporting with SQL and Power BI.”
```

Every recommendation should be:

- Specific
- Prioritized
- Explainable
- Actionable
- Relevant to the user
- Connected to a measurable career outcome

---

## 2. AI Must Use Context

Generic responses provide limited value.

Recommendations should use available user context, including:

- Current job title
- Target job
- Experience
- Education
- Skills
- Language levels
- Location
- Preferred city
- Salary expectations
- Resume content
- ATS results
- Career goals
- Application history
- Previous recommendations

A recommendation without sufficient context should clearly state its uncertainty.

---

## 3. AI Must Remain Explainable

Users should understand why a recommendation was generated.

Example:

```text
Recommendation:
Improve Docker experience.

Reason:
Docker appears in 68% of the analyzed Java backend job descriptions,
but it is not demonstrated in your project-experience section.
```

AI should avoid unexplained scores and hidden decisions.

---

## 4. AI Must Not Guarantee Outcomes

CareerInDe must never claim that a user will definitely:

- Receive an interview
- Obtain a job
- Receive a specific salary
- Pass an interview
- Be approved for a visa
- Be selected by an employer

Preferred language:

```text
This change may improve your compatibility with similar job descriptions.
```

Incorrect language:

```text
This change will guarantee an interview.
```

---

## 5. AI Must Support Human Agency

Users remain responsible for final decisions.

The AI system should:

- Recommend
- Explain
- Compare
- Prioritize
- Warn
- Suggest alternatives

It should not make irreversible decisions on behalf of users without explicit confirmation.

---

## 6. AI Must Be Provider Independent

Business modules should not depend directly on one LLM provider.

Incorrect:

```text
ResumeService
    ↓
Groq API
```

Preferred:

```text
ResumeService
    ↓
AI Orchestration Service
    ↓
Provider Interface
    ↓
Groq / OpenAI / Future Provider
```

This architecture prevents vendor lock-in.

---

## 7. AI Must Be Cost-Aware

Every AI request has an operational cost.

The system should avoid:

- Duplicate requests
- Excessively long prompts
- Reanalyzing unchanged resumes
- Sending unnecessary profile data
- Using expensive models for simple tasks
- Unlimited uncontrolled generation

AI cost should be treated as a product and architecture constraint.

---

## 8. AI Must Be Safe

AI features must account for:

- Prompt injection
- Data leakage
- Hallucination
- Biased recommendations
- Malformed responses
- Provider failure
- Unsafe career advice
- Excessive confidence
- Protected characteristics
- Cost abuse

AI output must always pass through validation before becoming a business result.

---

# Current AI Status

CareerInDe currently contains or has discussed several early AI capabilities.

## Implemented or Partially Implemented

- PDF resume text extraction
- Keyword-based ATS scoring
- Detected skills
- Missing skills
- Basic recommendations
- Groq API integration
- Structured AI output parsing
- Display of AI results on the resume result page

Example AI output previously generated:

```text
ATS_SCORE: 85

STRENGTHS:
- Java
- Spring Boot
- PostgreSQL

MISSING_SKILLS:
- Kubernetes
- AWS

RECOMMENDATIONS:
- Add measurable achievements.
- Demonstrate deployment experience.
- Improve GitHub project presentation.
```

---

## Known Historical Issue

A Groq API request previously returned:

```text
400 Bad Request
```

Possible causes discussed included:

- Incorrect request schema
- Incorrect model name
- Invalid message structure
- Missing or malformed headers
- Response-format mismatch

Later project progress indicated that structured AI output was successfully received.

The current repository must be reviewed to confirm the exact implementation and remaining technical debt.

---

## Planned AI Capabilities

- Advanced ATS analysis
- Job-description matching
- Resume rewriting
- Career GPS
- Skill-gap analysis
- AI Career Coach
- Interview Coach
- Cover-letter generation
- Application strategy
- Learning recommendations
- Germany-readiness evaluation
- Salary-positioning guidance
- Career decision engine
- Execution engine
- AI memory
- Recommendation history
- Multi-provider routing

---

# AI System Boundaries

The AI system is responsible for:

- Analysis
- Classification
- Recommendation
- Explanation
- Prioritization
- Structured generation
- Comparison
- Summarization
- Planning support

The AI system is not responsible for:

- Authentication
- Authorization
- Payment processing
- Direct database access
- User-role assignment
- Security decisions
- Final employment decisions
- Legal decisions
- Visa decisions
- Automatic account modification
- Direct file storage

These responsibilities remain in deterministic backend services.

---

# High-Level AI Architecture

```text
User Action
    ↓
Product Module
    ↓
AI Orchestration Service
    ↓
Context Builder
    ↓
Prompt Template
    ↓
Provider Router
    ↓
LLM Provider
    ↓
Raw Response
    ↓
Response Parser
    ↓
Schema Validation
    ↓
Business Validation
    ↓
Persistence
    ↓
User-Facing Result
```

Every stage has a separate responsibility.

---

# AI Architecture Layers

The CareerInDe AI platform should be divided into the following layers.

```text
Product Layer
    ↓
AI Use-Case Layer
    ↓
Context Layer
    ↓
Prompt Layer
    ↓
Provider Layer
    ↓
Validation Layer
    ↓
Persistence and Analytics Layer
```

---

# Product Layer

The Product Layer contains business modules that require AI assistance.

Examples:

- Resume
- ATS
- Career GPS
- Interview Coach
- Application Tracker
- Skill Gap
- Dashboard
- Career Profile

Product modules should request business outcomes.

Example:

```text
Analyze this resume for a Java Backend Developer target role.
```

They should not manage provider-specific payloads.

---

# AI Use-Case Layer

The use-case layer coordinates one AI business operation.

Example services:

```text
ResumeAnalysisService
AtsAnalysisService
SkillGapAnalysisService
CareerRecommendationService
CareerGpsService
InterviewCoachService
CoverLetterService
JobMatchService
```

Each service should have one clear responsibility.

---

# Context Layer

The Context Layer decides which information is required for a specific AI request.

Possible inputs include:

- User profile
- Resume text
- Parsed skills
- Target job
- Target location
- Job description
- Language level
- ATS history
- Previous recommendations
- Application history
- Learning progress

The context builder should include only relevant information.

Sending the complete user history to every request is prohibited.

---

# Prompt Layer

The Prompt Layer defines structured and reusable prompt templates.

A prompt should contain:

- System role
- Task
- Trusted business rules
- User context
- Untrusted document data
- Output schema
- Constraints
- Safety instructions
- Quality criteria

Prompt text should not be hardcoded across unrelated service classes.

---

# Provider Layer

The Provider Layer communicates with external or local models.

Target interface:

```java
public interface LlmProvider {

    AiResponse generate(AiRequest request);

}
```

Possible implementations:

```text
GroqLlmProvider
OpenAiLlmProvider
AnthropicLlmProvider
LocalLlmProvider
```

The application should be able to replace providers without rewriting business services.

---

# Validation Layer

LLM output must be treated as untrusted.

Validation includes:

- JSON parsing
- Required field checks
- Score range checks
- Enum validation
- Maximum-length validation
- Empty-result detection
- Unsupported-value rejection
- Content sanitization
- Business-rule validation

Example:

```text
ATS score must be an integer between 0 and 100.
```

---

# Persistence and Analytics Layer

AI results should be stored with sufficient metadata.

Recommended metadata:

```text
user_id
resume_id
use_case
provider
model
prompt_version
analysis_version
input_hash
created_at
duration_ms
input_tokens
output_tokens
estimated_cost
status
error_code
```

This information supports:

- Reproducibility
- Debugging
- Cost analysis
- Evaluation
- Provider comparison
- Historical comparison

---

# AI Request Lifecycle

```text
Request Created
    ↓
Authorization Verified
    ↓
Usage Limit Checked
    ↓
Context Collected
    ↓
Sensitive Data Minimized
    ↓
Prompt Built
    ↓
Provider Selected
    ↓
Request Sent
    ↓
Response Received
    ↓
Response Validated
    ↓
Business Result Created
    ↓
Result Stored
    ↓
Usage Recorded
    ↓
Response Returned
```

---

# Provider Strategy

CareerInDe should follow a multi-provider strategy.

No provider should become permanently embedded in core business logic.

---

## Current Provider: Groq

Potential advantages:

- Fast inference
- Low-cost or free development usage
- Access to open models
- Suitable for experimentation

Potential limitations:

- Model availability may change
- Rate limits
- Provider-specific request format
- Structured-output reliability
- Commercial terms may evolve

Groq is appropriate for development and selected production use cases when output quality is sufficient.

---

## Planned Provider: OpenAI

Potential advantages:

- Strong model quality
- Structured output support
- Mature API ecosystem
- Broad model selection
- Reliable tool and JSON capabilities

Potential limitations:

- Cost
- Data-processing review
- Vendor dependency
- Rate limits
- Regional-processing considerations

---

## Future Providers

Possible providers:

- Anthropic
- Google Gemini
- Azure OpenAI
- Mistral
- AWS Bedrock
- Local models
- EU-hosted providers

Providers should be evaluated by use case rather than brand preference.

---

# Provider Selection Criteria

Each provider should be evaluated against:

| Criterion | Description |
|---|---|
| Quality | Accuracy and relevance |
| Structured Output | JSON/schema reliability |
| Latency | Response speed |
| Cost | Cost per request |
| Privacy | Data processing and retention |
| Availability | Reliability and limits |
| Model Choice | Suitable models |
| Regional Hosting | EU processing options |
| Safety | Moderation and controls |
| Vendor Risk | Lock-in and contract stability |

---

# Model Routing Strategy

Different tasks may require different models.

Example:

```text
Simple classification
    → Fast low-cost model

Resume rewriting
    → Higher-quality language model

Career planning
    → Strong reasoning model

Skill extraction
    → Structured-output model

Embeddings
    → Dedicated embedding model
```

The most expensive model should not be used by default.

---

# AI Configuration

AI configuration should be externalized.

Example environment variables:

```text
AI_DEFAULT_PROVIDER=groq
AI_DEFAULT_MODEL=...
AI_REQUEST_TIMEOUT_SECONDS=30
AI_MAX_RETRIES=2
AI_MAX_INPUT_TOKENS=12000
AI_MAX_OUTPUT_TOKENS=2000
AI_MONTHLY_BUDGET_EUR=...
```

API keys must never be committed to Git.

---

# AI Failure Handling

AI providers may fail because of:

- Timeout
- Rate limit
- Invalid request
- Invalid model
- Provider outage
- Malformed response
- Safety refusal
- Token limit
- Network error

The application should translate provider-specific errors into stable internal errors.

Example error codes:

```text
AI_PROVIDER_UNAVAILABLE
AI_RATE_LIMITED
AI_RESPONSE_INVALID
AI_INPUT_TOO_LARGE
AI_ANALYSIS_FAILED
AI_TIMEOUT
```

---

# Retry Strategy

Retries should be limited.

Recommended rules:

- Retry temporary network failures.
- Retry selected 5xx responses.
- Respect provider retry headers.
- Do not retry invalid requests.
- Do not retry safety refusals blindly.
- Use exponential backoff.
- Prevent duplicate paid operations.

---

# Fallback Strategy

Future provider fallback may follow:

```text
Primary Provider
    ↓ failure
Secondary Provider
    ↓ failure
Deterministic Basic Analysis
    ↓
User-Friendly Error
```

Fallback must not silently produce significantly different results without recording the provider and model.

---

# AI Availability Principle

Core non-AI product functions must remain usable during an AI outage.

Users should still be able to:

- Log in
- Access profile
- View existing resumes
- View previous ATS results
- Track applications
- Access stored recommendations

AI failure should degrade the product gracefully rather than make the entire platform unavailable.

---

# AI Cost Governance

AI usage should be measured for every request.

Recommended metrics:

- Requests per user
- Tokens per use case
- Cost per analysis
- Cost per paying user
- Failed-request cost
- Cache-hit rate
- Provider distribution
- Average latency

---

# Usage Limits

Limits may depend on subscription plan.

Example:

| Feature | Free | Pro | Premium |
|---|---:|---:|---:|
| Resume analysis | 1/month | 10/month | Higher limit |
| ATS job matching | Limited | Included | Included |
| Cover letters | Limited | Monthly quota | Higher quota |
| Interview sessions | No | Limited | Included |
| Career GPS refresh | Quarterly | Monthly | On demand |

Exact limits require business validation.

---

# AI Caching

AI results may be cached when inputs are unchanged.

A cache key may include:

```text
user_id
resume_hash
target_job
job_description_hash
prompt_version
model
```

Caching can reduce:

- Cost
- Latency
- Duplicate processing

AI results should be regenerated when relevant source data changes.

---

# AI Input Hashing

Before an analysis, CareerInDe may calculate a hash of relevant inputs.

Example:

```text
SHA-256(
    normalized_resume_text
    + target_job
    + prompt_version
    + analysis_version
)
```

If the same analysis already exists, the stored result may be reused.

---

# Prompt Versioning

Every production prompt must have a version.

Example:

```text
resume-analysis-v1
ats-general-v2
career-gps-v1
skill-gap-v3
```

Prompt changes should be tracked like code changes.

Stored AI results should record the prompt version used.

---

# Analysis Versioning

Prompt version and algorithm version are different.

Example:

```text
prompt_version = ats-prompt-v3
analysis_version = ats-engine-0.2
```

This distinction supports reproducibility.

---

# AI Result Status

Recommended statuses:

```text
QUEUED
PROCESSING
COMPLETED
FAILED
PARTIAL
INVALID
CANCELLED
```

Long-running AI tasks should support asynchronous processing in future versions.

---

# AI Design Principles Summary

Every AI feature must answer:

- What user problem does it solve?
- What context does it require?
- Which data is unnecessary?
- Which provider and model are appropriate?
- What is the expected output schema?
- How is the output validated?
- How is cost controlled?
- How is uncertainty explained?
- How is the feature tested?
- What happens when the provider fails?

An AI feature is not production-ready until all questions have documented answers.



