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
    // PROMPT
    // =========================================================

    private String buildPrompt(
            String cvText,
            String jobDescription) {

        return """
You are CareerInDe's professional ATS resume optimization engine.

Your task is to optimize an EXISTING candidate resume
for a specific target job.

FACTUAL ACCURACY IS MANDATORY.

You MUST NOT invent information.

Never invent:
- skills
- technologies
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

ONLY use facts contained in the ORIGINAL CV.

The target job description may ONLY be used for:
- relevance
- wording
- ordering
- emphasis
- ATS keyword positioning

A requirement from the job description MUST NOT be added
to the resume unless it is supported by the original CV.

You may:
- improve wording
- improve clarity
- improve professional tone
- improve ATS relevance
- reorder existing information
- emphasize relevant qualifications
- rewrite existing bullet points
- make existing experience more concise

You MUST preserve factual meaning.

If information does not exist in the original CV,
return an empty string or empty array.

Never guess.

Never fabricate metrics.

Do NOT turn keywords from the job description into
candidate experience.

JOB TITLES:

Preserve the candidate's actual job titles.

Do not create a new historical job title merely to
increase ATS relevance.

TARGET ROLE:

targetRole represents the position being targeted,
not a fabricated historical position.


CONTENT LIMITS:

professionalSummary:
Maximum 3 concise sentences.

skills:
Maximum 12 skills.
Include only skills supported by the CV.
Prioritize skills relevant to the target job.

experiences:
Maximum 4 relevant entries.

experience bulletPoints:
Maximum 3 concise bullet points per experience.

education:
Maximum 3 entries.

projects:
Maximum 3 relevant projects.

project bulletPoints:
Maximum 2 bullet points per project.

project technologies:
Maximum 6 technologies.

languages:
Maximum 6 entries.

certifications:
Maximum 5 entries.


MATCH SCORE:

originalMatchScore and optimizedMatchScore must be
integers from 0 to 100.

These scores are temporary AI estimates.

Do not inflate the optimized score because of invented
qualifications.

The optimized score may improve only because of:
- clearer wording
- better organization
- better keyword positioning
- stronger emphasis on existing relevant qualifications.


================ ORIGINAL CV ================

""" + cvText + """


================ TARGET JOB DESCRIPTION ================

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