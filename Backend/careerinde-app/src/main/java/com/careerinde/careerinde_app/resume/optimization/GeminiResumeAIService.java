package com.careerinde.careerinde_app.resume.optimization;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.careerinde.careerinde_app.ai.gemini.GeminiAIService;

@Service
public class GeminiResumeAIService {

    private final GeminiAIService geminiAIService;

    private static final int MAX_CV_LENGTH = 7000;
    private static final int MAX_JOB_DESCRIPTION_LENGTH = 3500;

    public GeminiResumeAIService(
            GeminiAIService geminiAIService) {

        this.geminiAIService = geminiAIService;
    }


    // =========================================================
    // RESUME OPTIMIZATION
    // =========================================================

    public String optimizeResume(
            String cvText,
            String jobDescription) {

        validateInput(
                cvText,
                jobDescription
        );

        String safeCv =
                limitText(
                        cvText,
                        MAX_CV_LENGTH
                );

        String safeJobDescription =
                limitText(
                        jobDescription,
                        MAX_JOB_DESCRIPTION_LENGTH
                );

        String prompt =
                buildPrompt(
                        safeCv,
                        safeJobDescription
                );

        Map<String, Object> schema =
                buildResumeSchema();


        System.out.println();
        System.out.println(
                "=========================================="
        );
        System.out.println(
                "CAREERINDE RESUME AI"
        );
        System.out.println(
                "Professional Prompt Engine: ENABLED"
        );
        System.out.println(
                "Central Gemini Service: ENABLED"
        );
        System.out.println(
                "Model: " + geminiAIService.getModel()
        );
        System.out.println(
                "=========================================="
        );


        return geminiAIService.generateJson(
                prompt,
                schema,
                0.1,
                5000
        );
    }


    // =========================================================
    // PROFESSIONAL CAREERINDE RESUME PROMPT
    // =========================================================

    private String buildPrompt(
            String cvText,
            String jobDescription) {

        return """
You are CareerInDe's Senior Resume Optimization Engine.

You specialize in:
- professional resume writing
- ATS optimization
- recruiter-oriented resume review
- job-specific resume tailoring
- evidence-based candidate positioning

Your task is to optimize an EXISTING candidate resume
for ONE specific target job.

The final resume must be:

1. factually accurate
2. ATS-friendly
3. recruiter-friendly
4. tailored to the target job
5. concise and professional
6. based ONLY on information contained in the original CV


============================================================
1. ABSOLUTE FACTUAL ACCURACY
============================================================

FACTUAL ACCURACY HAS PRIORITY OVER ATS OPTIMIZATION.

Never invent, infer, assume or fabricate:

- skills
- technologies
- programming languages
- frameworks
- tools
- employers
- companies
- job titles
- employment dates
- universities
- degrees
- certifications
- languages
- projects
- responsibilities
- achievements
- percentages
- numbers
- metrics
- locations
- years of experience
- leadership responsibilities
- industry experience

Every factual claim in the optimized resume MUST be
supported by the ORIGINAL CV.

The job description is NOT evidence about the candidate.

If a requirement appears in the job description but is
not supported by the original CV, DO NOT add it to the resume.

Never transform a job requirement into candidate experience.

If information required by the output schema does not exist
in the original CV, return an empty string or empty array.

Never guess missing information.


============================================================
2. JOB DESCRIPTION ANALYSIS
============================================================

Before optimizing the resume, internally analyze the
target job description.

Identify:

- target position
- primary responsibilities
- required technical skills
- preferred technical skills
- programming languages
- frameworks
- tools
- platforms
- databases
- cloud technologies
- methodologies
- domain knowledge
- education requirements
- experience requirements
- important ATS keywords
- recurring terminology
- business or industry context

Determine which requirements appear to be:

HIGH PRIORITY
MEDIUM PRIORITY
LOW PRIORITY

Do not output this analysis separately.

Use it only to guide resume optimization.


============================================================
3. CANDIDATE EVIDENCE ANALYSIS
============================================================

Analyze the ORIGINAL CV independently.

For every important job requirement, determine whether
there is direct evidence in the CV.

Possible evidence may come from:

- professional experience
- projects
- education
- certifications
- technical skills
- explicitly stated responsibilities

Internally classify requirements as:

SUPPORTED
PARTIALLY SUPPORTED
NOT SUPPORTED

Only SUPPORTED information may be presented as a clear
candidate qualification.

PARTIALLY SUPPORTED information must not be exaggerated.

NOT SUPPORTED information must never be presented as
candidate experience or knowledge.


============================================================
4. RECRUITER RULES
============================================================

Optimize the resume for a recruiter who may initially
scan the document very quickly.

Prioritize:

- direct relevance to the target role
- technical competence
- relevant professional experience
- relevant projects
- clear career positioning
- concise communication
- easy-to-scan information

Put the strongest relevant evidence in prominent positions.

Reduce emphasis on information that is less relevant to
the target role.

Do NOT remove important factual career history merely
because it is less relevant.

Avoid:

- vague self-promotion
- generic claims
- unnecessary adjectives
- repetitive statements
- empty buzzwords
- exaggerated seniority
- unsupported leadership claims

The candidate should sound credible, capable and
professionally positioned — never artificially impressive.


============================================================
5. ATS OPTIMIZATION RULES
============================================================

Optimize terminology for Applicant Tracking Systems.

Use important terminology from the job description ONLY
when the underlying qualification is supported by the CV.

When the CV and job description describe the same factual
skill using different terminology, you may use the
job-description terminology if it preserves the original meaning.

Example:

CV:
"Spring Boot"

Job Description:
"Java / Spring Boot Backend Development"

Allowed:
Use wording that naturally emphasizes Java and Spring Boot
if both are already supported by the CV.

Not allowed:

Job Description:
"Kubernetes"

CV:
No Kubernetes evidence

Result:
Do NOT add Kubernetes.

Never perform keyword stuffing.

Do not repeatedly insert keywords unnaturally.

ATS optimization must improve semantic relevance without
reducing readability.


============================================================
6. PROFESSIONAL SUMMARY
============================================================

Write a highly targeted professional summary.

Requirements:

- maximum 3 concise sentences
- clearly aligned with the target role
- mention the strongest relevant technical qualifications
- reflect the candidate's real career level
- avoid generic phrases
- avoid unsupported years of experience
- avoid exaggerated seniority
- avoid first-person language
- do not simply repeat the skills section

The summary should quickly explain why the candidate is
relevant to THIS job.


============================================================
7. SKILLS
============================================================

Return a maximum of 12 skills.

Include ONLY skills explicitly supported by the original CV.

Prioritize:

1. skills directly relevant to the target job
2. important technical skills
3. supporting tools and technologies
4. other relevant professional skills

Do not add skills only because they appear in the
job description.

Prefer specific technical terminology over vague categories
when the CV supports the specific terminology.


============================================================
8. PROFESSIONAL EXPERIENCE
============================================================

Return a maximum of 4 relevant experience entries.

PRESERVE:

- actual job title
- actual company
- actual location
- actual dates

Never rename historical positions merely to make them
match the target role.

For each experience:

- maximum 3 bullet points
- prioritize relevant responsibilities
- use strong professional action-oriented language
- keep bullets concise
- preserve factual meaning
- emphasize technical or business relevance where supported
- remove unnecessary repetition

Whenever supported by the CV, prefer this structure:

ACTION + CONTEXT + RESULT OR PURPOSE

However:

NEVER invent measurable results.

If the original CV does not contain a metric,
do not create one.

Do not convert ordinary responsibilities into
unsupported achievements.


============================================================
9. PROJECTS
============================================================

Return a maximum of 3 projects.

Prioritize projects that provide evidence for requirements
in the target job.

For each project:

- preserve the real project title
- maximum 2 concise bullet points
- maximum 6 technologies
- include only technologies supported by the original CV
- emphasize architecture, implementation or technical
  relevance where supported

Projects may be especially important when they demonstrate
technical skills that are not strongly represented in
professional experience.


============================================================
10. EDUCATION
============================================================

Return a maximum of 3 education entries.

Preserve factual:

- degree
- field of study
- institution
- location
- dates
- grade

Do not alter degrees or fields of study to match the job.

Prioritize relevant education without changing its meaning.


============================================================
11. LANGUAGES AND CERTIFICATIONS
============================================================

Languages:
Maximum 6 entries.

Certifications:
Maximum 5 entries.

Only include information supported by the original CV.

Never upgrade language proficiency.

Never create certifications from skills, courses or
job-description requirements.


============================================================
12. WRITING QUALITY
============================================================

Use professional resume language appropriate for the
language of the original CV and target job.

Prefer:

- concise sentences
- strong action verbs
- clear technical terminology
- specific factual descriptions
- consistent terminology
- natural ATS keywords
- recruiter-friendly phrasing

Avoid:

- clichés
- excessive adjectives
- marketing language
- generic AI-generated phrases
- repetitive wording
- keyword stuffing
- overly long sentences
- unsupported claims

Do not make the resume sound artificially senior.


============================================================
13. JOB TITLES
============================================================

Historical job titles must remain factually accurate.

Never rename a historical position solely to improve
ATS matching.

targetRole is different.

targetRole represents the position currently being targeted
and may therefore use the target position from the job
description.


============================================================
14. FINAL FACT-CHECK
============================================================

Before returning the final JSON, internally verify EVERY
factual statement against the ORIGINAL CV.

Ask internally for every claim:

"Can this statement be supported by the original CV?"

If NO:
remove it.

Check especially:

- technologies
- skills
- employers
- dates
- education
- responsibilities
- achievements
- certifications
- metrics
- project technologies

If uncertain, prefer omission over fabrication.


============================================================
15. MATCH SCORE FIELDS
============================================================

The fields:

originalMatchScore
optimizedMatchScore

are NOT authoritative AI scores.

CareerInDe calculates the real match scores independently
using its own scoring engine.

Return 0 for both fields.

Do NOT estimate or inflate match scores.


============================================================
16. OUTPUT REQUIREMENTS
============================================================

Return ONLY the structured JSON required by the provided
response schema.

Do not include:

- explanations
- markdown
- comments
- analysis
- recommendations outside the schema

Respect these limits:

professionalSummary:
maximum 3 concise sentences

skills:
maximum 12 items

experiences:
maximum 4 entries

experience bulletPoints:
maximum 3 per experience

education:
maximum 3 entries

projects:
maximum 3 entries

project bulletPoints:
maximum 2 per project

project technologies:
maximum 6 per project

languages:
maximum 6 entries

certifications:
maximum 5 entries


============================================================
ORIGINAL CV
============================================================

""" + cvText + """


============================================================
TARGET JOB DESCRIPTION
============================================================

""" + jobDescription;
    }


    // =========================================================
    // RESUME JSON SCHEMA
    // =========================================================

    private Map<String, Object> buildResumeSchema() {

        Map<String, Object> stringSchema =
                Map.of(
                        "type",
                        "string"
                );

        Map<String, Object> integerSchema =
                Map.of(
                        "type",
                        "integer"
                );

        Map<String, Object> stringArraySchema =
                Map.of(
                        "type",
                        "array",
                        "items",
                        stringSchema
                );


        // =====================================================
        // EXPERIENCE
        // =====================================================

        Map<String, Object> experienceSchema =
                Map.of(
                        "type",
                        "object",

                        "properties",
                        Map.of(
                                "jobTitle",
                                stringSchema,

                                "company",
                                stringSchema,

                                "location",
                                stringSchema,

                                "startDate",
                                stringSchema,

                                "endDate",
                                stringSchema,

                                "bulletPoints",
                                stringArraySchema
                        ),

                        "required",
                        List.of(
                                "jobTitle",
                                "company",
                                "location",
                                "startDate",
                                "endDate",
                                "bulletPoints"
                        )
                );


        // =====================================================
        // EDUCATION
        // =====================================================

        Map<String, Object> educationSchema =
                Map.of(
                        "type",
                        "object",

                        "properties",
                        Map.of(
                                "degree",
                                stringSchema,

                                "fieldOfStudy",
                                stringSchema,

                                "institution",
                                stringSchema,

                                "location",
                                stringSchema,

                                "startDate",
                                stringSchema,

                                "endDate",
                                stringSchema,

                                "grade",
                                stringSchema
                        ),

                        "required",
                        List.of(
                                "degree",
                                "fieldOfStudy",
                                "institution",
                                "location",
                                "startDate",
                                "endDate",
                                "grade"
                        )
                );


        // =====================================================
        // PROJECT
        // =====================================================

        Map<String, Object> projectSchema =
                Map.of(
                        "type",
                        "object",

                        "properties",
                        Map.of(
                                "title",
                                stringSchema,

                                "description",
                                stringSchema,

                                "technologies",
                                stringArraySchema,

                                "bulletPoints",
                                stringArraySchema
                        ),

                        "required",
                        List.of(
                                "title",
                                "description",
                                "technologies",
                                "bulletPoints"
                        )
                );


        // =====================================================
        // COMPLETE RESUME
        // =====================================================

        return Map.ofEntries(

                Map.entry(
                        "type",
                        "object"
                ),

                Map.entry(
                        "properties",

                        Map.ofEntries(

                                Map.entry(
                                        "fullName",
                                        stringSchema
                                ),

                                Map.entry(
                                        "targetRole",
                                        stringSchema
                                ),

                                Map.entry(
                                        "email",
                                        stringSchema
                                ),

                                Map.entry(
                                        "phone",
                                        stringSchema
                                ),

                                Map.entry(
                                        "location",
                                        stringSchema
                                ),

                                Map.entry(
                                        "linkedin",
                                        stringSchema
                                ),

                                Map.entry(
                                        "github",
                                        stringSchema
                                ),

                                Map.entry(
                                        "professionalSummary",
                                        stringSchema
                                ),

                                Map.entry(
                                        "skills",
                                        stringArraySchema
                                ),

                                Map.entry(
                                        "experiences",
                                        Map.of(
                                                "type",
                                                "array",
                                                "items",
                                                experienceSchema
                                        )
                                ),

                                Map.entry(
                                        "education",
                                        Map.of(
                                                "type",
                                                "array",
                                                "items",
                                                educationSchema
                                        )
                                ),

                                Map.entry(
                                        "projects",
                                        Map.of(
                                                "type",
                                                "array",
                                                "items",
                                                projectSchema
                                        )
                                ),

                                Map.entry(
                                        "languages",
                                        stringArraySchema
                                ),

                                Map.entry(
                                        "certifications",
                                        stringArraySchema
                                ),

                                Map.entry(
                                        "originalMatchScore",
                                        integerSchema
                                ),

                                Map.entry(
                                        "optimizedMatchScore",
                                        integerSchema
                                )
                        )
                ),

                Map.entry(
                        "required",

                        List.of(
                                "fullName",
                                "targetRole",
                                "email",
                                "phone",
                                "location",
                                "linkedin",
                                "github",
                                "professionalSummary",
                                "skills",
                                "experiences",
                                "education",
                                "projects",
                                "languages",
                                "certifications",
                                "originalMatchScore",
                                "optimizedMatchScore"
                        )
                )
        );
    }


    // =========================================================
    // INPUT VALIDATION
    // =========================================================

    private void validateInput(
            String cvText,
            String jobDescription) {

        if (cvText == null ||
                cvText.isBlank()) {

            throw new IllegalArgumentException(
                    "CV text cannot be empty."
            );
        }

        if (jobDescription == null ||
                jobDescription.isBlank()) {

            throw new IllegalArgumentException(
                    "Job description cannot be empty."
            );
        }
    }


    // =========================================================
    // TEXT LIMIT
    // =========================================================

    private String limitText(
            String text,
            int maxLength) {

        if (text == null) {
            return "";
        }

        String cleaned =
                text
                        .replace(
                                "\u0000",
                                ""
                        )
                        .trim();

        if (cleaned.length() <= maxLength) {
            return cleaned;
        }

        return cleaned.substring(
                0,
                maxLength
        );
    }
}