package com.careerinde.careerinde_app.resume.optimization;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.careerinde.careerinde_app.ai.OpenAIService;
import com.careerinde.careerinde_app.ai.gemini.GeminiAIService;

@Service
public class GeminiResumeAIService {

    private final GeminiAIService geminiAIService;
    private final OpenAIService groqService;

    private static final int MAX_CV_LENGTH = 6500;
    private static final int MAX_JOB_DESCRIPTION_LENGTH = 3000;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public GeminiResumeAIService(
            GeminiAIService geminiAIService,
            OpenAIService groqService) {

        this.geminiAIService = geminiAIService;
        this.groqService = groqService;
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
                "Primary Provider: Gemini"
        );
        System.out.println(
                "Fallback Provider: Groq"
        );
        System.out.println(
                "Gemini Model: "
                        + geminiAIService.getModel()
        );
        System.out.println(
                "=========================================="
        );


        // =====================================================
        // PRIMARY PROVIDER: GEMINI
        // =====================================================

        try {

            long start =
                    System.currentTimeMillis();


            String result =
                    geminiAIService.generateJson(
                            prompt,
                            schema,
                            0.1,
                            3500
                    );


            long duration =
                    System.currentTimeMillis()
                            - start;


            System.out.println();
            System.out.println(
                    "=========================================="
            );
            System.out.println(
                    "RESUME PROVIDER: GEMINI"
            );
            System.out.println(
                    "Generation Time: "
                            + duration
                            + " ms"
            );
            System.out.println(
                    "=========================================="
            );


            return result;


        } catch (Exception geminiException) {


            // =================================================
            // GEMINI FAILED -> GROQ FALLBACK
            // =================================================

            System.err.println();
            System.err.println(
                    "=========================================="
            );
            System.err.println(
                    "GEMINI RESUME OPTIMIZATION FAILED"
            );
            System.err.println(
                    "Reason: "
                            + geminiException.getMessage()
            );
            System.err.println(
                    "Switching to Groq fallback..."
            );
            System.err.println(
                    "=========================================="
            );


            try {

                long start =
                        System.currentTimeMillis();


                String result =
                        groqService.sendPrompt(
                                prompt,
                                3000
                        );


                long duration =
                        System.currentTimeMillis()
                                - start;


                System.out.println();
                System.out.println(
                        "=========================================="
                );
                System.out.println(
                        "RESUME PROVIDER: GROQ FALLBACK"
                );
                System.out.println(
                        "Generation Time: "
                                + duration
                                + " ms"
                );
                System.out.println(
                        "=========================================="
                );


                return result;


            } catch (Exception groqException) {


                System.err.println();
                System.err.println(
                        "=========================================="
                );
                System.err.println(
                        "ALL RESUME AI PROVIDERS FAILED"
                );
                System.err.println(
                        "Gemini: "
                                + geminiException.getMessage()
                );
                System.err.println(
                        "Groq: "
                                + groqException.getMessage()
                );
                System.err.println(
                        "=========================================="
                );


                throw new IllegalStateException(
                        "AI resume optimization is temporarily unavailable.",
                        groqException
                );
            }
        }
    }


    // =========================================================
    // COMPACT PROFESSIONAL RESUME PROMPT
    // =========================================================

    private String buildPrompt(
            String cvText,
            String jobDescription) {

        return """
You are CareerInDe's professional ATS resume optimization engine.

Optimize the candidate's EXISTING CV for the supplied job.

GOALS:
- maximize job relevance
- improve ATS compatibility
- improve recruiter readability
- preserve factual accuracy
- produce concise professional content


FACTUAL SAFETY:

Use ONLY candidate facts supported by the ORIGINAL CV.

Never invent or assume:
- skills or technologies
- employers or job titles
- responsibilities
- dates or locations
- education or certifications
- projects
- achievements
- metrics or numbers
- years of experience
- language proficiency

The JOB DESCRIPTION is NOT evidence about the candidate.

A skill or requirement from the job description may be used
ONLY when the original CV supports it.

Never transfer a skill from one CV context into another.

For example:
If Spring Boot appears only in a project or skills section,
do NOT claim it was used at a specific employer unless the
CV explicitly supports that connection.

If information is uncertain or missing, omit it.


JOB MATCHING:

Internally identify the most important:
- responsibilities
- required skills
- preferred skills
- technologies
- ATS keywords
- education and experience requirements

Then match them against evidence in the CV.

Prioritize supported qualifications that are most relevant
to the target job.

Do not output this analysis.


ATS RULES:

Use important job-description terminology naturally when
the same qualification is supported by the CV.

Do not keyword-stuff.

Do not add missing requirements.

Keep standard, clear resume terminology.


SUMMARY:

Maximum 3 concise sentences.

Make it specific to the target role.

Highlight the strongest supported qualifications.

Do not claim a completed degree when the CV shows that the
degree is still in progress.

Do not invent years of experience or seniority.


SKILLS:

Maximum 12.

Use only CV-supported skills.

Order the most job-relevant skills first.


EXPERIENCE:

Maximum 4 entries.

Preserve actual:
- job title
- company
- location
- dates

Maximum 3 bullet points per experience.

Improve wording and relevance, but preserve factual meaning.

Never move technologies, responsibilities or achievements
between different experiences unless explicitly supported.

Never invent measurable results.


PROJECTS:

Maximum 3 projects.

Maximum 2 bullet points per project.

Maximum 6 technologies per project.

Use projects to demonstrate relevant technical evidence.

Only include technologies supported by the original CV.


EDUCATION:

Maximum 3 entries.

Preserve degree status, institution, field, dates,
location and grade exactly according to the CV.

Never represent an ongoing degree as completed.


LANGUAGES AND CERTIFICATIONS:

Languages: maximum 6.
Certifications: maximum 5.

Never upgrade proficiency or invent certifications.


WRITING:

Use professional, concise, recruiter-friendly language.

Avoid:
- clichés
- generic AI language
- exaggerated claims
- repetition
- keyword stuffing
- artificial seniority

Use the primary professional language appropriate to the
original CV and target job.


FINAL FACT CHECK:

Before returning the result, verify every factual claim
against the ORIGINAL CV.

Especially verify:
- skills
- technologies
- experience attribution
- employers
- job titles
- dates
- degree status
- certifications
- achievements
- metrics

If a statement cannot be directly supported, remove it.


MATCH SCORES:

Return:

originalMatchScore = 0
optimizedMatchScore = 0

CareerInDe calculates the real scores separately.


OUTPUT:

Return ONLY valid JSON matching this exact structure:

{
  "fullName": "",
  "targetRole": "",
  "email": "",
  "phone": "",
  "location": "",
  "linkedin": "",
  "github": "",
  "professionalSummary": "",
  "skills": [],
  "experiences": [
    {
      "jobTitle": "",
      "company": "",
      "location": "",
      "startDate": "",
      "endDate": "",
      "bulletPoints": []
    }
  ],
  "education": [
    {
      "degree": "",
      "fieldOfStudy": "",
      "institution": "",
      "location": "",
      "startDate": "",
      "endDate": "",
      "grade": ""
    }
  ],
  "projects": [
    {
      "title": "",
      "description": "",
      "technologies": [],
      "bulletPoints": []
    }
  ],
  "languages": [],
  "certifications": [],
  "originalMatchScore": 0,
  "optimizedMatchScore": 0
}

No markdown.
No explanations.
No comments.
No code fences.

The first character must be {
The last character must be }


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


        if (cleaned.length()
                <= maxLength) {

            return cleaned;
        }


        return cleaned.substring(
                0,
                maxLength
        );
    }
}